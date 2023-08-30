package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;


class LeaversIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    JudicialRoleTypeRepository judicialRoleTypeRepository;
    @Autowired
    BaseLocationRepository baseLocationRepository;
    @Autowired
    AuthorisationsRepository authorisationsRepository;
    @Autowired
    AppointmentsRepository appointmentsRepository;
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

    @DisplayName("Elinks Leavers endpoint status verification")
    @Test
    @Order(1)
    void getLeaversUserProfile() {

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkLeaversWrapperResponse profiles = (ElinkLeaversWrapperResponse)response.get("body");
        assertEquals("Leavers Data Loaded Successfully", profiles.getMessage());
    }

    @DisplayName("Elinks Leavers to JRD user profile verification")
    @Test
    @Order(2)
    void verifyLeaversJrdUserProfile() {
        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        Map<String, Object> leaversResponse = elinksReferenceDataClient.getLeavers();
        assertThat(leaversResponse).containsEntry("http_status", "200 OK");
        ElinkLeaversWrapperResponse profiles = (ElinkLeaversWrapperResponse)leaversResponse.get("body");
        assertEquals("Leavers Data Loaded Successfully", profiles.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();

        assertEquals(11, userprofile.size());
        assertEquals("28", userprofile.get(1).getPersonalCode());
        assertEquals(true, userprofile.get(1).getActiveFlag());
        assertEquals("1.11112E+12", userprofile.get(1).getObjectId());

    }

    @DisplayName("Elinks Leavers to JRD Audit Success Functionality verification")
    @Test
    @Order(3)
    void verifyLeaversJrdAuditFunctionality() {
        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        Map<String, Object> leaversResponse = elinksReferenceDataClient.getLeavers();
        assertThat(leaversResponse).containsEntry("http_status", "200 OK");
        ElinkLeaversWrapperResponse profiles = (ElinkLeaversWrapperResponse)leaversResponse.get("body");
        assertEquals("Leavers Data Loaded Successfully", profiles.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();

        assertEquals(11, userprofile.size());
        assertEquals("28", userprofile.get(1).getPersonalCode());
        assertEquals(true, userprofile.get(1).getActiveFlag());
        assertEquals("1.11112E+12", userprofile.get(1).getObjectId());

        List<ElinkDataSchedularAudit>  elinksAudit = elinkSchedularAuditRepository.findAll();

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(1);

        assertThat(auditEntry.getId()).isPositive();

        assertEquals(LEAVERSAPI, auditEntry.getApiName());
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
    }

}
