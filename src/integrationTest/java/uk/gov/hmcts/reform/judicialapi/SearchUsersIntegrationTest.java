package uk.gov.hmcts.reform.judicialapi;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchUsersIntegrationTest extends AuthorizationEnabledIntegrationTest {

    private UserSearchRequest userSearchRequest;

    @Before
    public void setUp() {
        super.setUpClient();
    }

    @Test
    public void shouldReturn200WhenUserProfileRequestedForGivenSearchString() {
        userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, "jrd-system-user", false);
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(3, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertEquals("test529@test.net", profiles.get(1).get("emailId"));
        assertEquals("test530@test.net", profiles.get(2).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Test
    public void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndServiceCodeAndLocation() {
        userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location("20013")
                .serviceCode("BBA3")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, "jrd-system-user", false);
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Test
    public void shouldReturn200AndIgnoreLocationWhenServiceCodeIsBfa1() {
        userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location("20012")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, "jrd-system-user", false);
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(2, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertEquals("test530@test.net", profiles.get(1).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Test
    public void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndLocation() {
        userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location("20012")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, "jrd-system-user", false);
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Test
    public void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndServiceCode() {
        userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, "jrd-system-user", false);
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(2, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertEquals("test530@test.net", profiles.get(1).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Test
    public void shouldReturn401ForInvalidTokens() {
        userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, "jrd-system-user", true);
        assertThat(response).containsEntry("http_status", "401");
    }

    @Test
    public void shouldReturn400WhenSearchStringIsEmpty() {
        userSearchRequest = UserSearchRequest.builder()
                .searchString("")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("cannot be empty"));
    }

    @Test
    public void shouldReturn400WhenSearchStringDoesNotContainRequiredLength() {
        userSearchRequest = UserSearchRequest.builder()
                .searchString("te")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("should have atleast 3 characters"));
    }

    @Test
    public void shouldReturn400WhenSearchStringContainsOtherThanLetters() {
        userSearchRequest = UserSearchRequest.builder()
                .searchString("test123")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("should contains letters only"));
    }
}
