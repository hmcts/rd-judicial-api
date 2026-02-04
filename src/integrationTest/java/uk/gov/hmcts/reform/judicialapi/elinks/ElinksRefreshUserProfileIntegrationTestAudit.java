package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElinksRefreshUserProfileIntegrationTestAudit extends ElinksDataLoadBaseTest {

    private static final String JRD_SYSTEM_USER = "jrd-system-user";
    @MockitoBean
    LocationReferenceDataFeignClient locationReferenceDataFeignClient;

    @BeforeEach
    void setUp() {
        mockJwtToken();
        stubS2SResponse();
        stubIdamUserInfoResponse();
        new RefreshRoleRequest("BFA1",
                List.of("aa57907b-6d8f-4d2a-9950-7dde95059d05"),
                List.of("ba57907b-6d8f-4d2a-9950-7dde95059d06"),
                List.of("ba57907b-6d8f-4d2a-9950-7dde95059d06"));
    }

    @DisplayName("Non-Tribunal cft region and location")
    @ParameterizedTest
    @MethodSource("provideRefreshUserProfileTestData")
    void shouldReturn200NonTribunalScenario(String serviceCode,
                                            String serviceName,
                                            int expectedUserProfilesCount) throws JsonProcessingException {
        mockLocationReferenceDataFeignClient(serviceCode, serviceName);
        RefreshRoleRequest refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames(serviceName)
                .sidamIds(Collections.emptyList())
                .objectIds(Collections.emptyList())
                .build();

        var response = elinksReferenceDataClient.refreshUserProfile(refreshRoleRequest, 10,
                0, "ASC", "objectId", JRD_SYSTEM_USER, false);
        assertThat(response).containsEntry("http_status", "200 OK");

        var userProfileList = (List<?>) response.get("body");

        assertThat(userProfileList).hasSize(expectedUserProfilesCount);
    }

    private void mockJwtToken() {
        elinksReferenceDataClient.clearTokens();
        String bearerToken = elinksReferenceDataClient.getAndReturnBearerToken(null, JRD_SYSTEM_USER);
        String[] bearerTokenArray = bearerToken.split(" ");
        when(jwtDecoder.decode(anyString())).thenReturn(getJwt(bearerTokenArray[1]));
    }

    private Jwt getJwt(String bearerToken) {
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

    private Stream<Arguments> provideRefreshUserProfileTestData() {
        return Stream.of(
                arguments("BBA2", "ST_CIC", 1),
                arguments("BFA1", "CMC", 6)
        );
    }

}
