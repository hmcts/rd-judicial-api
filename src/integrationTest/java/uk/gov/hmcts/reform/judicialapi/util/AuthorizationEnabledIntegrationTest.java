package uk.gov.hmcts.reform.judicialapi.util;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.launchdarkly.sdk.server.LDClient;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.judicialapi.configuration.RestTemplateConfiguration;
import uk.gov.hmcts.reform.judicialapi.service.impl.FeatureToggleServiceImpl;

import java.util.LinkedList;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.util.JwtTokenUtil.decodeJwtToken;
import static uk.gov.hmcts.reform.judicialapi.util.JwtTokenUtil.getUserIdAndRoleFromToken;
import static uk.gov.hmcts.reform.judicialapi.util.KeyGenUtil.getDynamicJwksResponse;

@Configuration
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Integration")})
@TestPropertySource(properties = {"S2S_URL=http://127.0.0.1:8990", "IDAM_URL:http://127.0.0.1:5000"})
@ContextConfiguration(classes = {RestTemplateConfiguration.class})
public abstract class AuthorizationEnabledIntegrationTest extends SpringBootIntegrationTest {

    @MockBean
    protected FeatureToggleServiceImpl featureToggleServiceImpl;

    @MockBean
    LDClient ldClient;

    protected JudicialReferenceDataClient judicialReferenceDataClient;

    @Value("${oidc.expiration}")
    private long expiration;
    @Value("${oidc.issuer}")
    private String issuer;
    @Value("${idam.s2s-authorised.services}")
    private String serviceName;

    @ClassRule
    public static WireMockRule s2sService = new WireMockRule(wireMockConfig().port(8990));

    @ClassRule
    public static WireMockRule sidamService = new WireMockRule(wireMockConfig().port(5000)
            .extensions(JudicialTransformer.class));

    @ClassRule
    public static WireMockRule mockHttpServerForOidc = new WireMockRule(wireMockConfig().port(7000));

    @Autowired
    Flyway flyway;

    @Before
    public void setUpClient() {
        JudicialReferenceDataClient.setBearerToken("");
        judicialReferenceDataClient = new JudicialReferenceDataClient(port, issuer, expiration, serviceName);
        when(featureToggleServiceImpl.isFlagEnabled(anyString())).thenReturn(true);
        flyway.clean();
        flyway.migrate();
    }

    @Before
    public void setupIdamStubs() throws Exception {

        s2sService.stubFor(get(urlEqualTo("/details"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("rd_judicial_api")));

        sidamService.stubFor(get(urlPathMatching("/o/userinfo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("{"
                                + "  \"id\": \"%s\","
                                + "  \"uid\": \"%s\","
                                + "  \"forename\": \"Super\","
                                + "  \"surname\": \"User\","
                                + "  \"email\": \"super.user@hmcts.net\","
                                + "  \"accountStatus\": \"active\","
                                + "  \"roles\": ["
                                + "  \"%s\""
                                + "  ]"
                                + "}")
                        .withTransformers("user-token-response")));

        mockHttpServerForOidc.stubFor(get(urlPathMatching("/jwks"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(getDynamicJwksResponse())));
    }

    @After
    public void cleanupTestData() {
        JudicialReferenceDataClient.setBearerToken("");
    }


    public static class JudicialTransformer extends ResponseTransformer {
        @Override
        public Response transform(Request request, Response response, FileSource files, Parameters parameters) {

            String formatResponse = response.getBodyAsString();

            String token = request.getHeader("Authorization");
            String tokenBody = decodeJwtToken(token.split(" ")[1]);
            LinkedList tokenInfo = getUserIdAndRoleFromToken(tokenBody);
            formatResponse = format(formatResponse, tokenInfo.get(1), tokenInfo.get(1), tokenInfo.get(0));

            return Response.Builder.like(response)
                    .but().body(formatResponse)
                    .build();
        }

        @Override
        public String getName() {
            return "user-token-response";
        }

        public boolean applyGlobally() {
            return false;
        }
    }
}



