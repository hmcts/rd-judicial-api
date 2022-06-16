package uk.gov.hmcts.reform.judicialapi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SearchUsersIntegrationTest extends AuthorizationEnabledIntegrationTest {

    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchString(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false);
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(3, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertEquals("test529@test.net", profiles.get(1).get("emailId"));
        assertEquals("test530@test.net", profiles.get(2).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndServiceCodeAndLocation(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location("20013")
                .serviceCode("BBA3")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false);
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn200AndIgnoreLocationWhenServiceCodeIsBfa1(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location("20012")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false);
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(2, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertEquals("test530@test.net", profiles.get(1).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndLocation(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location("20012")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false);
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndServiceCode(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false);
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(2, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertEquals("test530@test.net", profiles.get(1).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn401ForInvalidTokens(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, true);
        assertThat(response).containsEntry("http_status", "401");
    }

    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn400WhenSearchStringIsEmpty(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false);
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("cannot be empty"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn400WhenSearchStringDoesNotContainRequiredLength(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("te")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false);
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("should have atleast 3 characters"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn400WhenSearchStringContainsOtherThanLetters(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test123")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false);
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("should contains letters only"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringWithEmptyAdditionalBoolean(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false);
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(3, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertEquals("test529@test.net", profiles.get(1).get("emailId"));
        assertEquals("test530@test.net", profiles.get(2).get("emailId"));
        assertEquals("27", profiles.get(0).get("personalCode"));
        assertEquals("28", profiles.get(1).get("personalCode"));
        assertEquals("29", profiles.get(2).get("personalCode"));

        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringWithEmptyAdditionalBoolean02(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("sample")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false);
        var profiles = (List<Map<String, String>>)response.get("body");
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


}
