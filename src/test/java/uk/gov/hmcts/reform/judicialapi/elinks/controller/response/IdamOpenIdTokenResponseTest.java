package uk.gov.hmcts.reform.judicialapi.elinks.controller.response;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamOpenIdTokenResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class IdamOpenIdTokenResponseTest {

    @Test
    void testRegionMapping() {
        IdamOpenIdTokenResponse idamOpenIdTokenResponse = new IdamOpenIdTokenResponse("access_token");
        assertThat(idamOpenIdTokenResponse.getAccessToken(),is("access_token"));
    }

}
