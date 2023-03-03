package uk.gov.hmcts.reform.judicialapi.elinks.util;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.launchdarkly.sdk.server.LDClient;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.judicialapi.configuration.RestTemplateConfiguration;
import uk.gov.hmcts.reform.judicialapi.service.impl.FeatureToggleServiceImpl;
import uk.gov.hmcts.reform.judicialapi.util.SpringBootIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.wiremock.WireMockExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.util.JwtTokenUtil.decodeJwtToken;
import static uk.gov.hmcts.reform.judicialapi.util.JwtTokenUtil.getUserIdAndRoleFromToken;
import static uk.gov.hmcts.reform.judicialapi.util.KeyGenUtil.getDynamicJwksResponse;

@Configuration
@WithTags({@WithTag("testType:Integration")})
@TestPropertySource(properties = {"S2S_URL=http://127.0.0.1:8990", "IDAM_URL:http://127.0.0.1:5000"})
@ContextConfiguration(classes = {RestTemplateConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public abstract class ElinksEnabledIntegrationTest extends SpringBootIntegrationTest {

    @MockBean
    protected FeatureToggleServiceImpl featureToggleServiceImpl;

    @MockBean
    LDClient ldClient;

    protected ElinksReferenceDataClient elinksReferenceDataClient;

    @Value("${oidc.expiration}")
    private long expiration;
    @Value("${oidc.issuer}")
    private String issuer;
    @Value("${idam.s2s-authorised.services}")
    private String serviceName;

    @RegisterExtension
    protected final WireMockExtension s2sService = new WireMockExtension(8990);

    @RegisterExtension
    protected final WireMockExtension sidamService = new WireMockExtension(5000, new JudicialTransformer());

    @RegisterExtension
    protected final WireMockExtension mockHttpServerForOidc = new WireMockExtension(7000);

    @RegisterExtension
    protected final WireMockExtension elinks = new WireMockExtension(8000);


    @Autowired
    Flyway flyway;

    @BeforeAll
    public void setUpClient() {
        ElinksReferenceDataClient.setBearerToken("");
        elinksReferenceDataClient = new ElinksReferenceDataClient(port, issuer, expiration, serviceName);
        when(featureToggleServiceImpl.isFlagEnabled(anyString())).thenReturn(true);
        flyway.clean();
        flyway.migrate();
    }

    @BeforeAll
    public void setupIdamStubs() throws Exception {

        s2sService.stubFor(get(urlEqualTo("/details"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("rd_judicial_api")));

        sidamService.stubFor(get(urlPathMatching("/o/userinfo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("{"
                                + "  \"id\": \"%s\","
                                + "  \"uid\": \"%s\","
                                + "  \"forename\": \"Super\","
                                + "  \"surname\": \"User\","
                                + "  \"email\": \"super.user@hmcts.net\","
                                + "  \"accountStatus\": \"active\","
                                + "  \"roles\": ["
                                + "  \"%s\""
                                + "  ]"
                                + "}")
                        .withTransformers("user-token-response")));

        mockHttpServerForOidc.stubFor(get(urlPathMatching("/jwks"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(getDynamicJwksResponse())));


        elinks.stubFor(get(urlPathMatching("/people"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("{"
                                + "    \"pagination\": {"
                                + "      \"results\": 1,"
                                + "      \"pages\": 1,"
                                + "      \"current_page\": 1,"
                                + "      \"results_per_page\": 1,"
                                + "      \"more_pages\": false"
                                + "  },"
                                + "  \"results\": ["
                                + "      {"
                                + "          \"id\": \"552da697-4b3d-4aed-9c22-1e903b70aead\","
                                + "          \"per_id\": 57818,"
                                + "          \"personal_code\": \"0049931063\","
                                + "          \"title\": \"Tribunal Judge\","
                                + "          \"known_as\": \"Tester\","
                                + "          \"surname\": \"TestAccount 2\","
                                + "          \"fullname\": \"Tribunal Judge Tester TestAccount 2\","
                                + "          \"post_nominals\": \"ABC\","
                                + "      \"email\": \"Tester2@judiciarystaging.onmicrosoft.com\","
                                + "          \"sex\": \"sex_unknown\","
                                + "          \"work_phone\": null,"
                                + "          \"disability\": false,"
                                + "          \"retirement_date\": null,"
                                + "          \"leaving_on\": null,"
                                + "          \"appointments\": ["
                                + "              {"
                                + "                  \"appointment_id\": 114325,"
                                + "                  \"role\": \"Tribunal Judge\","
                                + "                  \"role_name\": \"Tribunal Judge\","
                                + "                  \"role_name_id\": null,"
                                + "                  \"type\": \"Courts\","
                                + "                  \"court_name\": \"Worcester Combined Court - Crown\","
                                + "                  \"court_type\": \"Crown Court\","
                                + "                  \"circuit\": \"default\","
                                + "                  \"bench\": null,"
                                + "                  \"advisory_committee_area\": null,"
                                + "                  \"location\": \"default\","
                                + "                  \"base_location\": \"Worcester Combined Court - Crown\","
                                + "                  \"base_location_id\": 0,"
                                + "                  \"is_principal\": false,"
                                + "                  \"start_date\": \"2022-06-29\","
                                + "                  \"end_date\": null,"
                                + "                  \"superseded\": true,"
                                + "                  \"contract_type\": \"fee_paid\","
                                + "                  \"contract_type_id\": 1,"
                                + "                  \"work_pattern\": \"Fee Paid Judiciary 5 Days Mon - Fri\","
                                + "                  \"work_pattern_id\": 10,"
                                + "                  \"fte_percent\": 100"
                                + "              },"
                                + "              {"
                                + "                  \"appointment_id\": 114329,"
                                + "                  \"role\": \"Magistrate\","
                                + "                  \"role_name\": \"Magistrate\","
                                + "                  \"role_name_id\": null,"
                                + "                   \"type\": \"Courts\","
                                + "                    \"court_name\": \"Central London County Court\","
                                + "                   \"court_type\": \"County Court\","
                                + "                  \"circuit\": \"default\","
                                + "                  \"bench\": null,"
                                + "                  \"advisory_committee_area\": null,"
                                + "                  \"location\": \"default\","
                                + "                  \"base_location\": \"Central London County Court\","
                                + "                  \"base_location_id\": 0,"
                                + "                  \"is_principal\": true,"
                                + "                  \"start_date\": \"2022-07-27\","
                                + "                  \"end_date\": null,"
                                + "                  \"superseded\": false,"
                                + "                  \"contract_type\": \"salaried\","
                                + "                  \"contract_type_id\": 0,"
                                + "                  \"work_pattern\": \"Fee Paid Judiciary 5 Days Mon - Fri\","
                                + "                  \"work_pattern_id\": 10,"
                                + "                  \"fte_percent\": 100"
                                + "              }"
                                + "          ],"
                                + "          \"training_records\": [],"
                                + "          \"authorisations\": ["
                                + "              {"
                                + "                  \"jurisdiction\": \"Family\","
                                + "                  \"tickets\": ["
                                + "                      \"Private Law\""
                                + "                  ]"
                                + "              },"
                                + "              {"
                                + "                  \"jurisdiction\": \"Tribunals\","
                                + "                  \"tickets\": ["
                                + "                      \"05 - Industrial Injuries\""
                                + "                  ]"
                                + "              }"
                                + "          ],"
                                + "          \"authorisations_with_dates\": ["
                                + "              {"
                                + "                  \"authorisation_id\": 29701,"
                                + "                  \"jurisdiction\": \"Family\","
                                + "                  \"jurisdiction_id\": 26,"
                                + "                  \"ticket\": \"Private Law\","
                                + "                  \"ticket_id\": 315,"
                                + "                  \"start_date\": \"2022-07-03\","
                                + "                 \"end_date\": null"
                                + "                },"
                                + "                {"
                                + "                \"authorisation_id\": 29700,"
                                + "                 \"jurisdiction\": \"Tribunals\","
                                + "                 \"jurisdiction_id\": 27,"
                                + "                 \"ticket\": \"05 - Industrial Injuries\","
                                + "                 \"ticket_id\": 367,"
                                + "                 \"start_date\": \"2022-07-04\","
                                + "                 \"end_date\": null"
                                + "                }"
                                + "            ]"
                                + "        }"
                                + "    ]"
                                + " }")
                        ));


        elinks.stubFor(get(urlPathMatching("/reference_data/location"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withHeader("Connection", "close")
                .withBody("{"
                    + " \"region_id\": \"0\","
                    + " \"region_desc_en\": \"default\","
                    + " \"region_desc_cy\": \"default\""
                    + " }")
                ));

        elinks.stubFor(get(urlPathMatching("/leavers"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("{"
                                + "   \"pagination\":{"
                                + "     \"results\":46,"
                                + "     \"pages\":1,"
                                + "     \"current_page\":1,"
                                + "     \"results_per_page\":50,"
                                + "     \"more_pages\":false"
                                + "   },"
                                + "   \"results\":["
                                + "     {"
                                + "       \"id\":\"d01b0b59-8d68-4463-9887-535989208e27\","
                                + "       \"per_id\":56787,"
                                + "       \"personal_code\":\"0049931063\","
                                + "       \"leaver\":true,"
                                + "       \"left_on\":\"2021-02-24\""
                                + "     }]"
                                + " }")
                       ));

        sidamService.stubFor(get(urlPathMatching("/api/v1/users"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("["
                                + "    {"
                                + "        \"id\": \"6455c84c-e77d-4c4f-9759-bf4a93a8e971\","
                                + "        \"forename\": \"Service\","
                                + "        \"surname\": \"Account\","
                                + "        \"email\": \"tester@hmcts.net\","
                                + "        \"active\": true,"
                                + "        \"locked\": false,"
                                + "        \"ssoId\": \"552da697-4b3d-4aed-9c22-1e903b70aead\","
                                + "        \"roles\": ["
                                + "            \"caseworker-privatelaw-systemupdate\","
                                + "            \"caseworker-privatelaw\","
                                + "            \"hearing-manager\","
                                + "            \"hearing-viewer\","
                                + "            \"jrd-admin\","
                                + "            \"listed-hearing-viewer\","
                                + "            \"idam-service-account\","
                                + "            \"judge\","
                                + "            \"caseworker\","
                                + "            \"judiciary\","
                                + "            \"jrd-system-user\","
                                + "            \"caseworker-privatelaw-courtadmin\""
                                + "        ],"
                                + "        \"lastModified\": \"2023-01-17T13:15:36.435Z\","
                                + "        \"createDate\": \"2022-12-02T11:54:55.212Z\""
                                + "    }"
                                + "]")
                        ));

        sidamService.stubFor(post(urlPathMatching("/o/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("{"
                                + "        \"access_token\": \"12345\""
                                + "    }")
                                ));

        elinks.stubFor(get(urlPathMatching("/reference_data/base_location"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withHeader("Connection", "close")
                .withBody("{"
                    + " \"base_location_id\": \"0\","
                    + " \"court_name\": \"default\","
                    + " \"court_type\": \"default\","
                    + " \"circuit\": \"default\","
                    + " \"area_of_expertise\": \"default\""
                    + " }")
                .withTransformers("user-token-response")));

    }

    @AfterEach
    public void cleanupTestData() {
        ElinksReferenceDataClient.setBearerToken("");
    }


    public static class JudicialTransformer extends ResponseTransformer {
        @Override
        public Response transform(Request request, Response response, FileSource files, Parameters parameters) {

            String formatResponse = response.getBodyAsString();

            String token = request.getHeader("Authorization");
            String tokenBody = decodeJwtToken(token.split(" ")[1]);
            var tokenInfo = getUserIdAndRoleFromToken(tokenBody);
            formatResponse = format(formatResponse, tokenInfo.get(1), tokenInfo.get(1), tokenInfo.get(0));

            return Response.Builder.like(response)
                    .but().body(formatResponse)
                    .build();
        }

        @Override
        public String getName() {
            return "user-token-response";
        }

        public boolean applyGlobally() {
            return false;
        }
    }
}



