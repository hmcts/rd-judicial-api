package uk.gov.hmcts.reform.judicialapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.util.JudicialReferenceDataClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;

public class RefreshUserProfileIntegrationTest extends AuthorizationEnabledIntegrationTest {

    private RefreshRoleRequest refreshRoleRequest;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
        super.setUpClient();
        refreshRoleRequest = new RefreshRoleRequest("BFA1",
                Arrays.asList("aa57907b-6d8f-4d2a-9950-7dde95059d05"),
                Arrays.asList("aa57907b-6d8f-4d2a-9950-7dde95059d05"));
    }

    private RefreshRoleRequest convertRequestStringToObj(String request) {
        RefreshRoleRequest refreshRoleRequest = null;
        try {
            refreshRoleRequest = mapper.readValue(request, RefreshRoleRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return refreshRoleRequest;
    }

    @Test
    public void shouldReturn_403_UnauthorisedUsers() {
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        Map<String, Object> response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,10,
                0,"ASC", "objectId", "test-user-role", false);
        assertThat(response).containsEntry("http_status", "403");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @Test
    public void shouldReturn_401_InvalidTokens() {
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        Map<String, Object> response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,10,
                0,"ASC", "objectId", "test-user-role", true);
        assertThat(response).containsEntry("http_status", "401");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @Test
    public void shouldReturn_400_WithMultipleParameters() {
        Map<String, Object> response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,10,
                0,"ASC", "objectId", "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "400");
    }

    @Test
    public void shouldReturn_200_ValidParameters_objectIds_01() {
        String request = "{\n"
                + "  \"ccdServiceName\": \"\",\n"
                + "  \"object_ids\": [\n"
                + "    \"1111\"\n"
                + "  ],\n"
                + "  \"sidam_ids\": [\n"
                + "    \"\"\n"
                + "  ]\n"
                + "}";

        refreshRoleRequest = convertRequestStringToObj(request);

        Map<String, Object> response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,10,
                0,"ASC", "objectId", "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "200 OK");

        List<?> userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(1);

        LinkedHashMap<String, Object> values = (LinkedHashMap<String, Object>) userProfileList.get(0);
        assertThat((List<?>) values.get("appointments")).hasSize(1);
        assertThat((List<?>) values.get("authorisations")).hasSize(1);

    }

    @Test
    public void shouldReturn_200_ValidParameters_sidamIds_01() {
        String request = "{\n"
                + "  \"ccdServiceName\": \"\",\n"
                + "  \"object_ids\": [\n"
                + "    \"\"\n"
                + "  ],\n"
                + "  \"sidam_ids\": [\n"
                + "    \"1111\"\n"
                + "  ]\n"
                + "}";

        refreshRoleRequest = convertRequestStringToObj(request);

        Map<String, Object> response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,20,
                0,"ASC", "objectId", "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "200 OK");

        List<?> userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(1);

        LinkedHashMap<String, Object> values = (LinkedHashMap<String, Object>) userProfileList.get(0);
        assertThat((List<?>) values.get("appointments")).hasSize(1);
        assertThat((List<?>) values.get("authorisations")).hasSize(1);
    }

    @Test
    public void shouldReturn_200_ValidParameters_objectIds_02() {
        String request = "{\n"
                + "  \"ccdServiceName\": \"\",\n"
                + "  \"object_ids\": [\n"
                + "    \"1111122223333\"\n"
                + "  ],\n"
                + "  \"sidam_ids\": [\n"
                + "    \"\"\n"
                + "  ]\n"
                + "}";

        refreshRoleRequest = convertRequestStringToObj(request);

        Map<String, Object> response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,10,
                0,"ASC", "objectId", "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "200 OK");

        List<?> userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(2);

        LinkedHashMap<String, Object> values = (LinkedHashMap<String, Object>) userProfileList.get(0);
        values.forEach((key, value) -> {
            if (key.equals("perid") && values.equals(528)) {
                assertThat((List<?>) values.get("appointments")).hasSize(3);
                assertThat((List<?>) values.get("authorisations")).hasSize(2);
            } else {
                assertThat((List<?>) values.get("appointments")).hasSize(1);
                assertThat((List<?>) values.get("authorisations")).hasSize(1);
            }
        });
    }

    @Test
    public void shouldReturn_200_ValidParameters_objectIds_sample_01() {
        String request = "{\n"
                + "  \"ccdServiceName\": \"\",\n"
                + "  \"object_ids\": [\n"
                + "    \"d4774030-32cc-4b64-894f-d475b0b1129c\"\n"
                + "  ],\n"
                + "  \"sidam_ids\": [\n"
                + "    \"\"\n"
                + "  ]\n"
                + "}";

        refreshRoleRequest = convertRequestStringToObj(request);

        Map<String, Object> response = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest, 10,
                0, "ASC", "objectId", "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "200 OK");

        List<?> userProfileList = (List<?>) response.get("body");
        assertThat(userProfileList).hasSize(1);

        LinkedHashMap<String, Object> values = (LinkedHashMap<String, Object>) userProfileList.get(0);
        values.forEach((key, value) -> {
            if (key.equals("perid") && values.equals(40399)) {
                assertThat((List<?>) values.get("appointments")).hasSize(1);
                assertThat((List<?>) values.get("authorisations")).hasSize(1);
            }
        });
    }

}
