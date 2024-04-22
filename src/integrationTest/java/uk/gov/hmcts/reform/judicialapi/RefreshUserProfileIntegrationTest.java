package uk.gov.hmcts.reform.judicialapi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.util.JudicialReferenceDataClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.util.FeatureConditionEvaluation.FORBIDDEN_EXCEPTION_LD;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.ATLEAST_ONE_PARAMETER_REQUIRED;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.COMMA_SEPARATED_AND_ALL_NOT_ALLOWED;

class RefreshUserProfileIntegrationTest extends AuthorizationEnabledIntegrationTest {

    private static RefreshRoleRequest refreshRoleRequest;

    @BeforeEach
    void setUp() {
        refreshRoleRequest = new RefreshRoleRequest("BFA1",
                List.of("aa57907b-6d8f-4d2a-9950-7dde95059d05"),
                List.of("ba57907b-6d8f-4d2a-9950-7dde95059d06"),
                List.of("ba57907b-6d8f-4d2a-9950-7dde95059d06"));
    }

    @DisplayName("AC2 - Scenario-Retrieve using ObjectId")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn_200_ValidParameters_objectIds_01(String role) {
        mockJwtToken(role);
        RefreshRoleRequest refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(List.of("d4774030-32cc-4b64-894f-d475b0b1129c"))
                .build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 10,
                0, "ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");

        assertThat(userProfileList).hasSize(1);

        var values = (LinkedHashMap<String, Object>) userProfileList.get(0);

