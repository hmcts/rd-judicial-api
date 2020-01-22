package uk.gov.hmcts.reform.judicialapi.util;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.judicialapi.persistence.JudicialRoleTypeRepository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@Configuration
@TestPropertySource(properties = {"S2S_URL=http://127.0.0.1:8990","IDAM_URL:http://127.0.0.1:5000"})
public abstract class AuthorizationEnabledIntegrationTest extends SpringBootIntegrationTest {

    @Autowired
    protected JudicialRoleTypeRepository judicialRoleTypeRepository;

    protected JudicialReferenceDataClient judicialReferenceDataClient;

    @Rule
    public WireMockRule s2sService = new WireMockRule(8990);

    @Rule
    public WireMockRule sidamService = new WireMockRule(WireMockConfiguration.options().port(5000)
            .extensions(new ExternalTransformer()));

    @Value("${exui.role.caseworker}")
    protected String caseworker;

    @Value("${exui.role.pui-organisation-manager}")
    protected String puiOrgManager;

    @Before
    public void setUpClient() {
        judicialReferenceDataClient = new JudicialReferenceDataClient(port);
    }

    @Before
    public void setUpWireMock() throws Exception {

        s2sService.stubFor(get(urlEqualTo("/details"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("it")));

        s2sService.stubFor(post(urlEqualTo("/lease"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyZF9wcm9mZXNzaW9uYWxfYXBpIiwiZXhwIjoxNTY0NzU2MzY4fQ.UnRfwq_yGo6tVWEoBldCkD1zFoiMSqqm1rTHqq4f_PuTEHIJj2IHeARw3wOnJG2c3MpjM71ZTFa0RNE4D2AUgA")));

        sidamService.stubFor(get(urlEqualTo("/details"))
                .withHeader("Authorization", containing("caseworker"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{"
                                +  "  \"id\": \"%s\","
                                +  "  \"forename\": \"Fname\","
                                +  "  \"surname\": \"Lname\","
                                +  "  \"email\": \"fname.lname@hmcts.net\","
                                +  "  \"accountStatus\": \"active\","
                                +  "  \"roles\": ["
                                +  "  \"caseworker\""
                                +  "  ]"
                                +  "}")
                        .withTransformers("external_user-token-response")));

        sidamService.stubFor(get(urlEqualTo("/details"))
                .withHeader("Authorization", containing("pui-organisation-manager"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{"
                                +  "  \"id\": \"%s\","
                                +  "  \"forename\": \"Fname\","
                                +  "  \"surname\": \"Lname\","
                                +  "  \"email\": \"fname.lname@hmcts.net\","
                                +  "  \"accountStatus\": \"active\","
                                +  "  \"roles\": ["
                                +  "  \"pui-organisation-manager\""
                                +  "  ]"
                                +  "}")
                        .withTransformers("external_user-token-response")));
    }

    @After
    public void cleanupTestData() {
        judicialRoleTypeRepository.deleteAll();;
    }

    public static class ExternalTransformer extends ResponseTransformer {
        @Override
        public Response transform(Request request, Response response, FileSource files, Parameters parameters) {

            String formatResponse = response.getBodyAsString();

            String token = request.getHeader("Authorization");
            String userId = token.split(" ")[1];

            formatResponse = String.format(formatResponse, userId);

            return Response.Builder.like(response)
                    .but().body(formatResponse)
                    .build();
        }

        @Override
        public String getName() {
            return "external_user-token-response";
        }

        public boolean applyGlobally() {
            return false;
        }
    }
}
