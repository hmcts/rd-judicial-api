package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinksResponses;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;

public class ElinksDataCleanupIntegrationTests extends ElinksDataLoadBaseTest {
    @BeforeEach
    public void cleanUp() {
        deleteData();
    }

    @DisplayName("Should delete eLinks Responses")
    @Test
    void shouldDeleteElinksResponses() throws IOException {

        final String peopleApiResponseJson = readJsonAsString(PEOPLE_API_RESPONSE_JSON);
        final String locationApiResponseJson = readJsonAsString(LOCATION_API_RESPONSE_JSON);

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        List<ElinksResponses> elinksResponses = elinksResponsesRepository.findAll();

        elinksResponses.forEach(response ->
                response.setCreatedDate(LocalDateTime.now().minusDays(cleanElinksResponsesDays))
        );

        elinksResponsesRepository.saveAll(elinksResponses);

        elinksServiceImpl.cleanUpElinksResponses();

        List<ElinksResponses> elinksResponsesAfterCleanUp = elinksResponsesRepository.findAll();

        assertThat(elinksResponsesAfterCleanUp).isEmpty();

    }

    @DisplayName("Should delete old deleted profiles")
    @Test
    void shouldDeleteOldDeletedProfiles() {
        LocalDateTime schedularStartTime = LocalDateTime.now();
        profileRepository.save(buildUserProfileDto());
        assertThat(profileRepository.findAll()).isNotEmpty();
        elinksServiceImpl.deleteJohProfiles(schedularStartTime);
        //after deleting the entry from table whose deleted date on is before 7 years
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
