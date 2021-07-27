package uk.gov.hmcts.reform.judicialapi;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.util.JudicialReferenceDataClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.util.FeatureConditionEvaluation.FORBIDDEN_EXCEPTION_LD;

public class FetchUsersIntegrationTest extends AuthorizationEnabledIntegrationTest {

    private UserRequest userRequest;

    @Before
    public void setUp() {
        super.setUpClient();
        userRequest = new UserRequest(Arrays.asList("44862987-4b00-e2e7-4ff8-281b87f16bf9",
                "4c0ff6a3-8fd6-803b-301a-29d9dacccca8"));
    }

    @Test
    public void shouldReturn200WithValidParameters() {
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Test
    public void shouldReturn403ForUnauthorisedUsers() {
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, "test-user-role", false);
        assertThat(response).containsEntry("http_status", "403");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @Test
    public void shouldReturn401ForInvalidTokens() {
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, "jrd-system-user", true);
        assertThat(response).containsEntry("http_status", "401");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @Test
    public void shouldReturn400ForEmptyUserIds() {
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        userRequest = new UserRequest();
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "400");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @Test
    public void shouldReturn404WhenNoUsersFound() {
        JudicialReferenceDataClient.setBearerToken(EMPTY);
        userRequest = new UserRequest(Collections.singletonList(UUID.randomUUID().toString()));

        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "404");
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @Test
    public void shouldReturn403WhenLdFeatureDisabled() {
        Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put("JrdUsersController.fetchUsers", "test-jrd-flag");
        when(featureToggleServiceImpl.isFlagEnabled(anyString(), anyString())).thenReturn(false);
        when(featureToggleServiceImpl.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        Map<String, Object> errorResponseMap = judicialReferenceDataClient
                .fetchJudicialProfilesById(10, 0,
                        userRequest, "jrd-system-user", false);

        assertThat(errorResponseMap).containsEntry("http_status", "403");
        assertThat((String) errorResponseMap.get("response_body"))
                .contains("test-jrd-flag".concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD));
    }

}
