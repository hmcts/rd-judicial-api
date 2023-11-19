package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.scheduler.ElinksApiJobScheduler;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.judicialapi.util.KeyGenUtil.getDynamicJwksResponse;

public class PeopleTribunalsParentIdNullIntegrationTest extends ElinksEnabledIntegrationTest {


    @Autowired
    LocationRepository locationRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    JudicialRoleTypeRepository judicialRoleTypeRepository;
    @Autowired
    BaseLocationRepository baseLocationRepository;
    @Autowired
    AuthorisationsRepository authorisationsRepository;
    @Autowired
    AppointmentsRepository appointmentsRepository;
    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;


    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;

    @Autowired
    private ElinksApiJobScheduler elinksApiJobScheduler;

    @Autowired
    private DataloadSchedulerJobRepository dataloadSchedulerJobRepository;

    @Autowired
    PublishSidamIdService publishSidamIdService;

    @MockBean
    ElinkTopicPublisher elinkTopicPublisher;

    @Autowired
    ElinkDataExceptionRepository elinkDataExceptionRepository;

    @BeforeAll
    void loadElinksResponse() throws Exception {

        cleanupData();

        String locationResponseValidationJson =
                loadJson("src/integrationTest/resources/wiremock_responses/location_nullParentID.json");
        String peopleResponseValidationJson =
                loadJson("src/integrationTest/resources/wiremock_responses/People_ParentIDNull.json");


        elinks.stubFor(get(urlPathMatching("/reference_data/location"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", V2.MediaType.SERVICE)
                        .withHeader("Connection", "close")
                        .withBody(locationResponseValidationJson)));

        elinks.stubFor(get(urlPathMatching("/people"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", V2.MediaType.SERVICE)
                        .withHeader("Connection", "close")
                        .withBody(peopleResponseValidationJson)));

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


    @BeforeEach
    void before() {
        cleanupData();
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @DisplayName("Elinks People endpoint Parent ID Null verification")
    @Test
    void getPeopleUserProfile() {
        elinksApiJobScheduler.loadElinksJob();

        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();
        // asserting location data
        validateLocationData(elinksAudit);
        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse)response.get("body");
        assertEquals("People data loaded successfully", profiles.getMessage());

        var list = elinkDataExceptionRepository.findAll();

        assertThat(list).isNotEmpty();
        Assert.assertEquals(" The Parent ID is null/blanks for Tribunal Base Location ID  "
                        + "1815 in the Location_Type table.",
                list.get(0).getErrorDescription());
    }



    private void validateLocationData(List<ElinkDataSchedularAudit> elinksAudit) {
        cleanupData();
        Map<String, Object> locationResponse = elinksReferenceDataClient.getLocations();
        ElinkLocationWrapperResponse locations = (ElinkLocationWrapperResponse) locationResponse.get("body");

    }


    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
        authorisationsRepository.deleteAll();
        appointmentsRepository.deleteAll();
        judicialRoleTypeRepository.deleteAll();
        baseLocationRepository.deleteAll();
        profileRepository.deleteAll();
        dataloadSchedulerJobRepository.deleteAll();
    }




}