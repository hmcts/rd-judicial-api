package uk.gov.hmcts.reform.judicialapi.controller.domain;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.domain.RegionType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createRegionType;

class RegionTypeTest {

    @Test
    void testRegionType() {
        RegionType regionType = createRegionType();

        assertNotNull(regionType);
        assertNotNull(regionType.getAppointments());
        assertEquals("0", regionType.getRegionId());
        assertEquals("default", regionType.getRegionDescEn());
        assertEquals("default", regionType.getRegionDescCy());
    }
}
