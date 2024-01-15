package uk.gov.hmcts.reform.judicialapi.elinks;




import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkDeletedWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkIdamWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.scheduler.ElinksApiJobScheduler;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;
import static uk.gov.hmcts.reform.judicialapi.util.KeyGenUtil.getDynamicJwksResponse;

class ElinksSsoidEndIntegrationTest extends ElinksEnabledIntegrationTest {

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


    @Value("${idam.find.query}")
    String idamFindQuery;

    @BeforeAll
    void loadElinksResponse() throws Exception {

        String locationResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/test_loc.json");
        String baselocationResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/base_location.json");
        String peopleResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/testssoid_people.json");
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

        String idamResponseForObjectId =
            loadJson("src/integrationTest/resources/wiremock_responses/idamResponsefromSsoId.json");

        sidamService.stubFor(get(urlPathMatching("/api/v1/users"))
            .withQueryParam("query", containing("ssoid"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withHeader("Connection", "close")
                .withBody(idamResponseForObjectId)
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
    void after() {
        cleanupData();
    }

    @DisplayName("Elinks end to end success scenario")
    @Test
    void test_elinks_end_to_end_success_scenario_with_return_status_200()
            throws JOSEException, JsonProcessingException {



        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();
        // asserting location data
        validateLocationData(elinksAudit);

        //asserting baselocation data
        validateBaseLocation(elinksAudit);

        //asserting people data
        validatePeopleData(elinksAudit);
        idamSetUp();
        List<DataloadSchedulerJob> audits = dataloadSchedulerJobRepository.findAll();


        validateIdamFetch(audits);


    }

    private void validateLeaver(List<ElinkDataSchedularAudit> elinksAudit) {
        Map<String, Object> leaversResponse = elinksReferenceDataClient.getLeavers();
        ElinkLeaversWrapperResponse leaversProfiles = (ElinkLeaversWrapperResponse) leaversResponse.get("body");
        ElinkDataSchedularAudit leaversAuditEntry = elinksAudit.get(2);

        assertThat(leaversResponse).containsEntry("http_status", "200 OK");
        assertEquals("Leavers Data Loaded Successfully", leaversProfiles.getMessage());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),leaversAuditEntry.getStatus());

        List<UserProfile> leaverUserProfile = profileRepository.findAll();
        assertEquals(2, leaverUserProfile.size());
        assertEquals("4913085", leaverUserProfile.get(0).getPersonalCode());
        assertEquals(true, leaverUserProfile.get(0).getActiveFlag());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae", leaverUserProfile.get(0).getObjectId());
        assertEquals("2026-07-23",leaverUserProfile.get(0).getLastWorkingDate().toString());
        assertNotNull(leaverUserProfile.get(0).getLastLoadedDate());


        ElinkDataSchedularAudit auditEntry = elinksAudit.get(2);
        assertThat(auditEntry.getId()).isPositive();
        assertEquals(LEAVERSAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    private void validatePeopleData(List<ElinkDataSchedularAudit> elinksAudit) {
        Map<String, Object> peopleResponse = elinksReferenceDataClient.getPeoples();
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse) peopleResponse.get("body");
        assertThat(peopleResponse).containsEntry("http_status", "200 OK");
        assertEquals("People data loaded successfully", profiles.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();
        assertEquals(12, userprofile.size());
        assertEquals("12345477", userprofile.get(11).getPersonalCode());
        assertEquals("Rachel", userprofile.get(11).getKnownAs());
        assertEquals("Jones", userprofile.get(11).getSurname());
        assertEquals("District Judge Rachel Jones", userprofile.get(11).getFullName());
        assertEquals(null, userprofile.get(11).getPostNominals());
        assertEquals("DJ.Rachel.Jones@ejudiciary.net",
                userprofile.get(11).getEmailId());
        assertTrue(userprofile.get(11).getActiveFlag());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cbe", userprofile.get(11).getObjectId());
        assertEquals("33333344",userprofile.get(11).getSidamId());
        assertEquals("RJ",userprofile.get(11).getInitials());

        //asserting Judiciary additonal roles data
        List<JudicialRoleType> roleRequest = judicialRoleTypeRepository.findAll();
        assertEquals(1, roleRequest.size());
        assertEquals("Course Director for COP (JC)", roleRequest.get(0).getTitle());
        assertEquals("12345477", roleRequest.get(0).getPersonalCode());
        assertEquals("427", roleRequest.get(0).getJurisdictionRoleId());
        assertEquals("fee", roleRequest.get(0).getJurisdictionRoleNameId());

    }

    private void validateBaseLocation(List<ElinkDataSchedularAudit> elinksAudit) {
        Map<String, Object> baseLocationResponse = elinksReferenceDataClient.getBaseLocations();
        ElinkBaseLocationWrapperResponse baseLocations =
                (ElinkBaseLocationWrapperResponse) baseLocationResponse.get("body");
    }

    private void validateLocationData(List<ElinkDataSchedularAudit> elinksAudit) {
        Map<String, Object> locationResponse = elinksReferenceDataClient.getLocations();
        ElinkLocationWrapperResponse locations = (ElinkLocationWrapperResponse) locationResponse.get("body");

    }

    private void idamSetUp() {

        final String clientId = "234342332";
        final String redirectUri = "http://idam-api.aat.platform.hmcts.net";
        //The authorization and clientAuth is the dummy value which we can evaluate using BASE64 encoder.
        final String authorization = "ZHVtbXl2YWx1ZUBobWN0cy5uZXQ6SE1DVFMxMjM0";
        final String clientAuth = "cmQteHl6LWFwaTp4eXo=";
        final String url = "http://127.0.0.1:5000";
        tokenConfigProperties.setClientId(clientId);
        tokenConfigProperties.setClientAuthorization(clientAuth);
        tokenConfigProperties.setAuthorization(authorization);
        tokenConfigProperties.setRedirectUri(redirectUri);
        tokenConfigProperties.setUrl(url);

    }

    private void validateSidamPublish() {
        Map<String, Object> idamResponse = elinksReferenceDataClient.publishSidamIds();
        doNothing().when(elinkTopicPublisher).sendMessage(anyList(),anyString());
        assertThat(idamResponse).containsEntry("http_status", "200 OK");
        HashMap publishSidamIdsResponse = (LinkedHashMap)idamResponse.get("body");

        assertThat(publishSidamIdsResponse.get("publishing_status")).isNotNull();

        List<ElinkDataExceptionRecords> elinksException = elinkDataExceptionRepository.findAll();
        assertThat(elinksException).isNotEmpty();
    }

    private void validateElasticSearch(List<DataloadSchedulerJob> audits) {
        Map<String, Object> idamResponses = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals("200 OK",idamResponses.get("http_status"));
        List<IdamResponse> idamResponseVal = (ArrayList<IdamResponse>) idamResponses.get("body");
        assertEquals(2,idamResponseVal.size());

        List<UserProfile> userprofileAfterSidamresponse = profileRepository.findAll();
        UserProfile sidamID = userprofileAfterSidamresponse.get(0);

        assertEquals(2, userprofileAfterSidamresponse.size());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae",
            userprofileAfterSidamresponse.get(1).getObjectId());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e972",
            userprofileAfterSidamresponse.get(1).getSidamId());

        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), audits.get(0).getPublishingStatus());
    }

