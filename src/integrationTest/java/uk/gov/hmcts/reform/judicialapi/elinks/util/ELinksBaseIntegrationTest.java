package uk.gov.hmcts.reform.judicialapi.elinks.util;

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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

    @RegisterExtension
    protected static final WireMockExtension elinks = new WireMockExtension(8000);

    @MockitoBean
    protected FeatureToggleServiceImpl featureToggleServiceImpl;

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

    public static void stubLocationApiResponse(final String locationApiResponseJson,
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

}



