package uk.gov.hmcts.reform.judicialapi.elinks.controller.request;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class UserSearchRequestTest {

    @Test
    void testRegionMapping() {
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setSearchString("test");
        userSearchRequest.setServiceCode("BHA1");
        userSearchRequest.setLocation("LONDON");

        assertThat(userSearchRequest.getSearchString(), is("test"));
        assertThat(userSearchRequest.getServiceCode(), is("bha1"));
        assertThat(userSearchRequest.getLocation(), is("london"));
    }

}
