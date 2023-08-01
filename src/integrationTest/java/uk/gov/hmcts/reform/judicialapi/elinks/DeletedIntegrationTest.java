package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkDeletedWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;


class DeletedIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    private ProfileRepository profileRepository;


    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;

    @BeforeAll
    void loadElinksResponse() throws Exception {

        cleanupData();

        String locationResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/location.json");
        String baselocationResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/base_location.json");
        String peopleResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/people_part.json");
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

        elinks.stubFor(get(urlPathMatching("/deleted"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", V2.MediaType.SERVICE)
                .withHeader("Connection", "close")
                .withBody(deletedResponseValidationJson)));
    }

    @BeforeEach
    void setUp() {
        cleanupData();
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @DisplayName("Elinks Deleted endpoint status verification")
    @Test
    @Order(1)
    void getDeletedUserProfile() {

        Map<String, Object> response = elinksReferenceDataClient.getDeleted();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkDeletedWrapperResponse elinkDeletedWrapperResponse = (ElinkDeletedWrapperResponse)response.get("body");
        assertEquals("Deleted users Data Loaded Successfully", elinkDeletedWrapperResponse.getMessage());
    }

    @DisplayName("Elinks Deleted to JRD user profile verification")
    @Test
    @Order(2)
    void verifyDeletedJrdUserProfile() {
        Map<String, Object> baseLocationResponse = elinksReferenceDataClient.getBaseLocations();
        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        Map<String, Object> deleted = elinksReferenceDataClient.getDeleted();
        assertThat(deleted).containsEntry("http_status", "200 OK");
        ElinkDeletedWrapperResponse profiles = (ElinkDeletedWrapperResponse)deleted.get("body");
        assertEquals("Deleted users Data Loaded Successfully", profiles.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();

        assertEquals(5, userprofile.size());
        assertEquals("4913085", userprofile.get(3).getPersonalCode());
        assertEquals(true, userprofile.get(3).getDeletedFlag());
        assertEquals("2023-07-13", userprofile.get(3).getDeletedOn().toLocalDate().toString());

        assertEquals("4913086", userprofile.get(4).getPersonalCode());
        assertEquals(false, userprofile.get(4).getDeletedFlag());
        assertEquals("2022-07-10", userprofile.get(4).getDeletedOn().toLocalDate().toString());



    }

    @DisplayName("Elinks Deleted to JRD Audit Success Functionality verification")
    @Test
    @Order(3)
    void verifyDeletedJrdAuditFunctionality() {
        Map<String, Object> baseLocationResponse = elinksReferenceDataClient.getBaseLocations();
        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        Map<String, Object> deleted = elinksReferenceDataClient.getDeleted();
        assertThat(deleted).containsEntry("http_status", "200 OK");
        ElinkDeletedWrapperResponse deletedResponse = (ElinkDeletedWrapperResponse)deleted.get("body");
        assertEquals("Deleted users Data Loaded Successfully", deletedResponse.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();

        assertEquals(5, userprofile.size());
        assertEquals("4913085", userprofile.get(3).getPersonalCode());
        assertEquals(true, userprofile.get(3).getDeletedFlag());
        assertEquals("2023-07-13", userprofile.get(3).getDeletedOn().toLocalDate().toString());

        assertEquals("4913086", userprofile.get(4).getPersonalCode());
        assertEquals(false, userprofile.get(4).getDeletedFlag());
        assertEquals("2022-07-10", userprofile.get(4).getDeletedOn().toLocalDate().toString());


        List<ElinkDataSchedularAudit>  elinksAudit = elinkSchedularAuditRepository.findAll();

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(2);

        assertThat(auditEntry.getId()).isPositive();

        assertEquals(DELETEDAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
    }

}