        assertThat((List<?>) values.get("appointments")).hasSize(1);
        assertThat((List<?>) values.get("authorisations")).hasSize(1);
        var appointment = (LinkedHashMap<String, Object>) ((List<?>) values.get("appointments")).get(0);
        assertThat((List<?>) appointment.get("roles")).isEmpty();

    }

    @DisplayName("AC3  - Scenario-Retrieve based on SIDAM ID(s)")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn_200_ValidParameters_sidamIds_01(String role) {
        mockJwtToken(role);
        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(List.of("1111"))
                .objectIds(Collections.emptyList())
                .build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 20,
                0, "ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(1);

        var values = (LinkedHashMap<String, Object>) userProfileList.get(0);
        assertThat((List<?>) values.get("appointments")).hasSize(1);
        assertThat((List<?>) values.get("authorisations")).hasSize(1);
        var appointment = (LinkedHashMap<String, Object>) ((List<?>) values.get("appointments")).get(0);
        assertThat((List<?>) appointment.get("roles")).hasSize(2);
    }


    @DisplayName("AC4  - Scenario- Get Bad Request when all params are empty")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn_200_ValidParameters_ccdPageSize(String role) {
        mockJwtToken(role);
        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(Collections.emptyList())
                .build();
        // pageSize 2
        var errorResponseMap = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 2,
                0, "", "objectId", role, false);
        assertThat(errorResponseMap).containsEntry("http_status", "400");
        assertThat((String) errorResponseMap.get("response_body"))
                .contains(ATLEAST_ONE_PARAMETER_REQUIRED);

    }

    @DisplayName("AC6 - Scenario: Full list of Judicial user details is sorted based on the sort_column")
    @ParameterizedTest
    @CsvSource({
            "jrd-system-user,ASC",
            "jrd-admin,ASC",
            "jrd-admin,DESC"
    })
    void shouldReturn_200_ValidParameters_sorted(String role, String sortDirection) {
        mockJwtToken(role);
        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(List.of("1111122223333"))
                .build();
        // sort rows in ascending order
        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 3,
                0, sortDirection, "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(3);

    }

    @DisplayName("AC9 - Scenario: Get Bad Request when no parameter is passed")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn_400_ValidParameters_ccdServiceEmpty(String role) {
        mockJwtToken(role);
        refreshRoleRequest = RefreshRoleRequest.builder().build();

        var errorResponseMap = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 3,
                0, "ASC", "objectId", role, false);
        assertThat(errorResponseMap).containsEntry("http_status", "400");
        assertThat((String) errorResponseMap.get("response_body"))
                .contains(ATLEAST_ONE_PARAMETER_REQUIRED);

    }

    @DisplayName("Get Bad Request When CCDServiceName Contain Comma Separated")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-admin"})
    void shouldReturn_400_ValidParameters_ResponseHeader(String role) {
        mockJwtToken(role);
        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("ALL,all")
                .sidamIds(List.of(""))
                .objectIds(Collections.emptyList())
                .build();

        var errorResponseMap = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 3,
                0, "ASC", "objectId", role, false);
        assertThat(errorResponseMap).containsEntry("http_status", "400");
        assertThat((String) errorResponseMap.get("response_body"))
                .contains(COMMA_SEPARATED_AND_ALL_NOT_ALLOWED);
    }

    @DisplayName("AC26 - Scenario : Judge has an Active IAC Appointment with an Active IAC Authorisation")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn_200_ValidParameters_objectIds_02(String role) {
        mockJwtToken(role);
        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(List.of("1111122223333"))
                .build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 10,
                0, "ASC", "objectId", role, false);
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

    @DisplayName("AC27  - Scenario-Retrieve based on Personal Code(s)")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn_200_ValidParameters_personalCodes_01(String role) {
        mockJwtToken(role);
        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(List.of(""))
                .objectIds(Collections.emptyList())
                .personalCodes(List.of("A123"))
                .build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 20,
                0, "ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(1);

        var values = (LinkedHashMap<String, Object>) userProfileList.get(0);
        assertThat((List<?>) values.get("appointments")).hasSize(1);
        assertThat((List<?>) values.get("authorisations")).isEmpty();
    }

    @DisplayName("AC28  - Scenario-Retrieve based on Personal Code(s) return 404")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn_404_InValid_personalCodes_01(String role) {
        mockJwtToken(role);
        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(List.of(""))
                .objectIds(Collections.emptyList())
                .personalCodes(List.of("AAAAAAA"))
                .build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 20,
                0, "ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "404");
    }

    @DisplayName("Scenario-Feature flag is not released")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn403WhenLdFeatureDisabled(String role) {
        var launchDarklyMap = new HashMap<String, String>();
        mockJwtToken(role);
        launchDarklyMap.put("JrdUsersController.refreshUserProfile", "test-jrd-flag");
        when(featureToggleServiceImpl.isFlagEnabled(anyString())).thenReturn(false);
        when(featureToggleServiceImpl.getLaunchDarklyMap()).thenReturn(launchDarklyMap);

        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(List.of("fcb4f03c-4b3f-4c3c-bf3a-662b4557b470"))
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
        mockJwtToken(INVALID_TEST_USER);
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 10,
                0, "ASC", "objectId", INVALID_TEST_USER, false);
        assertThat(response).containsEntry("http_status", "403");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @DisplayName("Scenario-InvalidTokens")
    @Test
    void shouldReturn_401_InvalidTokens() {
        mockJwtToken(INVALID_TEST_USER);
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 10,
                0, "ASC", "objectId", INVALID_TEST_USER, true);
        assertThat(response).containsEntry("http_status", "401");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @DisplayName("Scenario-MultipleParameters")
    @Test
    void shouldReturn_400_WithMultipleParameters() {
        mockJwtToken(JRD_SYSTEM_USER);
        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 10,
                0, "ASC", "objectId", JRD_SYSTEM_USER, false);
        assertThat(response).containsEntry("http_status", "400");
    }

    @DisplayName("Validate soft delete service code scenario")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn_200_ValidParameters_Mrd_Delete_time(String role) {
        mockJwtToken(role);
        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(List.of("74ac97ad"))
                .build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 20,
                0, "ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(1);

        var values = (LinkedHashMap<String, Object>) userProfileList.get(0);
        assertThat((List<?>) values.get("appointments")).hasSize(1);
        assertThat((List<?>) values.get("authorisations")).hasSize(3);
        var appointmentOne = (LinkedHashMap<String, Object>) ((List<?>) values.get("authorisations")).get(0);
        Assertions.assertNull(appointmentOne.get("ticket_code"));
        assertThat((List<?>) appointmentOne.get("service_codes")).isEmpty();
        var appointmentTwo = (LinkedHashMap<String, Object>) ((List<?>) values.get("authorisations")).get(1);
        Assertions.assertEquals("357", appointmentTwo.get("ticket_code"));
        assertThat((List<?>) appointmentTwo.get("service_codes")).toString().equals("BBA3");
        var appointmentThree = (LinkedHashMap<String, Object>) ((List<?>) values.get("authorisations")).get(2);
        Assertions.assertNull(appointmentThree.get("ticket_code"));
        assertThat((List<?>) appointmentThree.get("service_codes")).isEmpty();

    }

    @DisplayName("Non-Tribunal cft region and location")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    void shouldReturn_200_Non_Tribunal_scenario_01(String role) {
        mockJwtToken(role);
        RefreshRoleRequest refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(List.of("d4774030-32cc-4b64-894f-d475b0b1129c"))
                .build();

        var response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 10,
                0, "ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");

        assertThat(userProfileList).hasSize(1);

        var values = (LinkedHashMap<String, Object>) userProfileList.get(0);

        assertThat((List<?>) values.get("appointments")).hasSize(1);
        var appointment = (LinkedHashMap<String, Object>) ((List<?>) values.get("appointments")).get(0);
        Assertions.assertEquals("12", appointment.get("cft_region_id"));
        Assertions.assertEquals("National", appointment.get("cft_region"));
        Assertions.assertEquals("12", appointment.get("location_id"));
        Assertions.assertEquals("National", appointment.get("location"));
    }
}
