package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
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

import java.util.List;
import java.util.Map;

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
    void getLeaversUserProfile() {

        Map<String, Object> response = elinksReferenceDataClient.getDeleted();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkDeletedWrapperResponse elinkDeletedWrapperResponse = (ElinkDeletedWrapperResponse)response.get("body");
        assertEquals("Deleted users Data Loaded Successfully", elinkDeletedWrapperResponse.getMessage());
    }

    @DisplayName("Elinks Deleted to JRD user profile verification")
    @Test
    @Order(2)
    void verifyDeletedJrdUserProfile() {
        Map<String, Object> deleted = elinksReferenceDataClient.getDeleted();
        assertThat(deleted).containsEntry("http_status", "200 OK");
        ElinkDeletedWrapperResponse profiles = (ElinkDeletedWrapperResponse)deleted.get("body");
        assertEquals("Deleted users Data Loaded Successfully", profiles.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();

        assertEquals(5, userprofile.size());
        assertEquals("28", userprofile.get(1).getPersonalCode());
        assertEquals(true, userprofile.get(1).getActiveFlag());
        assertEquals("1.11112E+12", userprofile.get(1).getObjectId());

    }

    @DisplayName("Elinks Deleted to JRD Audit Success Functionality verification")
    @Test
    @Order(3)
    void verifyDeletedJrdAuditFunctionality() {
        Map<String, Object> deleted = elinksReferenceDataClient.getDeleted();
        assertThat(deleted).containsEntry("http_status", "200 OK");
        ElinkDeletedWrapperResponse deletedResponse = (ElinkDeletedWrapperResponse)deleted.get("body");
        assertEquals("Deleted users Data Loaded Successfully", deletedResponse.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();

        assertEquals(5, userprofile.size());
        assertEquals("28", userprofile.get(1).getPersonalCode());
        assertEquals(true, userprofile.get(1).getActiveFlag());
        assertEquals("1.11112E+12", userprofile.get(1).getObjectId());

        List<ElinkDataSchedularAudit>  elinksAudit = elinkSchedularAuditRepository.findAll();

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(1);

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
