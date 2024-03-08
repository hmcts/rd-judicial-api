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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.judicialapi.configuration.RestTemplateConfiguration;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.service.impl.FeatureToggleServiceImpl;
import uk.gov.hmcts.reform.judicialapi.util.SpringBootIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.versions.V2;
import uk.gov.hmcts.reform.judicialapi.wiremock.WireMockExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
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
    protected JwtDecoder jwtDecoder;

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
    protected IdamTokenConfigProperties tokenConfigProperties;


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

    public static String loadJson(String jsonFilePath) throws IOException {
        return Files.readString(Paths.get(jsonFilePath), UTF_8);
    }

    @BeforeAll
    public void setupIdamStubs() throws Exception {

        String locationResponseValidationJson =
                loadJson("src/integrationTest/resources/wiremock_responses/location.json");
        String baselocationResponseValidationJson =
                loadJson("src/integrationTest/resources/wiremock_responses/base_location.json");
        String peopleResponseValidationJson =
                loadJson("src/integrationTest/resources/wiremock_responses/people.json");
        String leaversResponseValidationJson =
                loadJson("src/integrationTest/resources/wiremock_responses/leavers.json");
        String deletedResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/deleted.json");

        elinks.stubFor(get(urlPathMatching("/reference_data/location"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", V2.MediaType.SERVICE)
                        .withHeader("Connection", "close")
                        .withBody(locationResponseValidationJson)));

        elinks.stubFor(get(urlPathMatching("/reference_data/base_location"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", V2.MediaType.SERVICE)
                        .withHeader("Connection", "close")
                        .withBody(baselocationResponseValidationJson)
                        .withTransformers("user-token-response")));

        elinks.stubFor(get(urlPathMatching("/people"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", V2.MediaType.SERVICE)
                        .withHeader("Connection", "close")
                        .withBody(peopleResponseValidationJson)));

        elinks.stubFor(get(urlPathMatching("/leavers"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", V2.MediaType.SERVICE)
                        .withHeader("Connection", "close")
                        .withBody(leaversResponseValidationJson)));

        elinks.stubFor(get(urlPathMatching("/deleted"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", V2.MediaType.SERVICE)
                .withHeader("Connection", "close")
                .withBody(deletedResponseValidationJson)));

        String idamResponseValidationJson =
                loadJson("src/integrationTest/resources/wiremock_responses/idamresponse.json");

        sidamService.stubFor(get(urlPathMatching("/api/v1/users"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(idamResponseValidationJson)
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

    protected void initialize() {
        byte[] base64UserDetails = Base64.getDecoder().decode("ZHVtbXl2YWx1ZUBobWN0cy5uZXQ6SE1DVFMxMjM0");
        byte[] clientAuth = Base64.getDecoder().decode("cmQteHl6LWFwaTp4eXo");
        tokenConfigProperties.setClientId("234342332");
        tokenConfigProperties.setClientAuthorization(new String(clientAuth));
        tokenConfigProperties.setAuthorization(new String(base64UserDetails));
        tokenConfigProperties.setRedirectUri("http://idam-api.aat.platform.hmcts.net");
        tokenConfigProperties.setUrl("http://127.0.0.1:5000");
    }
}



