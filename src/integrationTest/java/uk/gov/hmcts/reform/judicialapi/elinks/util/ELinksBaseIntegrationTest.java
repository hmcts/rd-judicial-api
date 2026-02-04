package uk.gov.hmcts.reform.judicialapi.elinks.util;

import com.github.tomakehurst.wiremock.extension.ResponseTransformerV2;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.launchdarkly.sdk.server.LDClient;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.reform.judicialapi.configuration.RestTemplateConfiguration;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinksResponsesRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.audit.AppointmentsRepositoryAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.audit.AuthorisationsRepositoryAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.audit.JudicialRoleTypeRepositoryAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.audit.ProfileRepositoryAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.scheduler.ElinksApiJobScheduler;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.impl.ELinksServiceImpl;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.service.impl.FeatureToggleServiceImpl;
import uk.gov.hmcts.reform.judicialapi.util.SpringBootIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.versions.V2;
import uk.gov.hmcts.reform.judicialapi.wiremock.WireMockExtension;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.util.JwtTokenUtil.decodeJwtToken;
import static uk.gov.hmcts.reform.judicialapi.util.JwtTokenUtil.getUserIdAndRoleFromToken;

@Configuration
@WithTags({@WithTag("testType:Integration")})
@ExtendWith(SerenityJUnit5Extension.class)
@TestPropertySource(properties = {"S2S_URL=http://127.0.0.1:8990", "IDAM_URL:http://127.0.0.1:5000"})
@ContextConfiguration(classes = {RestTemplateConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public abstract class ELinksBaseIntegrationTest extends SpringBootIntegrationTest {

    public static final String RESPONSE_BODY_ERROR_MSG = "errorMessage";
    public static final String RESPONSE_BODY_MSG_KEY = "message";
    protected static final String USER_PASSWORD = "user:password";
    protected static final String JUDICIAL_REF_DATA_ELINKS = "judicial-ref-data-elinks";
    private static final String IDAM_SEARCHUSERS = "/api/v1/users";
    @RegisterExtension
    protected static final WireMockExtension s2sService = new WireMockExtension(8990);
    @RegisterExtension
    protected static final WireMockExtension sidamService = new WireMockExtension(5000, new JudicialTransformer());
    @RegisterExtension
    protected static final WireMockExtension mockHttpServerForOidc = new WireMockExtension(7000);
    @RegisterExtension
    protected static final WireMockExtension elinks = new WireMockExtension(8000);
    @MockitoBean
    protected FeatureToggleServiceImpl featureToggleServiceImpl;
    @MockitoBean
    protected JwtDecoder jwtDecoder;
    protected ElinksReferenceDataClient elinksReferenceDataClient;
    @Autowired
    protected AppointmentsRepository appointmentsRepository;
    @Autowired
    protected ProfileRepository profileRepository;
    @Autowired
    protected AuthorisationsRepository authorisationsRepository;
    @Autowired
    protected JudicialRoleTypeRepository judicialRoleTypeRepository;
    @Autowired
    protected BaseLocationRepository baseLocationRepository;
    @Autowired
    protected ElinksResponsesRepository elinksResponsesRepository;
    @Autowired
    protected ElinkSchedularAuditRepository elinkSchedularAuditRepository;
    @Autowired
    protected DataloadSchedulerJobRepository dataloadSchedulerJobRepository;
    @Autowired
    protected ElinkDataExceptionRepository elinkDataExceptionRepository;
    @Autowired
    protected ElinksApiJobScheduler elinksApiJobScheduler;
    @Autowired
    protected PublishSidamIdService publishSidamIdService;
    @Autowired
    protected DataloadSchedulerJobAudit dataloadSchedulerJobAudit;
    @Autowired
    protected ELinksServiceImpl elinksServiceImpl;
    @Autowired
    protected JudicialRoleTypeRepositoryAudit judicialRoleTypeRepositoryAudit;
    @Autowired
    protected AuthorisationsRepositoryAudit authorisationsRepositoryAudit;
    @Autowired
    protected AppointmentsRepositoryAudit appointmentsRepositoryAudit;
    @Autowired
    protected ProfileRepositoryAudit profileRepositoryAudit;
    @MockitoBean
    protected ElinkTopicPublisher elinkTopicPublisher;
    @MockitoBean
    protected IdamTokenConfigProperties idamTokenConfigProperties;
    @Value("${elinks.cleanElinksResponsesDays}")
    protected Long cleanElinksResponsesDays;
    @MockitoBean
    private LDClient ldClient;
    @Autowired
    private Flyway flyway;
    @Value("${oidc.expiration}")
    private long expiration;
    @Value("${oidc.issuer}")
    private String issuer;
    @Value("${idam.s2s-authorised.services}")
    private String serviceName;

    @BeforeAll
    public void setUpClient() {
        ElinksReferenceDataClient.setIdamAuthToken("");
        elinksReferenceDataClient = new ElinksReferenceDataClient(port, issuer, expiration, serviceName);
        when(featureToggleServiceImpl.isFlagEnabled(anyString())).thenReturn(true);
        flyway.clean();
        flyway.migrate();
    }

    protected void stubPeopleApiResponse(final String peopleApiResponseJson,
                                         final HttpStatus httpStatus) {

        elinks.stubFor(get(urlPathMatching("/people"))
                .willReturn(aResponse()
                        .withStatus(httpStatus.value())
                        .withHeader("Content-Type", V2.MediaType.SERVICE)
                        .withHeader("Connection", "close")
                        .withBody(peopleApiResponseJson)));
    }

    protected void stubS2SResponse() {
        s2sService.stubFor(get(urlEqualTo("/details"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("rd_judicial_api")));
    }

    protected void stubIdamUserInfoResponse() {
        sidamService.stubFor(get(urlPathMatching("/o/userinfo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("""
                                  {
                                      "id": "%s",
                                      "uid": "%s",
                                      "forename": "Super",
                                      "surname": "User",
                                      "email": "super.user@hmcts.net",
                                      "accountStatus": "active",
                                      "roles": [ "%s" ]
                                  }
                                  """)
                        .withTransformers("user-token-response")));
    }

    protected void stubIdamResponse(final String idamResponseValidationJson,
                                    final HttpStatus httpStatus) {
        if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
            sidamService.stubFor(get(urlPathMatching(IDAM_SEARCHUSERS))
                    .willReturn(serverError()
                            .withStatus(httpStatus.value())
                            .withHeader("Content-Type", "application/json")
                            .withBody("Internal server error")
                    ));
        } else {
            sidamService.stubFor(get(urlPathMatching(IDAM_SEARCHUSERS))
                    .willReturn(aResponse()
                            .withStatus(httpStatus.value())
                            .withHeader("Content-Type", "application/json")
                            .withHeader("Connection", "close")
                            .withBody(idamResponseValidationJson)
                    ));
        }
    }

    protected void stubIdamElasticSearchResponse(final String idamResponseValidationJson,
                                                 final int pageNumber,
                                                 final HttpStatus httpStatus) {
        if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
            sidamService.stubFor(get(urlPathMatching(IDAM_SEARCHUSERS))
                    .willReturn(serverError()
                            .withStatus(httpStatus.value())
                            .withHeader("Content-Type", "application/json")
                            .withBody("Internal server error")
                    ));
        } else {
            sidamService.stubFor(get(urlPathMatching(IDAM_SEARCHUSERS))
                    .withId(UUID.randomUUID())
                    .withQueryParam("page", equalTo(String.valueOf(pageNumber)))
                    .withQueryParam("size", equalTo("4"))
                    .withQueryParam("query", equalTo("(roles:judiciary) AND lastModified:>now-12h"))
                    .willReturn(aResponse()
                            .withStatus(httpStatus.value())
                            .withHeader("Content-Type", "application/json")
                            .withHeader("Connection", "close")
                            .withBody(idamResponseValidationJson)));
        }
    }

    protected void stubIdamTokenResponse(final HttpStatus httpStatus) {
        sidamService.stubFor(post(urlPathMatching("/o/token"))
                .willReturn(aResponse()
                        .withStatus(httpStatus.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("""
                                  {
                                  "access_token": "12345"
                                   }
                                   """
                        )
                ));
    }

    protected void stubLocationApiResponse(final String locationApiResponseJson,
                                           final HttpStatus httpStatus) {

        elinks.stubFor(get(urlPathMatching("/reference_data/location"))
                .willReturn(aResponse()
                        .withStatus(httpStatus.value())
                        .withHeader("Content-Type", V2.MediaType.SERVICE)
                        .withHeader("Connection", "close")
                        .withBody(locationApiResponseJson)));
    }

    protected void stubDeletedApiResponse(final String deletedApiResponseJson,
                                          final HttpStatus httpStatus) {

        elinks.stubFor(get(urlPathMatching("/deleted"))
                .willReturn(aResponse()
                        .withStatus(httpStatus.value())
                        .withHeader("Content-Type", V2.MediaType.SERVICE)
                        .withHeader("Connection", "close")
                        .withBody(deletedApiResponseJson)));
    }

    protected void stubLeaversApiResponse(final String leaversApiResponseJson,
                                          final HttpStatus httpStatus) {

        elinks.stubFor(get(urlPathMatching("/leavers"))
                .willReturn(aResponse()
                        .withStatus(httpStatus.value())
                        .withHeader("Content-Type", V2.MediaType.SERVICE)
                        .withHeader("Connection", "close")
                        .withBody(leaversApiResponseJson)));
    }

    public static class JudicialTransformer implements ResponseTransformerV2 {

        @Override
        public Response transform(Response response, ServeEvent serveEvent) {

            String formatResponse = response.getBodyAsString();

            String token = serveEvent.getRequest().header("Authorization").firstValue();
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

        @Override
        public boolean applyGlobally() {
            return false;
        }
    }
}



