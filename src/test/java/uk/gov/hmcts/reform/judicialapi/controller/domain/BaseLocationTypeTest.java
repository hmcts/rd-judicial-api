package uk.gov.hmcts.reform.judicialapi.controller.domain;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.domain.BaseLocationType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createBaseLocationType;

class BaseLocationTypeTest {

    @Test
    void testBaseLocationType() {
        BaseLocationType baseLocationType = createBaseLocationType();

        assertNotNull(baseLocationType);
        assertNotNull(baseLocationType.getAppointments());
        assertEquals("0", baseLocationType.getBaseLocationId());
        assertEquals("Court Type", baseLocationType.getCourtType());
        assertEquals("Court Name", baseLocationType.getCourtName());
        assertEquals("Circuit", baseLocationType.getCircuit());
        assertEquals("Area of Expertise", baseLocationType.getAreaOfExpertise());
    }
}
