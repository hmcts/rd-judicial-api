package uk.gov.hmcts.reform.judicialapi.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static uk.gov.hmcts.reform.judicialapi.util.SpringBootIntegrationTest.getObjectMapper;

public final class IdamWireMockStubs {

    private static final String IDAM_SEARCHUSERS = "/api/v1/users";
    private static WireMockServer idamMockServer = null;

    private IdamWireMockStubs() {
    }

    public static void registerDefaults(WireMockServer server) {
        idamMockServer = server;
        server.stubFor(
                get(urlPathEqualTo("/o/userinfo"))
                        .atPriority(10)
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(getUserDetailsJson())
                                        .withTransformers("user-token-response")
                        )
        );
    }

    private static String getUserDetailsJson() {
        try {
            LinkedHashMap<String, Object> data = new LinkedHashMap<>();
            data.put("id", "%s");
            data.put("uid", "%s");
            data.put("forename", "Super");
            data.put("surname", "User");
            data.put("email", "dummy@email.com");
            data.put("roles", List.of("%s"));

            return getObjectMapper().writeValueAsString(data);

        } catch (Exception e) {
            throw new IllegalStateException("Unable to create IDAM userinfo response", e);
        }
    }

    public static void stubIdamResponse(final String idamResponseValidationJson,
                                    final HttpStatus httpStatus) {
        if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
            idamMockServer.stubFor(get(urlPathMatching(IDAM_SEARCHUSERS))
                    .willReturn(serverError()
                            .withStatus(httpStatus.value())
                            .withHeader("Content-Type", "application/json")
                            .withBody("Internal server error")
                    ));
        } else {
            idamMockServer.stubFor(get(urlPathMatching(IDAM_SEARCHUSERS))
                    .willReturn(aResponse()
                            .withStatus(httpStatus.value())
                            .withHeader("Content-Type", "application/json")
                            .withHeader("Connection", "close")
                            .withBody(idamResponseValidationJson)
                    ));
        }
    }

    public static void stubIdamElasticSearchResponse(final String idamResponseValidationJson,
                                                 final int pageNumber,
                                                 final HttpStatus httpStatus) {
        if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
            idamMockServer.stubFor(get(urlPathMatching(IDAM_SEARCHUSERS))
                    .willReturn(serverError()
                            .withStatus(httpStatus.value())
                            .withHeader("Content-Type", "application/json")
                            .withBody("Internal server error")
                    ));
        } else {
            idamMockServer.stubFor(get(urlPathMatching(IDAM_SEARCHUSERS))
                    .withId(UUID.randomUUID())
                    .withQueryParam("page", equalTo(String.valueOf(pageNumber)))
                    .withQueryParam("size", equalTo("4"))
                    .withQueryParam("query", equalTo("(roles:judiciary) AND lastModified:>now-12h"))
                    .willReturn(aResponse()
                            .withStatus(httpStatus.value())
                            .withHeader("Content-Type", "application/json")
                            .withHeader("Connection", "close")
                            .withBody(idamResponseValidationJson)));
        }
    }

    public static void stubIdamTokenResponse(final HttpStatus httpStatus) {
        idamMockServer.stubFor(post(urlPathMatching("/o/token"))
                .willReturn(aResponse()
                        .withStatus(httpStatus.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("""
                                  {
                                  "access_token": "12345"
                                   }
                                  """
                        )
                ));
    }
}
