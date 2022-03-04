package uk.gov.hmcts.reform.judicialapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.util.JudicialReferenceDataClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.HashMap;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static uk.gov.hmcts.reform.judicialapi.util.FeatureConditionEvaluation.FORBIDDEN_EXCEPTION_LD;

class RefreshUserProfileIntegrationTest extends AuthorizationEnabledIntegrationTest {

    private static RefreshRoleRequest refreshRoleRequest;

    @BeforeEach
     void setUp() {
        refreshRoleRequest = new RefreshRoleRequest("BFA1",
                Arrays.asList("aa57907b-6d8f-4d2a-9950-7dde95059d05"),
                Arrays.asList("ba57907b-6d8f-4d2a-9950-7dde95059d06"));
    }

    @DisplayName("AC2 - Scenario-Retrieve using ObjectId")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
     void shouldReturn_200_ValidParameters_objectIds_01(String role) {

        RefreshRoleRequest refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(Arrays.asList("d4774030-32cc-4b64-894f-d475b0b1129c"))
                .build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,10,
                0,"ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");

        assertThat(userProfileList).hasSize(1);

        var values = (LinkedHashMap<String, Object>) userProfileList.get(0);

        assertThat((List<?>) values.get("appointments")).hasSize(1);
        assertThat((List<?>) values.get("authorisations")).hasSize(1);
        var appointment = (LinkedHashMap<String, Object>)((List<?>) values.get("appointments")).get(0);
        assertThat((List<?>)appointment.get("roles")).isEmpty();

    }

    @DisplayName("AC3  - Scenario-Retrieve based on SIDAM ID(s)")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn_200_ValidParameters_sidamIds_01(String role) {

        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Arrays.asList("1111"))
                .objectIds(Collections.emptyList())
                .build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,20,
                0,"ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(1);

        var values = (LinkedHashMap<String, Object>) userProfileList.get(0);
        assertThat((List<?>) values.get("appointments")).hasSize(1);
        assertThat((List<?>) values.get("authorisations")).hasSize(1);
        var appointment = (LinkedHashMap<String, Object>)((List<?>) values.get("appointments")).get(0);
        assertThat((List<?>)appointment.get("roles")).hasSize(2);
    }


    @DisplayName("AC4  - Scenario- Full list of all the Judicial user details is paginated as per the input page size")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn_200_ValidParameters_ccdPageSize(String role) {

        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(Collections.emptyList())
                .build();
        // pageSize 2
        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,2,
                0,"", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(2);

    }

    @DisplayName("AC6 - Scenario: Full list of Judicial user details is sorted based on the sort_column")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn_200_ValidParameters_sorted(String role) {

        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(Collections.emptyList())
                .build();
        // sort rows in ascending order
        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,3,
                0,"ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(3);

    }

    @DisplayName("AC7 - Scenario: Full list of Judicial user is sorted based on the descending order")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-admin"})
    void sortedDescendingOrder(String role) {

        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(Arrays.asList("1111122223333"))
                .build();
        // sort rows in descending order
        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,20,
                0,"DESC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(3);

    }

    @DisplayName("AC8 - Scenario: Full list of Judicial user details is retrieved based on the page_number")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
     void shouldReturn_200_ValidParameters_ccdPageNumber(String role) {

        refreshRoleRequest = RefreshRoleRequest.builder()
                 .ccdServiceNames("")
                 .sidamIds(Arrays.asList(""))
                 .objectIds(Collections.emptyList())
                .build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,3,
                1,"ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(3);

    }

    @DisplayName("AC9 - Scenario: Full list of Judicial user details is retrieved when no parameter is passed")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
     void shouldReturn_200_ValidParameters_ccdServiceEmpty(String role) {

        refreshRoleRequest = RefreshRoleRequest.builder().build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,3,
                0,"ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

    }

    @DisplayName("Scenario-Response header scenario")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-admin"})
    void shouldReturn_200_ValidParameters_ResponseHeader(String role) {

        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Arrays.asList(""))
                .objectIds(Collections.emptyList())
                .build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,3,
                0,"ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");
        String headers = (String) response.get("headers");

        assertTrue(headers.contains("total_records"));
    }

    @DisplayName("AC26 - Scenario : Judge has an Active IAC Appointment with an Active IAC Authorisation")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn_200_ValidParameters_objectIds_02(String role) {

        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(Arrays.asList("1111122223333"))
                .build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,10,
                0,"ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(3);

        var values = (LinkedHashMap<String, Object>) userProfileList.get(0);
        values.forEach((key, value) -> {
            if (key.equals("emailId") && value.equals("test528@test.net")) {
                assertThat((List<?>) values.get("appointments")).hasSize(3);
                assertThat((List<?>) values.get("authorisations")).hasSize(2);
            } else if (key.equals("emailId") && value.equals("test529@test.net")) {
                assertThat((List<?>) values.get("appointments")).hasSize(3);
                assertThat((List<?>) values.get("authorisations")).hasSize(2);
            } else if (key.equals("emailId") && value.equals("test530@test.net")) {
                assertThat((List<?>) values.get("appointments")).hasSize(1);
                assertThat((List<?>) values.get("authorisations")).isEmpty();
            }
        });
    }

    @DisplayName("Scenario-Feature flag is not released")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn403WhenLdFeatureDisabled(String role) {
        var launchDarklyMap = new HashMap<String, String>();
        launchDarklyMap.put("JrdUsersController.refreshUserProfile", "test-jrd-flag");
        when(featureToggleServiceImpl.isFlagEnabled(anyString())).thenReturn(false);
        when(featureToggleServiceImpl.getLaunchDarklyMap()).thenReturn(launchDarklyMap);

        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(Arrays.asList("fcb4f03c-4b3f-4c3c-bf3a-662b4557b470"))
                .build();

        var errorResponseMap = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 10,
                0, "ASC", "objectId", role, false);

        assertThat(errorResponseMap).containsEntry("http_status", "403");
        assertThat((String) errorResponseMap.get("response_body"))
                .contains("test-jrd-flag".concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD));
    }

    @DisplayName("Scenario-UnauthorisedUsers")
    @Test
    void shouldReturn_403_UnauthorisedUsers() {
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,10,
                0,"ASC", "objectId", "test-user-role", false);
        assertThat(response).containsEntry("http_status", "403");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @DisplayName("Scenario-InvalidTokens")
    @Test
    void shouldReturn_401_InvalidTokens() {
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,10,
                0,"ASC", "objectId", "test-user-role", true);
        assertThat(response).containsEntry("http_status", "401");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @DisplayName("Scenario-MultipleParameters")
    @Test
    void shouldReturn_400_WithMultipleParameters() {
        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,10,
                0,"ASC", "objectId", "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "400");
    }
}
