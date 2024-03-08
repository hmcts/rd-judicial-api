package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinksResponses;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
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

    @BeforeAll
    void loadElinksResponse() throws Exception {
        cleanupData();

        String peopleResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/people_part.json");

        elinks.stubFor(get(urlPathMatching("/people"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", V2.MediaType.SERVICE)
                .withHeader("Connection", "close")
                .withBody(peopleResponseValidationJson)));
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
        List<ElinksResponses> elinksResponses = elinksResponsesRepository.findAll();
        assertThat(elinksResponses.size()).isGreaterThan(0);
        assertThat(elinksResponses.get(0).getCreatedDate()).isNotNull();
        assertThat(elinksResponses.get(0).getElinksData()).isNotNull();
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

        assertEquals(15, userprofile.size());
        UserProfile userProfile = userprofile.get(13);
        assertEquals("4913085", userProfile.getPersonalCode());
        assertEquals(true, userProfile.getDeletedFlag());
        assertEquals("2023-07-13", userProfile.getDeletedOn().toLocalDate().toString());

        userProfile = userprofile.get(14);
        assertEquals("4913086", userProfile.getPersonalCode());
        assertEquals(false, userProfile.getDeletedFlag());
        assertEquals("2022-07-10",userProfile.getDeletedOn().toLocalDate().toString());



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

        assertEquals(15, userprofile.size());
        UserProfile userProfile = userprofile.get(13);
        assertEquals("4913085", userProfile.getPersonalCode());
        assertEquals(true, userProfile.getDeletedFlag());
        assertEquals("2023-07-13", userProfile.getDeletedOn().toLocalDate().toString());

        userProfile = userprofile.get(14);
        assertEquals("4913086", userProfile.getPersonalCode());
        assertEquals(false, userProfile.getDeletedFlag());
        assertEquals("2022-07-10",userProfile.getDeletedOn().toLocalDate().toString());

        List<ElinkDataSchedularAudit>  elinksAudit = elinkSchedularAuditRepository.findAll();

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(2);

        assertThat(auditEntry.getId()).isPositive();

        assertEquals(DELETEDAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    protected void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
    }

}
