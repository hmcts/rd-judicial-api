package uk.gov.hmcts.reform.judicialapi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.util.JudicialReferenceDataClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.util.FeatureConditionEvaluation.FORBIDDEN_EXCEPTION_LD;

class FetchUsersIntegrationTest extends AuthorizationEnabledIntegrationTest {

    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest(Arrays.asList("44862987-4b00-e2e7-4ff8-281b87f16bf9",
                "4c0ff6a3-8fd6-803b-301a-29d9dacccca8"));
    }


    @DisplayName("Scenario-Retrieve using one UserRequest")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void retrieveJudicialProfileBasedOnId(String role) {
        mockJwtToken(role);
        userRequest = new UserRequest(List.of("44862987-4b00-e2e7-4ff8-281b87f16bf9"));
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, role, false);
        assertThat(response).containsEntry("http_status", "200 OK");
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());
    }

    @DisplayName("Scenario-Retrieve using multiple UserRequest")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn200WithValidParameters(String role) {
        mockJwtToken(role);
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, role, false);
        assertThat(response).containsEntry("http_status", "200 OK");
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(2, profiles.size());
    }


    @DisplayName("Scenario: Full list of Judicial user details is retrieved based on the page_number")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn_200_ValidParameters_ccdPageNumber(String role) {
        mockJwtToken(role);
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(1, 1,
                userRequest, role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(1, profiles.size());

    }

    @DisplayName("Scenario: Full list of Judicial user details is retrieved based on the pageSize")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn_200_ValidParameters_PageSize(String role) {
        mockJwtToken(role);
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(2, 0,
                userRequest, role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(2, profiles.size());

    }

    @DisplayName("Scenario-Response header  total_records scenario")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-admin"})
    void shouldReturn_200_ValidParameters_ResponseHeader(String role) {
        mockJwtToken(role);
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, role, false);
        assertThat(response).containsEntry("http_status", "200 OK");
        var profiles = (List<Map<String, String>>) response.get("body");
        assertEquals(2, profiles.size());
        String headers = (String) response.get("headers");

        Assertions.assertFalse(headers.contains("total_records"));
    }


    @DisplayName("Scenario-UnauthorisedUsers")
    @Test
    void shouldReturn403ForUnauthorisedUsers() {
        mockJwtToken(INVALID_TEST_USER);
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, INVALID_TEST_USER, false);
        assertThat(response).containsEntry("http_status", "403");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @DisplayName("Scenario-InvalidTokens")
    @Test
    void shouldReturn401ForInvalidTokens() {
        mockJwtToken(JRD_SYSTEM_USER);
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, JRD_SYSTEM_USER, true);
        assertThat(response).containsEntry("http_status", "401");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @DisplayName("Scenario-EmptyUserIds")
    @Test
    void shouldReturn400ForEmptyUserIds() {
        mockJwtToken(JRD_SYSTEM_USER);
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        userRequest = new UserRequest();
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, JRD_SYSTEM_USER, false);
        assertThat(response).containsEntry("http_status", "400");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @DisplayName("Scenario-NoUsersFound")
    @Test
    void shouldReturn404WhenNoUsersFound() {
        mockJwtToken(JRD_SYSTEM_USER);
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        userRequest = new UserRequest(Collections.singletonList(UUID.randomUUID().toString()));

        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, JRD_SYSTEM_USER, false);
        assertThat(response).containsEntry("http_status", "404");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @DisplayName("Scenario-FeatureDisabled")
    @Test
    void shouldReturn403WhenLdFeatureDisabled() {
        Map<String, String> launchDarklyMap = new HashMap<>();
        mockJwtToken(JRD_SYSTEM_USER);
        launchDarklyMap.put("JrdUsersController.fetchUsers", "test-jrd-flag");
        when(featureToggleServiceImpl.isFlagEnabled(anyString())).thenReturn(false);
        when(featureToggleServiceImpl.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        Map<String, Object> errorResponseMap = judicialReferenceDataClient
                .fetchJudicialProfilesById(10, 0,
                        userRequest, JRD_SYSTEM_USER, false);

        assertThat(errorResponseMap).containsEntry("http_status", "403");
        assertThat((String) errorResponseMap.get("response_body"))
                .contains("test-jrd-flag".concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD));
    }
}
