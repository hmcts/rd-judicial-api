package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertEquals(1, userprofile.size());
        assertEquals("0049931063", userprofile.get(0).getPersonalCode());
        assertEquals("Tester", userprofile.get(0).getKnownAs());
        assertEquals("TestAccount 2", userprofile.get(0).getSurname());
        assertEquals("Tribunal Judge Tester TestAccount 2", userprofile.get(0).getFullName());
        assertEquals("ABC", userprofile.get(0).getPostNominals());
        assertEquals("Tester2@judiciarystaging.onmicrosoft.com", userprofile.get(0).getEjudiciaryEmailId());
        assertNull(userprofile.get(0).getLastWorkingDate());
        assertEquals("552da697-4b3d-4aed-9c22-1e903b70aead", userprofile.get(0).getObjectId());

    }


    @DisplayName("Elinks People to Appointment verification")
    @Test
    void verifyPeopleJrdAppointment() {

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse)response.get("body");
        assertEquals("People data loaded successfully", profiles.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();
        List<Appointment> appointmentList = appointmentsRepository.findAll();

        assertEquals(2, appointmentList.size());
        assertEquals(userprofile.get(0).getPersonalCode(), appointmentList.get(0).getPersonalCode());
        assertEquals(userprofile.get(0).getObjectId(),appointmentList.get(0).getObjectId());
        assertEquals("0", appointmentList.get(0).getBaseLocationId());
        assertEquals("0", appointmentList.get(0).getRegionId());
        assertNull(appointmentList.get(0).getEpimmsId());
        assertNull(appointmentList.get(0).getServiceCode());
        assertEquals("fee_paid", appointmentList.get(0).getAppointmentType());
        assertNotNull(appointmentList.get(0).getStartDate());
        assertFalse(appointmentList.get(0).getIsPrincipleAppointment());
        assertEquals("Fee Paid Judiciary 5 Days Mon - Fri", appointmentList.get(0).getWorkPattern());
        assertEquals("Tribunal Judge", appointmentList.get(0).getAppointmentRolesMapping());

        assertEquals(userprofile.get(0).getPersonalCode(), appointmentList.get(1).getPersonalCode());
        assertEquals(userprofile.get(0).getObjectId(),appointmentList.get(1).getObjectId());
        assertEquals("0", appointmentList.get(1).getBaseLocationId());
        assertEquals("0", appointmentList.get(1).getRegionId());
        assertNull(appointmentList.get(1).getEpimmsId());
        assertNull(appointmentList.get(1).getServiceCode());
        assertEquals("salaried", appointmentList.get(1).getAppointmentType());
        assertNotNull(appointmentList.get(1).getStartDate());
        assertTrue(appointmentList.get(1).getIsPrincipleAppointment());
        assertEquals("Fee Paid Judiciary 5 Days Mon - Fri", appointmentList.get(1).getWorkPattern());
        assertEquals("Magistrate", appointmentList.get(1).getAppointmentRolesMapping());

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

        assertEquals(2, authorisationList.size());
        assertEquals(userprofile.get(0).getPersonalCode(), authorisationList.get(0).getPersonalCode());
        assertEquals(userprofile.get(0).getObjectId(),authorisationList.get(0).getObjectId());
        assertEquals("Family", authorisationList.get(0).getJurisdiction());
        assertEquals("Private Law", authorisationList.get(0).getLowerLevel());
        assertEquals("315",authorisationList.get(0).getTicketCode());
        assertNotNull(authorisationList.get(0).getStartDate());
        assertNull(authorisationList.get(0).getEndDate());
        assertNotNull(authorisationList.get(0).getCreatedDate());
        assertNotNull(authorisationList.get(0).getLastUpdated());

        assertEquals(userprofile.get(0).getPersonalCode(), authorisationList.get(1).getPersonalCode());
        assertEquals(userprofile.get(0).getObjectId(),authorisationList.get(1).getObjectId());
        assertEquals("Tribunals", authorisationList.get(1).getJurisdiction());
        assertEquals("05 - Industrial Injuries", authorisationList.get(1).getLowerLevel());
        assertEquals("367",authorisationList.get(1).getTicketCode());
        assertNotNull(authorisationList.get(1).getStartDate());
        assertNull(authorisationList.get(1).getEndDate());
        assertNotNull(authorisationList.get(1).getCreatedDate());
        assertNotNull(authorisationList.get(1).getLastUpdated());

    }

    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
    }

}
