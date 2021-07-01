package uk.gov.hmcts.reform.judicialapi.controller.domain;

import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.domain.RegionType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createRegionType;

public class RegionTypeTest {

    @Test
    public void testRegionType() {
        RegionType regionType = createRegionType();

        assertNotNull(regionType);
        assertNotNull(regionType.getAppointments());
        assertThat(regionType.getRegionId()).isEqualTo("0");
        assertThat(regionType.getRegionDescEn()).isEqualTo("default");
        assertThat(regionType.getRegionDescCy()).isEqualTo("default");
    }
}
