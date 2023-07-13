package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;

class PeopleIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    @Autowired
    private AuthorisationsRepository authorisationsRepository;

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


    @DisplayName("Elinks People endpoint status verification")
    @Test
    void getPeopleUserProfile() {

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse)response.get("body");
        assertEquals("People data loaded successfully", profiles.getMessage());
    }

    @DisplayName("Elinks People to JRD user profile verification")
    @Test
    void verifyPeopleJrdUserProfile() {

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse)response.get("body");
        assertEquals("People data loaded successfully", profiles.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();

        assertEquals(2, userprofile.size());
        assertEquals("410540", userprofile.get(1).getPersonalCode());
        assertEquals("Yuriko", userprofile.get(1).getKnownAs());
        assertEquals("Koiko", userprofile.get(1).getSurname());
        assertEquals("Her Honour Judge Yuriko Koiko", userprofile.get(1).getFullName());
        assertEquals(null, userprofile.get(1).getPostNominals());
        assertEquals("HHJ.Yuriko.Koiko@judiciarystaging13232.onmicrosoft.com",
                userprofile.get(1).getEjudiciaryEmailId());
        assertNull(userprofile.get(1).getLastWorkingDate());
        assertEquals("94772643-2c5f-4f84-8731-3dd7c25c9e11", userprofile.get(1).getObjectId());
        assertEquals("B.K",userprofile.get(1).getInitials());

        assertEquals("c38f7bdc-e52b-4711-90e6-9d49a2bb38f2", userprofile.get(0).getObjectId());

    }

    @DisplayName("Elinks People to Authorisation verification")
    @Test
    void verifyPeopleJrdAuthorisation() {

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse)response.get("body");
        assertEquals("People data loaded successfully", profiles.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();
        List<Authorisation> authorisationList = authorisationsRepository.findAll();

        assertEquals(8, authorisationList.size());
        assertEquals(userprofile.get(0).getPersonalCode(), authorisationList.get(0).getPersonalCode());
        assertEquals(userprofile.get(0).getObjectId(),authorisationList.get(0).getObjectId());
        assertEquals("Civil", authorisationList.get(0).getJurisdiction());
        assertEquals("Administrative Court", authorisationList.get(0).getLowerLevel());
        assertEquals("290",authorisationList.get(0).getTicketCode());
        assertNotNull(authorisationList.get(0).getStartDate());
        assertNull(authorisationList.get(0).getEndDate());
        assertNotNull(authorisationList.get(0).getCreatedDate());
        assertNotNull(authorisationList.get(0).getLastUpdated());

        assertEquals(userprofile.get(0).getPersonalCode(), authorisationList.get(1).getPersonalCode());
        assertEquals(userprofile.get(0).getObjectId(),authorisationList.get(1).getObjectId());
        assertEquals("Civil", authorisationList.get(1).getJurisdiction());
        assertEquals("Civil Authorisation", authorisationList.get(1).getLowerLevel());
        assertEquals("294",authorisationList.get(1).getTicketCode());
        assertNotNull(authorisationList.get(1).getStartDate());
        assertNull(authorisationList.get(1).getEndDate());
        assertNotNull(authorisationList.get(1).getCreatedDate());
        assertNotNull(authorisationList.get(1).getLastUpdated());

    }


    @DisplayName("Elinks People to Audit verification")
    @Test
    void verifyPeopleJrdAuditFunctionality() {

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse)response.get("body");
        assertEquals("People data loaded successfully", profiles.getMessage());

        List<ElinkDataSchedularAudit>  elinksAudit = elinkSchedularAuditRepository.findAll();
        ElinkDataSchedularAudit auditEntry = elinksAudit.get(0);
        assertEquals(PEOPLEAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.PARTIAL_SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
        authorisationsRepository.deleteAll();
        appointmentsRepository.deleteAll();
        profileRepository.deleteAll();
    }

}
