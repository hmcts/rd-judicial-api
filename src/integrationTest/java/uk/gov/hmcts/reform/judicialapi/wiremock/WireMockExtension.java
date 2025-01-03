package uk.gov.hmcts.reform.judicialapi.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.ResponseTransformerV2;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockExtension extends WireMockServer implements BeforeAllCallback, AfterAllCallback {

    public WireMockExtension(int port) {
        super(port);
    }

    public WireMockExtension(int port, ResponseTransformerV2 judicialTransformer) {
        super(wireMockConfig().extensions(judicialTransformer).port(port));
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        start();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        stop();
    }

}
