package uk.gov.hmcts.reform.judicialapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.ResponseTransformerV2;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.launchdarkly.sdk.server.LDClient;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.judicialapi.configuration.RestTemplateConfiguration;
import uk.gov.hmcts.reform.judicialapi.service.impl.FeatureToggleServiceImpl;
import uk.gov.hmcts.reform.judicialapi.wiremock.WireMockExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.util.JwtTokenUtil.decodeJwtToken;
import static uk.gov.hmcts.reform.judicialapi.util.JwtTokenUtil.getUserIdAndRoleFromToken;

@Configuration
@WithTags({@WithTag("testType:Integration")})
@TestPropertySource(properties = {"S2S_URL=http://127.0.0.1:8990", "IDAM_URL:http://127.0.0.1:5001"})
@ContextConfiguration(classes = {RestTemplateConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public abstract class AuthorizationEnabledIntegrationTest extends SpringBootIntegrationTest {

    public static final String JRD_SYSTEM_USER = "jrd-system-user";
    public static final String INVALID_TEST_USER = "test-user-role";
    @RegisterExtension
    protected static final WireMockExtension s2sService = new WireMockExtension(8990);
    @RegisterExtension
    protected static final WireMockExtension sidamService = new WireMockExtension(5001, new JudicialTransformer());
    @RegisterExtension
    protected static final WireMockExtension mockHttpServerForOidc = new WireMockExtension(7001);
    @MockitoBean
    protected FeatureToggleServiceImpl featureToggleServiceImpl;
    protected JudicialReferenceDataClient judicialReferenceDataClient;
    @MockitoBean
    protected JwtDecoder jwtDecoder;
    @MockitoBean
    LDClient ldClient;
    @Autowired
    Flyway flyway;
    @Value("${oidc.expiration}")
    private long expiration;
    @Value("${oidc.issuer}")
    private String issuer;
    @Value("${idam.s2s-authorised.services}")
    private String serviceName;

    public static String getDynamicJwksResponse() throws JOSEException, JsonProcessingException {
        RSAKey rsaKey = KeyGenUtil.getRsaJwk();
        Map<String, List<Map<String, Object>>> body = new LinkedHashMap<>();
        List<Map<String, Object>> keyList = new ArrayList<>();
        keyList.add(rsaKey.toJSONObject());
        body.put("keys", keyList);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(body);
    }

    @BeforeEach
    public void setUpClient() {
        judicialReferenceDataClient = new JudicialReferenceDataClient(port, issuer, expiration, serviceName);
        when(featureToggleServiceImpl.isFlagEnabled(anyString())).thenReturn(true);
        flyway.clean();
        flyway.migrate();
    }

    @BeforeAll
    public void setupIdamStubs() throws Exception {

        s2sService.stubFor(get(urlEqualTo("/details"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("rd_judicial_api")));


        LinkedHashMap<String, Object> data = new LinkedHashMap<>();

        ArrayList<String> roles = new ArrayList<>();
        roles.add("%s");

        data.put("id", "%s");
        data.put("uid", "%s");
        data.put("forename", "Super");
        data.put("surname", "User");
        data.put("email", "super.user@hmcts.net");
        data.put("accountStatus", "active");
        data.put("roles", roles);


        sidamService.stubFor(get(urlPathMatching("/o/userinfo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(WireMockUtil.getObjectMapper().writeValueAsString(data))
                        .withTransformers("user-token-response")));

        mockHttpServerForOidc.stubFor(get(urlPathMatching("/jwks"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(getDynamicJwksResponse())));
    }

    public synchronized void mockJwtToken(String role) {
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

    public static class JudicialTransformer implements ResponseTransformerV2 {


        @Override
        public Response transform(Response response, ServeEvent serveEvent) {

            String formatResponse = response.getBodyAsString();

            String token = serveEvent.getRequest().header("Authorization").firstValue();
            String tokenBody = decodeJwtToken(token.split(" ")[1]);
            var tokenInfo = getUserIdAndRoleFromToken(tokenBody);
            formatResponse = format(formatResponse, tokenInfo.get(1), tokenInfo.get(1), tokenInfo.get(0));

            return Response.Builder.like(response)
                    .but().body(formatResponse)
                    .build();

        }

        @Override
        public String getName() {
            return "user-token-response";
        }

        @Override
        public boolean applyGlobally() {
            return false;
        }
    }
}



