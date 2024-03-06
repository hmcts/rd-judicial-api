package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ElinksRefreshUserProfileIntegrationTest extends ElinksEnabledIntegrationTest {

    private static RefreshRoleRequest refreshRoleRequest;

    @BeforeEach
    void setUp() {
        refreshRoleRequest = new RefreshRoleRequest("BFA1",
                Arrays.asList("aa57907b-6d8f-4d2a-9950-7dde95059d05"),
                Arrays.asList("ba57907b-6d8f-4d2a-9950-7dde95059d06"),
                Arrays.asList("ba57907b-6d8f-4d2a-9950-7dde95059d06"));
    }

    @DisplayName("Non-Tribunal cft region and location")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn_200_Non_Tribunal_scenario_01(String role) {
        RefreshRoleRequest refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("ST_CIC")
                .sidamIds(Collections.emptyList())
                .objectIds(Collections.emptyList())
                .build();

        var response = elinksReferenceDataClient.refreshUserProfile(refreshRoleRequest,10,
                0,"ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");

        assertThat(userProfileList).hasSize(1);

        var values = (LinkedHashMap<String, Object>) userProfileList.get(0);

        assertThat((List<?>) values.get("appointments")).hasSize(1);
        var appointment = (LinkedHashMap<String, Object>)((List<?>) values.get("appointments")).get(0);
        Assertions.assertEquals("12",appointment.get("cft_region_id"));
        Assertions.assertEquals("National",appointment.get("cft_region"));
        Assertions.assertEquals("12",appointment.get("location_id"));
        Assertions.assertEquals("National",appointment.get("location"));
    }
}
