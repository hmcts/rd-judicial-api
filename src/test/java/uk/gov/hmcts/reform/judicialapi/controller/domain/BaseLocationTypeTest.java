package uk.gov.hmcts.reform.judicialapi.controller.domain;

import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.domain.BaseLocationType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createBaseLocationType;

public class BaseLocationTypeTest {

    @Test
    public void testBaseLocationType() {
        BaseLocationType baseLocationType = createBaseLocationType();

        assertNotNull(baseLocationType);
        assertNotNull(baseLocationType.getAppointments());
        assertThat(baseLocationType.getBaseLocationId()).isEqualTo("0");
        assertThat(baseLocationType.getCourtType()).isEqualTo("Court Type");
        assertThat(baseLocationType.getCourtName()).isEqualTo("Court Name");
        assertThat(baseLocationType.getCircuit()).isEqualTo("Circuit");
        assertThat(baseLocationType.getAreaOfExpertise()).isEqualTo("Area of Expertise");
    }
}
