package uk.gov.hmcts.reform.judicialapi;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchUsersIntegrationTest extends AuthorizationEnabledIntegrationTest {

    private UserSearchRequest userSearchRequest;

    @Before
    public void setUp() {
        super.setUpClient();
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
