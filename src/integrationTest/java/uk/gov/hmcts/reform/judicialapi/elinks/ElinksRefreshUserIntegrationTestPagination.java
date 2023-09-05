package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksReferenceDataClient;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.ATLEAST_ONE_PARAMETER_REQUIRED;

public class ElinksRefreshUserIntegrationTestPagination {

    private static uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest refreshRoleRequest;

    protected ElinksReferenceDataClient judicialReferenceDataClient;

    @MockBean
    protected JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        refreshRoleRequest = new RefreshRoleRequest("BFA1",
                Arrays.asList("aa57907b-6d8f-4d2a-9950-7dde95059d05"),
                Arrays.asList("ba57907b-6d8f-4d2a-9950-7dde95059d06"),
                Arrays.asList("ba57907b-6d8f-4d2a-9950-7dde95059d06"));
    }

    @DisplayName("AC4  - Scenario- Get Bad Request when all params are empty")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn_200_ValidParameters_ccdPageSize(String role) {
        mockJwtToken(role);
        refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(Collections.emptyList())
                .build();
        // pageSize 2
        var errorResponseMap = judicialReferenceDataClient.refreshUserProfile(refreshRoleRequest,2,
                0,"", "objectId", role, false);
        assertThat(errorResponseMap).containsEntry("http_status", "400");
        assertThat((String) errorResponseMap.get("response_body"))
                .contains(ATLEAST_ONE_PARAMETER_REQUIRED);

    }

    @DisplayName("AC2 - Scenario-Retrieve using ObjectId")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user","jrd-admin"})
    void shouldReturn_200_ValidParameters_objectIds_01(String role) {
        mockJwtToken(role);
        uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest refreshRoleRequest =
                uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest.builder()
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
        mockJwtToken(role);
        refreshRoleRequest = uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest.builder()
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


    private void mockJwtToken(String role) {
        judicialReferenceDataClient.clearTokens();
        String bearerToken = judicialReferenceDataClient.getAndReturnBearerToken(null, role);
        String[] bearerTokenArray = bearerToken.split(" ");
        when(jwtDecoder.decode(anyString())).thenReturn(getJwt(role, bearerTokenArray[1]));
    }

    public Jwt getJwt(String role, String bearerToken) {
        return Jwt.withTokenValue(bearerToken)
                .claim("exp", Instant.ofEpochSecond(1985763216))
                .claim("iat", Instant.ofEpochSecond(1985734416))
                .claim("token_type", "Bearer")
                .claim("tokenName", "access_token")
                .claim("expires_in", 28800)
                .header("kid", "b/O6OvVv1+y+WgrH5Ui9WTioLt0=")
                .header("typ", "RS256")
                .header("alg", "RS256")
                .build();
    }
}
