package uk.gov.hmcts.reform.judicialapi.elinks;

import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ELinksBaseIntegrationTest;

import static org.junit.Assert.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.ElinksControllerIntegrationTest.RECORDS_PER_PAGE;

@TestPropertySource(properties = {
    "elastic.search.recordsPerPage=" + RECORDS_PER_PAGE
})
class ElinksControllerIntegrationTest extends ELinksBaseIntegrationTest {

    public static final int RECORDS_PER_PAGE = 5;
    private static final String ELINKS_SEARCH_USER_URL = "/idam/elastic/search";

    @Test
    void searchUserTest() {
        // When
        Response response = SerenityRest
                .given()
                .relaxedHTTPSValidation()
                .get(ELINKS_SEARCH_USER_URL);

        // Then
        assertNotNull(response);
    }
}
