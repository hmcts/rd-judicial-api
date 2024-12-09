package uk.gov.hmcts.reform.judicialapi.elinks.domain;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RegionMappingTest {

    @Test
    void testRegionMapping() {
        JrdRegionMapping regionMapping = new JrdRegionMapping();
        regionMapping.setRegion("london");
        regionMapping.setRegionId("1");
        regionMapping.setJrdRegion("jrdregion");
        regionMapping.setJrdRegionId("11");

        assertNotNull(regionMapping);
        assertThat(regionMapping.getJrdRegionId(), is("11"));
        assertThat(regionMapping.getJrdRegion(), is("jrdregion"));
        assertThat(regionMapping.getRegion(), is("london"));
    }

}