    private void validateIdamFetch(List<DataloadSchedulerJob> audits) {
        Map<String, Object> idamResponses = elinksReferenceDataClient.getIdamIds();
        assertEquals("200 OK",idamResponses.get("http_status"));
        ElinkIdamWrapperResponse idamResponseVal = (ElinkIdamWrapperResponse) idamResponses.get("body");
        assertNotNull(idamResponseVal);

        List<UserProfile> userprofileAfterSidamresponse = profileRepository.findAll();
        UserProfile sidamID = userprofileAfterSidamresponse.get(0);

        assertEquals(12, userprofileAfterSidamresponse.size());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cbe",
            userprofileAfterSidamresponse.get(11).getObjectId());
        assertEquals("f523ab5d-0a87-44c0-8c3b-28ff89878afd",
            userprofileAfterSidamresponse.get(11).getSidamId());
    }

    private void validateDeleted(List<ElinkDataSchedularAudit> elinksAudit) {
        Map<String, Object> deletedResponse = elinksReferenceDataClient.getDeleted();
        ElinkDeletedWrapperResponse deletedProfiles = (ElinkDeletedWrapperResponse) deletedResponse.get("body");
        ElinkDataSchedularAudit deletedAuditEntry = elinksAudit.get(3);

        assertThat(deletedResponse).containsEntry("http_status", "200 OK");
        assertEquals("Deleted users Data Loaded Successfully", deletedProfiles.getMessage());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),deletedAuditEntry.getStatus());

        List<UserProfile> deletedUserProfile = profileRepository.findAll();
        assertEquals(2, deletedUserProfile.size());
        assertEquals("4913085", deletedUserProfile.get(1).getPersonalCode());
        assertEquals(true, deletedUserProfile.get(1).getDeletedFlag());
        assertEquals("2023-07-13",deletedUserProfile.get(1).getDeletedOn().toLocalDate().toString());

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(3);
        assertThat(auditEntry.getId()).isPositive();
        assertEquals(DELETEDAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
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