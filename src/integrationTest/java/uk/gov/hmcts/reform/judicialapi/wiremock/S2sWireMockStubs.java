package uk.gov.hmcts.reform.judicialapi.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class S2sWireMockStubs {

    public static final String RD_JUDICIAL_API = "rd_judicial_api";

    private S2sWireMockStubs() {
    }

    public static void registerDefaults(WireMockServer server) {
        server.stubFor(get(urlEqualTo("/details"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(RD_JUDICIAL_API)));
    }
}
