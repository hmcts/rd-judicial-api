package uk.gov.hmcts.reform.judicialapi.controller.domain;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createAppointment;

class AppointmentTest {

    @Test
    void testAppointment() {
        Appointment appointment = createAppointment();

        assertNotNull(appointment);
        assertEquals(1234567L, appointment.getOfficeAppointmentId());
        assertTrue(appointment.getIsPrincipleAppointment());
        assertTrue(appointment.getActiveFlag());
        assertNotNull(appointment.getStartDate());
        assertNotNull(appointment.getEndDate());
        assertNotNull(appointment.getExtractedDate());
        assertNotNull(appointment.getCreatedDate());
        assertNotNull(appointment.getLastLoadedDate());
        assertNotNull(appointment.getUserProfile());
        assertNotNull(appointment.getRegionType());
        assertNotNull(appointment.getBaseLocationType());
    }
}
