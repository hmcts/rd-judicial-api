package uk.gov.hmcts.reform.judicialapi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.util.JudicialReferenceDataClient;
import uk.gov.hmcts.reform.judicialapi.versions.V1;

import java.util.List;
import java.util.Map;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SearchUsersIntegrationTest extends AuthorizationEnabledIntegrationTest {

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchString(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(3, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertEquals("test528@test.net", profiles.get(1).get("emailId"));
        assertEquals("test529@test.net", profiles.get(2).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,20013", "jrd-system-user,200134", "jrd-admin,20013", "jrd-admin,200136"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndServiceCodeAndLocation(String role,
                                                                                              String location) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location(location)
                .serviceCode("BBA3")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(2, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,20012", "jrd-system-user,200123", "jrd-admin,20012", "jrd-admin,200124"})
    void shouldReturn200AndIgnoreLocationWhenServiceCodeIsBfa1(String role, String location) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location(location)
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(2, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertEquals("test528@test.net", profiles.get(1).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,20012", "jrd-system-user,200123", "jrd-admin,20012", "jrd-admin,200124"})
    void shouldReturn200AndIgnoreLocationWhenServiceCodeIsAaa6(String role, String location) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location(location)
                .serviceCode("AAA6")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,20012", "jrd-system-user,200123", "jrd-admin,20012", "jrd-admin,200124"})
    void shouldReturn200AndIgnoreLocationWhenServiceCodeIsAaa7(String role, String location) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location(location)
                .serviceCode("AAA7")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test529@test.net", profiles.get(0).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,20012", "jrd-system-user,200123", "jrd-admin,20012", "jrd-admin,200124"})
    void shouldReturn200AndIgnoreLocationWhenServiceCodeIsAba5(String role, String location) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location(location)
                .serviceCode("ABA5")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @CsvSource({"jrd-system-user,20012", "jrd-system-user,200123", "jrd-admin,20012", "jrd-admin,200124"})
    void shouldReturn200AndIgnoreLocationWhenServiceCodeIsAba3(String role, String location) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location(location)
                .serviceCode("ABA3")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndLocation(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location("20012")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndServiceCode(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(2, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertEquals("test528@test.net", profiles.get(1).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
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
                userSearchRequest, role, true,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
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
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
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
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("searchString must be at least 3 characters including letters, "
                + "apostrophe, hyphen"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn400WhenSearchStringContainsOtherThanLetters(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test123")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("searchString must be at least 3 characters including letters, "
                + "apostrophe, hyphen"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringWithEmptyAdditionalBoolean(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
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

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringWithEmptyAdditionalBoolean02(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("sample")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(5, profiles.size());
        assertEquals("test900@test.net", profiles.get(0).get("emailId"));
        assertEquals("300", profiles.get(0).get("personalCode"));
        assertEquals("test900@test.net", profiles.get(1).get("emailId"));
        assertEquals("A123", profiles.get(1).get("personalCode"));

        assertEquals("test901@test.net", profiles.get(2).get("emailId"));
        assertEquals("301", profiles.get(2).get("personalCode"));
        assertEquals("test902@test.net", profiles.get(3).get("emailId"));
        assertEquals("302", profiles.get(3).get("personalCode"));

        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileApostropheString(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("Am'")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test802@test.net", profiles.get(0).get("emailId"));
        assertEquals("Am'ar", profiles.get(0).get("knownAs"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileApostropheStrings(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("O'j")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test803@test.net", profiles.get(0).get("emailId"));
        assertEquals("O'jas", profiles.get(0).get("knownAs"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileHyphenString(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("Li-a")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test804@test.net", profiles.get(0).get("emailId"));
        assertEquals("Li-am", profiles.get(0).get("knownAs"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileHyphenStrings(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("V-e")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test805@test.net", profiles.get(0).get("emailId"));
        assertEquals("V-ed", profiles.get(0).get("knownAs"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileEmptySpace(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("J Ro")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test806@test.net", profiles.get(0).get("emailId"));
        assertEquals("J Rock", profiles.get(0).get("knownAs"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WhenUserProfileEmptySpaces(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("To N")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false,
                MediaType.valueOf(V1.MediaType.SERVICE), MediaType.valueOf(V1.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test807@test.net", profiles.get(0).get("emailId"));
        assertEquals("To Nick", profiles.get(0).get("knownAs"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }
}
