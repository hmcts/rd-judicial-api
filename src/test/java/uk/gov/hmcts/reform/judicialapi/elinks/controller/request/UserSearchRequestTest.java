package uk.gov.hmcts.reform.judicialapi.elinks.controller.request;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JrdRegionMapping;

class UserSearchRequestTest {

    @Test
    void testRegionMapping() {
        UserSearchRequest userSearchRequest=new UserSearchRequest();
        userSearchRequest.setSearchString("test");
        userSearchRequest.setServiceCode("BHA1");
        userSearchRequest.setLocation("LONDON");

        assertThat(userSearchRequest.getSearchString(), is("test"));
        assertThat(userSearchRequest.getServiceCode(), is("bha1"));
        assertThat(userSearchRequest.getLocation(), is("london"));
    }

}
