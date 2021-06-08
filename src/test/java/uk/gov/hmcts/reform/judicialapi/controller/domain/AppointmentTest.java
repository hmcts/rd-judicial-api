package uk.gov.hmcts.reform.judicialapi.controller.domain;

import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createAppointment;

public class AppointmentTest {

    @Test
    public void testAppointment() {
        Appointment appointment = createAppointment();

        assertNotNull(appointment);
        assertThat(appointment.getOfficeAppointmentId()).isEqualTo(1234567L);
        assertTrue(appointment.getIsPrincipleAppointment());
        assertTrue(appointment.getActiveFlag());
        assertNotNull(appointment.getStartDate());
        assertNotNull(appointment.getEndDate());
        assertNotNull(appointment.getExtractedDate());
        assertNotNull(appointment.getCreatedDate());
        assertNotNull(appointment.getLastLoadedDate());
        assertNotNull(appointment.getUserProfile());
        assertNotNull(appointment.getRegionType());
        assertNotNull(appointment.getRoleType());
        assertNotNull(appointment.getBaseLocationType());
        assertNotNull(appointment.getContractType());
    }
}
