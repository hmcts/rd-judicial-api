package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonParser;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.MapperFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.json.JsonMapper;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ELinksBaseIntegrationTest;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.Assert.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.ElinksControllerIntegrationTest.RECORDS_PER_PAGE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksResponsesHelper.objectMapper;

@TestPropertySource(properties = {
    "elastic.search.recordsPerPage=" + RECORDS_PER_PAGE
})
class ElinksControllerIntegrationTest extends ELinksBaseIntegrationTest {

    public static final int RECORDS_PER_PAGE = 5;
    private static final String ELINKS_SEARCH_USER_URL = "/idam/elastic/search";
    private static final String IDAM_SEARCH_USER_URL = "/api/v1/users";
    public static final ObjectMapper MAPPER = JsonMapper.builder()
            .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();

    @Test
    void searchUserTest() throws JsonProcessingException {
        // Given
        List<IdamResponse> idamResponses = new ArrayList<>();

        String body = MAPPER.writeValueAsString(idamResponses);
        stubSearchUserIdamResponse(body);

        // When
        Response response = SerenityRest
                .given()
                .relaxedHTTPSValidation()
                .get(ELINKS_SEARCH_USER_URL);

        // Then
        assertNotNull(response);
    }

    private void stubSearchUserIdamResponse(String body) {
        sidamService.stubFor(get(urlPathMatching(IDAM_SEARCH_USER_URL))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(body)
                ));
    }
}
