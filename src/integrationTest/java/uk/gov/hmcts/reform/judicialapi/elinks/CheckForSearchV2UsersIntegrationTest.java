package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.util.JudicialReferenceDataClient;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckForSearchV2UsersIntegrationTest extends AuthorizationEnabledIntegrationTest {

    private static final MediaType V2_SERVICE_MEDIA_TYPE =
            MediaType.valueOf(V2.MediaType.SERVICE);

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private BaseLocationRepository baseLocationRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AuthorisationsRepository authorisationsRepository;

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    @AfterEach
    void tearDown() {
        cleanupData();
        resetAuthenticationState();
    }

    @ParameterizedTest(name = "[{index}] role={0}")
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchString(String role) {

        var request = userSearchRequest("test", null, null);

        var response = searchUsers(request, role);

        assertStatus(response, "200 OK");

        var usersList = foundUsersList(response);

        assertEquals(3, usersList.size());

        assertUserProfile(
                usersList.get(0),
                "test530@test.net",
                "Ms",
                "B.K",
                null
        );

        assertEquals("test528@test.net", usersList.get(1).get("emailId"));
        assertEquals("test529@test.net", usersList.get(2).get("emailId"));
    }

    @ParameterizedTest(name = "[{index}] role={0}, serviceCode={1}")
    @CsvSource({
        "jrd-system-user, BBA3",
        "jrd-admin,       BBA3",
        "jrd-system-user, BFA1",
        "jrd-admin,       BFA1"
    })
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringForsscsAndIac(String role,
                                                                                  String serviceCode) {

        var request = userSearchRequest("one", serviceCode, null);

        var response = searchUsers(request, role);

        assertStatus(response, "200 OK");

        var usersList = foundUsersList(response);

        assertEquals(1, usersList.size());

        assertUserProfile(
                usersList.getFirst(),
                "One531@test.net",
                "Mr",
                "J.K",
                "32"
        );
    }

    @ParameterizedTest(name = "[{index}] role={0}, serviceCode={1}")
    @CsvSource({
        "jrd-system-user, BBA3",
        "jrd-admin,       BBA3",
        "jrd-system-user, BFA1",
        "jrd-admin,       BFA1"
    })
    void shouldReturn200WhenUserProfileRequestedForSscsAndIacExpired(
            String role,
            String serviceCode) {

        var response = searchUsers(
                userSearchRequest("two", serviceCode, null),
                role
        );

        assertStatus(response, "200 OK");
        assertEquals(0, foundUsersList(response).size());
    }

    @ParameterizedTest(name = "[{index}] role={0}, serviceCode={1}")
    @CsvSource({
        "jrd-system-user, BBA3",
        "jrd-admin,       BBA3",
        "jrd-system-user, BFA1",
        "jrd-admin,       BFA1"
    })
    void shouldReturnNoUsersWhenAppointmentActiveAndAuthorisationExpired(
            String role,
            String serviceCode) {

        var response = searchUsers(
                userSearchRequest("three", serviceCode, null),
                role
        );

        assertStatus(response, "200 OK");
        assertEquals(0, foundUsersList(response).size());
    }

    @ParameterizedTest(name = "[{index}] role={0}, serviceCode={1}")
    @CsvSource({
        "jrd-system-user, BBA3",
        "jrd-admin,       BBA3",
        "jrd-system-user, BFA1",
        "jrd-admin,       BFA1"
    })
    void shouldReturnNoUsersWhenAppointmentExpiredAndAuthorisationActive(
            String role,
            String serviceCode) {

        var response = searchUsers(
                userSearchRequest("four", serviceCode, null),
                role
        );

        assertStatus(response, "200 OK");
        assertEquals(0, foundUsersList(response).size());
    }

    @ParameterizedTest(name = "[{index}] role={0}, serviceCode={1}, expectedCount={2}")
    @CsvSource({
        "jrd-system-user, BBA3, 1",
        "jrd-admin,       BBA3, 1",
        "jrd-system-user, BFA1, 0",
        "jrd-admin,       BFA1, 0"
    })
    void shouldReturnExpectedUsersWhenAppointmentAndAuthorisationAreActive(
            String role,
            String serviceCode,
            int expectedCount) {

        var response = searchUsers(
                userSearchRequest("five", serviceCode, null),
                role
        );

        assertStatus(response, "200 OK");
        assertEquals(expectedCount, foundUsersList(response).size());
    }

    @ParameterizedTest(name = "[{index}] role={0}, serviceCode={1}, expectedCount={2}")
    @CsvSource({
        "jrd-system-user, BBA3, 0",
        "jrd-admin,       BBA3, 0",
        "jrd-system-user, BFA1, 1",
        "jrd-admin,       BFA1, 1"
    })
    void shouldReturnExpectedUsersWhenSscsAppointmentExpiredAndIacAuthorisationActive(
            String role,
            String serviceCode,
            int expectedCount) {

        var response = searchUsers(
                userSearchRequest("six", serviceCode, null),
                role
        );

        assertStatus(response, "200 OK");
        assertEquals(expectedCount, foundUsersList(response).size());
    }

    @ParameterizedTest(name = "[{index}] role={0}, serviceCode={1}")
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturnUserWhenFamilyAppointmentIsActiveAndAuthorisationExpires(
            String role) {

        var response = searchUsers(
                userSearchRequest("seven", "BHA1", null),
                role
        );

        assertStatus(response, "200 OK");
        assertEquals(1, foundUsersList(response).size());
    }

    @ParameterizedTest(name = "[{index}] role={0}")
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn401ForInvalidToken(String role) {

        try {
            var response = judicialReferenceDataClient.searchUsers(
                    userSearchRequest("test", "BFA1", "location"),
                    role,
                    true,
                    V2_SERVICE_MEDIA_TYPE,
                    V2_SERVICE_MEDIA_TYPE
            );

            assertThat(response)
                    .containsEntry("http_status", "401");

        } finally {
            resetAuthenticationState();
        }
    }

    @ParameterizedTest(name = "[{index}] role={0}")
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn400WhenSearchStringIsEmpty(String role) {

        var response = searchUsers(
                userSearchRequest("", "BFA1", "location"),
                role
        );

        assertStatus(response, "400");

        assertResponseBodyContains(
                response,
                "cannot be empty"
        );
    }

    @ParameterizedTest(name = "[{index}] role={0}")
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn400WhenSearchStringIsTooShort(String role) {

        var response = searchUsers(
                userSearchRequest("te", "BFA1", "location"),
                role
        );

        assertStatus(response, "400");

        assertResponseBodyContains(
                response,
                "searchString must be at least 3 characters including letters, "
                        + "apostrophe, hyphen"
        );
    }

    @ParameterizedTest(name = "[{index}] role={0}")
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenSearchStringContainsNumbers(String role) {

        var response = searchUsers(
                userSearchRequest("test123", "BFA1", "location"),
                role
        );

        assertStatus(response, "200 OK");
    }

    @ParameterizedTest(name = "[{index}] role={0}")
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn400WhenSearchStringContainsInvalidCharacters(String role) {

        var response = searchUsers(
                userSearchRequest("àèÙ{}{}", "BFA1", "location"),
                role
        );

        assertStatus(response, "400");

        assertResponseBodyContains(
                response,
                "searchString must be at least 3 characters including letters, "
                        + "apostrophe, hyphen"
        );
    }

    @ParameterizedTest(name = "[{index}] role={0}")
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenSearchStringContainsAccentedCharacters(String role) {

        var response = searchUsers(
                userSearchRequest("àèÙ", "BFA1", "location"),
                role
        );

        assertStatus(response, "200 OK");
    }

    @ParameterizedTest(name = "[{index}] role={0}")
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturnUsersWhenAdditionalBooleanIsNotProvided(String role) {

        var response = searchUsers(
                userSearchRequest("test", null, null),
                role
        );

        assertStatus(response, "200 OK");

        var profiles = foundUsersList(response);

        assertEquals(3, profiles.size());

        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertEquals("test528@test.net", profiles.get(1).get("emailId"));
        assertEquals("test529@test.net", profiles.get(2).get("emailId"));

        assertEquals("29", profiles.get(0).get("personalCode"));
        assertEquals("27", profiles.get(1).get("personalCode"));
        assertEquals("28", profiles.get(2).get("personalCode"));
    }

    private Map<String, Object> searchUsers(
            UserSearchRequest request,
            String role) {

        return judicialReferenceDataClient.searchUsers(
                request,
                role,
                false,
                V2_SERVICE_MEDIA_TYPE,
                V2_SERVICE_MEDIA_TYPE
        );
    }

    private UserSearchRequest userSearchRequest(String searchString,
                                                String serviceCode,
                                                String location) {

        UserSearchRequest.UserSearchRequestBuilder builder =
                UserSearchRequest.builder();

        if (Objects.nonNull(serviceCode)) {
            builder.serviceCode(serviceCode);
        }
        if (Objects.nonNull(location)) {
            builder.location(location);
        }
        return builder.searchString(searchString).build();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> foundUsersList(
            Map<String, Object> response) {

        return (List<Map<String, String>>) response.get("body");
    }

    private void assertStatus(
            Map<String, Object> response,
            String expectedStatus) {

        assertThat(response)
                .containsEntry("http_status", expectedStatus);
    }

    private void assertResponseBodyContains(
            Map<String, Object> response,
            String expectedText) {

        var responseBody = (String) response.get("response_body");

        assertThat(responseBody)
                .contains(expectedText);
    }

    private void assertUserProfile(
            Map<String, String> profile,
            String email,
            String postNominals,
            String initials,
            String personalCode) {

        assertEquals(email, profile.get("emailId"));
        assertEquals(postNominals, profile.get("postNominals"));
        assertEquals(initials, profile.get("initials"));

        if (personalCode != null) {
            assertEquals(personalCode, profile.get("personalCode"));
        }
    }

    private void cleanupData() {
        authorisationsRepository.deleteAll();
        appointmentsRepository.deleteAll();
        locationRepository.deleteAll();
        baseLocationRepository.deleteAll();
        profileRepository.deleteAll();
    }

    private void resetAuthenticationState() {
        judicialReferenceDataClient.clearTokens();
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }
}