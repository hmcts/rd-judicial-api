package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.util.JudicialReferenceDataClient;
import uk.gov.hmcts.reform.judicialapi.versions.V1;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings("checkstyle:Indentation")
class CheckForSearchV2UsersIntegrationTest extends AuthorizationEnabledIntegrationTest {

    @Autowired
    LocationRepository locationRepository;
    @Autowired
    BaseLocationRepository baseLocationRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    AuthorisationsRepository authorisationsRepository;
    @Autowired
    AppointmentsRepository appointmentsRepository;

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchString(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(3, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertEquals("Ms", profiles.get(0).get("postNominals"));
        assertEquals("B.K", profiles.get(0).get("initials"));
        assertEquals("test528@test.net", profiles.get(1).get("emailId"));
        assertEquals("test529@test.net", profiles.get(2).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,BBA3",
                "jrd-admin,BBA3",
                "jrd-system-user,BFA1",
                "jrd-admin,BFA1",})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringForsscsAndIac(String role, String serviceCode) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("one")
                .serviceCode(serviceCode)
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("One531@test.net", profiles.get(0).get("emailId"));
        assertEquals("Mr", profiles.get(0).get("postNominals"));
        assertEquals("J.K", profiles.get(0).get("initials"));
        assertEquals("32", profiles.get(0).get("personalCode"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,BBA3",
                "jrd-admin,BBA3",
                "jrd-system-user,BFA1",
                "jrd-admin,BFA1",})
    void shouldReturn200WhenUserProfileRequestedForSscsAndIacExpired(String role, String serviceCode) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("two")
                .serviceCode(serviceCode)
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(0, profiles.size());
    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,BBA3",
                "jrd-admin,BBA3",
                "jrd-system-user,BFA1",
                "jrd-admin,BFA1",})
    void shouldReturn200WhenUserProfileRequestedForSscsAndIacAppointmentActiveAuthExpired(String role,
                                                                                          String serviceCode) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("three")
                .serviceCode(serviceCode)
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(0, profiles.size());
    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,BBA3",
                "jrd-admin,BBA3",
                "jrd-system-user,BFA1",
                "jrd-admin,BFA1",})
    void shouldReturn200WhenUserProfileRequestedForSscsAndIacAppointmentExpiredAuthActive(String role,
                                                                                          String serviceCode) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("four")
                .serviceCode(serviceCode)
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(0, profiles.size());
    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,BBA3",
                "jrd-admin,BBA3",
                "jrd-system-user,BFA1",
                "jrd-admin,BFA1",})
    void shouldReturn200WhenUserProfileRequestedForSscsAndIacAppointmentActiveSscsAuthActive(String role,
                                                                                             String serviceCode) {

        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("five")
                .serviceCode(serviceCode)
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        if (("BBA3").equals(serviceCode)) {
            assertEquals(1, profiles.size());
        } else if (("BFA1").equals(serviceCode)) {
            assertEquals(0, profiles.size());
        }

    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,BBA3",
                "jrd-admin,BBA3",
                "jrd-system-user,BFA1",
                "jrd-admin,BFA1",})
    void shouldReturn200WhenUserProfileRequestedSscsAppointmentExpiredIacSscsAuthActive(String role,
                                                                                        String serviceCode) {

        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("six")
                .serviceCode(serviceCode)
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        if (("BBA3").equals(serviceCode)) {
            assertEquals(0, profiles.size());
        } else if (("BFA1").equals(serviceCode)) {
            assertEquals(1, profiles.size());
        }

    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,BHA1",
                "jrd-admin,BHA1"})
    void shouldReturn200WhenUserProfileRequestedFamilyAppointmentActiveAuthExpires(String role,
                                                                                   String serviceCode) {

        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("seven")
                .serviceCode(serviceCode)
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());

    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn401ForInvalidTokens(String role) {
        judicialReferenceDataClient.clearTokens();
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, true, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        assertThat(response).containsEntry("http_status", "401");
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn400WhenSearchStringIsEmpty(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("cannot be empty"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn400WhenSearchStringDoesNotContainRequiredLength(String role) {

        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("te")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("searchString must be at least 3 characters including letters, "
                + "apostrophe, hyphen"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenSearchStringContainsOtherThanLetters(String role) {

        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test123")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn400WhenSearchStringContainsOtherThanAccentedCharacters(String role) {

        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("àèÙ{}{}")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("searchString must be at least 3 characters including letters, "
                + "apostrophe, hyphen"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenSearchStringContainsAccentedCharacters(String role) {

        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("àèÙ")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringWithEmptyAdditionalBoolean(String role) {

        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V1.MediaType.SERVICE),
                MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(3, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertEquals("test528@test.net", profiles.get(1).get("emailId"));
        assertEquals("test529@test.net", profiles.get(2).get("emailId"));
        assertEquals("29", profiles.get(0).get("personalCode"));
        assertEquals("27", profiles.get(1).get("personalCode"));
        assertEquals("28", profiles.get(2).get("personalCode"));

        assertThat(response).containsEntry("http_status", "200 OK");
    }

    private void cleanupData() {
        authorisationsRepository.deleteAll();
        appointmentsRepository.deleteAll();
        locationRepository.deleteAll();
        baseLocationRepository.deleteAll();
        profileRepository.deleteAll();
    }
}
