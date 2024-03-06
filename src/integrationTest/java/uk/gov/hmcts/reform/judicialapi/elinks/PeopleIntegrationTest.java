package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinksResponses;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.impl.ELinksServiceImpl;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;

class PeopleIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    ELinksServiceImpl elinksServiceImpl;

    @Value("${elinks.cleanElinksResponsesDays}")
    private Long cleanElinksResponsesDays;

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

        List<ElinksResponses> elinksResponses = elinksResponsesRepository.findAll();

        assertThat(elinksResponses).isNotEmpty();
        assertThat(elinksResponses.get(0).getCreatedDate()).isNotNull();
        assertThat(elinksResponses.get(0).getElinksData()).isNotNull();
    }

    @DisplayName("Elinks People to JRD user profile verification")
    @Test
    void verifyPeopleJrdUserProfile() {

        Map<String, Object> locationResponse = elinksReferenceDataClient.getLocations();
        Map<String, Object> baseLocationResponse = elinksReferenceDataClient.getBaseLocations();
        ElinkBaseLocationWrapperResponse baseLocations =
            (ElinkBaseLocationWrapperResponse) baseLocationResponse.get("body");

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse)response.get("body");
        assertEquals("People data loaded successfully", profiles.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();

        assertEquals(2, userprofile.size());
        assertEquals("4913085", userprofile.get(0).getPersonalCode());
        assertEquals("Rachel", userprofile.get(0).getKnownAs());
        assertEquals("Jones", userprofile.get(0).getSurname());
        assertEquals("District Judge Rachel Jones", userprofile.get(0).getFullName());
        assertEquals(null, userprofile.get(0).getPostNominals());
        assertEquals("DJ.Rachel.Jones@ejudiciary.net",
                userprofile.get(0).getEmailId());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae", userprofile.get(0).getObjectId());
        assertEquals("RJ",userprofile.get(0).getInitials());


    }

    @DisplayName("Elinks People to Authorisation verification")
    @Test
    void verifyPeopleJrdAuthorisation() {

        Map<String, Object> locationResponse = elinksReferenceDataClient.getLocations();
        Map<String, Object> baseLocationResponse = elinksReferenceDataClient.getBaseLocations();
        ElinkBaseLocationWrapperResponse baseLocations =
            (ElinkBaseLocationWrapperResponse) baseLocationResponse.get("body");

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse)response.get("body");
        assertEquals("People data loaded successfully", profiles.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();
        List<Authorisation> authorisationList = authorisationsRepository.findAll();

        assertEquals(4, authorisationList.size());
        assertEquals(userprofile.get(0).getPersonalCode(), authorisationList.get(0).getPersonalCode());
        assertEquals("Family", authorisationList.get(0).getJurisdiction());
        assertEquals("Court of Protection", authorisationList.get(0).getLowerLevel());
        assertEquals("313",authorisationList.get(0).getTicketCode());
        assertNotNull(authorisationList.get(0).getStartDate());
        assertNotNull(authorisationList.get(0).getCreatedDate());
        assertNotNull(authorisationList.get(0).getLastUpdated());

        assertEquals(userprofile.get(0).getPersonalCode(), authorisationList.get(1).getPersonalCode());
        assertEquals("Tribunals", authorisationList.get(1).getJurisdiction());
        assertEquals("Criminal Injuries Compensations", authorisationList.get(1).getLowerLevel());
        assertEquals("328",authorisationList.get(1).getTicketCode());
        assertNotNull(authorisationList.get(1).getStartDate());
        assertNotNull(authorisationList.get(1).getEndDate());
        assertNotNull(authorisationList.get(1).getCreatedDate());
        assertNotNull(authorisationList.get(1).getLastUpdated());

    }


    @DisplayName("Elinks People to Audit verification")
    @Test
    void verifyPeopleJrdAuditFunctionality() {

        Map<String, Object> locationResponse = elinksReferenceDataClient.getLocations();
        Map<String, Object> baseLocationResponse = elinksReferenceDataClient.getBaseLocations();
        ElinkBaseLocationWrapperResponse baseLocations =
            (ElinkBaseLocationWrapperResponse) baseLocationResponse.get("body");

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse)response.get("body");
        assertEquals("People data loaded successfully", profiles.getMessage());

        List<ElinkDataSchedularAudit>  elinksAudit = elinkSchedularAuditRepository.findAll();
        ElinkDataSchedularAudit auditEntry = elinksAudit.get(2);
        assertEquals(PEOPLEAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.PARTIAL_SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    @DisplayName("Elinks Responses cleanup status verification")
    @Test
    void testCleanElinksResponses() {

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse)response.get("body");
        assertEquals("People data loaded successfully", profiles.getMessage());

        List<ElinksResponses> elinksResponses = elinksResponsesRepository.findAll();

        elinksResponses.get(0).setCreatedDate(LocalDateTime.now().minusDays(cleanElinksResponsesDays));

        elinksResponsesRepository.saveAll(elinksResponses);

        elinksServiceImpl.cleanUpElinksResponses();

        List<ElinksResponses> elinksResponsesAfterCleanUp = elinksResponsesRepository.findAll();

        assertThat(elinksResponsesAfterCleanUp).isEmpty();

    }

    @DisplayName("Elinks Responses cleanup status verification")
    @Test
    void validateDeleteJohProfiles() {

        LocalDateTime schedularStartTime = LocalDateTime.now();
        profileRepository.save(buildUserProfileDto());
        assertThat(profileRepository.findAll()).isNotEmpty();
        elinksServiceImpl.deleteJohProfiles(schedularStartTime);
        //after deleting the entry from table whose deleted date on is before 7 years the repository is null
        assertThat(profileRepository.findAll()).isEmpty();

    }

    private UserProfile buildUserProfileDto() {

        return UserProfile.builder()
                .personalCode("0049931063")
                .knownAs("Tester")
                .surname("TestAccount")
                .fullName("Tribunal Judge Tester TestAccount 2")
                .postNominals("ABC")
                .emailId("Tester2@judiciarystaging.onmicrosoft.com")
                .lastWorkingDate(LocalDate.now())
                .activeFlag(true)
                .createdDate(LocalDateTime.now())
                .lastLoadedDate(LocalDateTime.now())
                .objectId("552da697-4b3d-4aed-9c22-1e903b70aead")
                .initials("Mr")
                .sidamId("3fa85f64-5717-4562-b3fc-2c963f66afa6")
                .deletedOn(LocalDateTime.now().minusYears(7))
                .deletedFlag(true)
                .build();
    }

}
