package uk.gov.hmcts.reform.judicialapi.security;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.util.TestApplicationServer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readString;
import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.ELinksBaseIntegrationTest.stubLocationApiResponse;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest.LOCATION_API_RESPONSE_JSON;
import static uk.gov.hmcts.reform.judicialapi.util.JudicialReferenceDataClient.APP_BASE_PATH;
import static uk.gov.hmcts.reform.judicialapi.util.JudicialReferenceDataClient.getHttpHeaders;

public class BaseSecurityIntegrationTest extends AuthorizationEnabledIntegrationTest {

    protected static final String VALID_ISSUER_1 = "http://localhost:5062/o";
    protected static final String VALID_ISSUER_2 = "https://secondary-idam.platform.hmcts.net";
    protected static final String ROGUE_ISSUER = "https://rogue-issuer.com";
    protected static final UserSearchRequest USER_SEARCH_REQUEST =
            UserSearchRequest.builder().searchString("test").build();

    @Autowired
    private TestApplicationServer testApplicationServer;


    private String value;

    @BeforeEach
    public void setUp() throws IOException {


        final String locationApiResponseJson =
                readString(Paths.get(requireNonNull(this.getClass()
                        .getResource(LOCATION_API_RESPONSE_JSON)).getPath()), UTF_8);

        stubLocationApiResponse(locationApiResponseJson, OK);
    }

    protected RequestSpecification jwtRequest(String issuer, boolean expired) {

        return SerenityRest.given()
                .baseUri(testApplicationServer.getBaseUrl() + APP_BASE_PATH)
                .headers(getHttpHeaders(issuer, expired, UUID.randomUUID().toString(), jrdSystemUser));

    }

    protected RequestSpecification unexpiredJwt(
            String issuer) {

        return jwtRequest(issuer, false);
    }

    protected RequestSpecification expiredJwt(
            String issuer) {

        return jwtRequest(issuer, true);
    }
}
