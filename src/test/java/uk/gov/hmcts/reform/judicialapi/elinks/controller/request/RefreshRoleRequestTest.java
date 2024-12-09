package uk.gov.hmcts.reform.judicialapi.elinks.controller.request;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RefreshRoleRequestTest {

    @Test
    void testRegionMapping() {
        RefreshRoleRequest refreshRoleRequest = RefreshRoleRequest.builder()
            .ccdServiceNames("abc")
            .objectIds(List.of("obj1"))
            .personalCodes(List.of("p1"))
            .sidamIds(List.of("s1"))
            .build();

        assertThat(refreshRoleRequest.getCcdServiceNames(), is("abc"));
        assertThat(refreshRoleRequest.getObjectIds().size(),is(1));
        assertThat(refreshRoleRequest.getPersonalCodes().size(),is(1));
        assertThat(refreshRoleRequest.getSidamIds().size(),is(1));
    }

}
