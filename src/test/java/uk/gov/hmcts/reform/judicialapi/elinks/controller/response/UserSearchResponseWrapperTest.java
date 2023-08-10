package uk.gov.hmcts.reform.judicialapi.elinks.controller.response;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamOpenIdTokenResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;

class UserSearchResponseWrapperTest {

    @Test
    void testRegionMapping() {
        UserSearchResponseWrapper userSearchResponseWrapper=new UserSearchResponseWrapper();
        /*userSearchResponseWrapper.setTitle();*/
    }

}
