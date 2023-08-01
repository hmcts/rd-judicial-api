package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CheckForSearchV2UsersIntegrationTest extends AuthorizationEnabledIntegrationTest {

    @Disabled("Skipped")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchString(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(3, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertEquals("Ms", profiles.get(0).get("postNominals"));
        assertEquals("B.K", profiles.get(0).get("initials"));
        assertEquals("test528@test.net", profiles.get(1).get("emailId"));
        assertEquals("test529@test.net", profiles.get(2).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Disabled("Skipped")
    @ParameterizedTest
    @CsvSource({ "jrd-system-user,20013","jrd-system-user,200134","jrd-admin,20013","jrd-admin,200136"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndServiceCodeAndLocation(String role,
                                                                                              String location) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location(location)
                .serviceCode("BBA3")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(2, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertEquals("S.K", profiles.get(0).get("initials"));
        assertEquals("Mr", profiles.get(0).get("postNominals"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Disabled("Skipped")
    @ParameterizedTest
    @CsvSource({ "jrd-system-user,20012","jrd-system-user,200123","jrd-admin,20012","jrd-admin,200124"})
    void shouldReturn200AndIgnoreLocationWhenServiceCodeIsBfa1(String role, String location) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location(location)
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(2, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertEquals("B.K", profiles.get(0).get("initials"));
        assertEquals("Ms", profiles.get(0).get("postNominals"));
        assertEquals("test528@test.net", profiles.get(1).get("emailId"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Disabled("Skipped")
    @ParameterizedTest
    @CsvSource({ "jrd-system-user,20012","jrd-system-user,200123","jrd-admin,20012","jrd-admin,200124"})
    void shouldReturn200AndIgnoreLocationWhenServiceCodeIsAaa6(String role, String location) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
            .searchString("test")
            .location(location)
            .serviceCode("AAA6")
            .build();
        var response = judicialReferenceDataClient.searchUsers(
            userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertEquals("S.K", profiles.get(0).get("initials"));
        assertEquals("Mr", profiles.get(0).get("postNominals"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Disabled("Skipped")
    @ParameterizedTest
    @CsvSource({ "jrd-system-user,20012","jrd-system-user,200123","jrd-admin,20012","jrd-admin,200124"})
    void shouldReturn200AndIgnoreLocationWhenServiceCodeIsAaa7(String role, String location) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
            .searchString("test")
            .location(location)
            .serviceCode("AAA7")
            .build();
        var response = judicialReferenceDataClient.searchUsers(
            userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertEquals("S.K", profiles.get(0).get("initials"));
        assertEquals("27", profiles.get(0).get("personalCode"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Disabled("Skipped")
    @ParameterizedTest
    @CsvSource({ "jrd-system-user,20012","jrd-system-user,200123","jrd-admin,20012","jrd-admin,200124"})
    void shouldReturn200AndIgnoreLocationWhenServiceCodeIsAba5(String role, String location) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
            .searchString("test")
            .location(location)
            .serviceCode("ABA5")
            .build();
        var response = judicialReferenceDataClient.searchUsers(
            userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertEquals("S.K", profiles.get(0).get("initials"));
        assertEquals("27", profiles.get(0).get("personalCode"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Disabled("Skipped")
    @ParameterizedTest
    @CsvSource({ "jrd-system-user,20012","jrd-system-user,200123","jrd-admin,20012","jrd-admin,200124"})
    void shouldReturn200AndIgnoreLocationWhenServiceCodeIsAba3(String role, String location) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
            .searchString("test")
            .location(location)
            .serviceCode("ABA3")
            .build();
        var response = judicialReferenceDataClient.searchUsers(
            userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test528@test.net", profiles.get(0).get("emailId"));
        assertEquals("S.K", profiles.get(0).get("initials"));
        assertEquals("27", profiles.get(0).get("personalCode"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Disabled("Skipped")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndLocation(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location("20012")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(1, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertEquals("B.K", profiles.get(0).get("initials"));
        assertEquals("Ms", profiles.get(0).get("postNominals"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Disabled("Skipped")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndServiceCode(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(2, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertEquals("B.K", profiles.get(0).get("initials"));
        assertEquals("Ms", profiles.get(0).get("postNominals"));
        assertEquals("test528@test.net", profiles.get(1).get("emailId"));
        assertEquals("S.K", profiles.get(1).get("initials"));
        assertEquals("Mr", profiles.get(1).get("postNominals"));
        assertThat(response).containsEntry("http_status", "200 OK");
    }


    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn401ForInvalidTokens(String role) {
        judicialReferenceDataClient.clearTokens();
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, true, MediaType.valueOf(V2.MediaType.SERVICE));
        assertThat(response).containsEntry("http_status", "401");
    }


    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn400WhenSearchStringIsEmpty(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("cannot be empty"));
    }


    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn400WhenSearchStringDoesNotContainRequiredLength(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("te")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("searchString must be at least 3 characters including letters, "
                + "apostrophe, hyphen"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn400WhenSearchStringContainsOtherThanLetters(String role) {
        mockJwtToken(role);
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test123")
                .location("location")
                .serviceCode("BFA1")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        assertThat(response).containsEntry("http_status", "400");
        var responseBody = (String) response.get("response_body");
        assertTrue(responseBody.contains("searchString must be at least 3 characters including letters, "
                + "apostrophe, hyphen"));
    }

    @Disabled("Skipped")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn200WhenUserProfileRequestedForGivenSearchStringWithEmptyAdditionalBoolean(String role) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                .searchString("test")
                .build();
        var response = judicialReferenceDataClient.searchUsers(
                userSearchRequest, role, false, MediaType.valueOf(V2.MediaType.SERVICE));
        var profiles = (List<Map<String, String>>)response.get("body");
        assertEquals(3, profiles.size());
        assertEquals("test530@test.net", profiles.get(0).get("emailId"));
        assertEquals("test528@test.net", profiles.get(1).get("emailId"));
        assertEquals("test529@test.net", profiles.get(2).get("emailId"));
        assertEquals("29", profiles.get(0).get("personalCode"));
        assertEquals("27", profiles.get(1).get("personalCode"));
        assertEquals("28", profiles.get(2).get("personalCode"));

        assertThat(response).containsEntry("http_status", "200 OK");
    }
}
