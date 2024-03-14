package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElinksRefreshUserProfileIntegrationTest extends ElinksEnabledIntegrationTest {

    private static RefreshRoleRequest refreshRoleRequest;

    @MockBean
    LocationReferenceDataFeignClient locationReferenceDataFeignClient;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        refreshRoleRequest = new RefreshRoleRequest("BFA1",
                Arrays.asList("aa57907b-6d8f-4d2a-9950-7dde95059d05"),
                Arrays.asList("ba57907b-6d8f-4d2a-9950-7dde95059d06"),
                Arrays.asList("ba57907b-6d8f-4d2a-9950-7dde95059d06"));
    }

    @DisplayName("Non-Tribunal cft region and location")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user"})
    void shouldReturn_200_Non_Tribunal_scenario_01(String role) throws JsonProcessingException {
        mockLocationReferenceDataFeignClient("BBA2", "ST_CIC");
        mockJwtToken(role);
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
        Assertions.assertEquals("1", appointment.get("cft_region_id"));
    }

    @DisplayName("Non-Tribunal cft region and location")
    @ParameterizedTest
    @ValueSource(strings = { "jrd-system-user"})
    void shouldReturn_200_Non_Tribunal_scenario_cmc(String role) throws JsonProcessingException {
        mockLocationReferenceDataFeignClient("BFA1", "CMC");
        mockJwtToken(role);
        RefreshRoleRequest refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("CMC")
                .sidamIds(Collections.emptyList())
                .objectIds(Collections.emptyList())
                .build();

        var response = elinksReferenceDataClient.refreshUserProfile(refreshRoleRequest,10,
                0,"ASC", "objectId", role, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");

        assertThat(userProfileList).hasSize(6);
    }

    private synchronized void mockJwtToken(String role) {
        elinksReferenceDataClient.clearTokens();
        String bearerToken = elinksReferenceDataClient.getAndReturnBearerToken(null, role);
        String[] bearerTokenArray = bearerToken.split(" ");
        when(jwtDecoder.decode(anyString())).thenReturn(getJwt(role, bearerTokenArray[1]));
    }

    private Jwt getJwt(String role, String bearerToken) {
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

    private void mockLocationReferenceDataFeignClient(String serviceCode,
                                                      String serviceName) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var lrdOrgInfoServiceResponse = new LrdOrgInfoServiceResponse();
        lrdOrgInfoServiceResponse.setServiceCode(serviceCode);
        lrdOrgInfoServiceResponse.setCcdServiceName(serviceName);
        String body = mapper.writeValueAsString(List.of(lrdOrgInfoServiceResponse));
        when(locationReferenceDataFeignClient.getLocationRefServiceMapping(any()))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());
    }
}
