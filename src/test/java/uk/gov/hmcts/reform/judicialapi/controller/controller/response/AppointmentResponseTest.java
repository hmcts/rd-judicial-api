package uk.gov.hmcts.reform.judicialapi.controller.controller.response;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.controller.response.AppointmentResponse;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createAppointment;

public class AppointmentResponseTest {


    @Test
    public void test_AppointmentResponseTest() {
        Appointment appointment = createAppointment();

        AppointmentResponse appointmentResponse = new AppointmentResponse(appointment);

        assertThat(appointmentResponse.getAppointmentId()).isEqualTo(appointment.getOfficeAppointmentId().toString());
        assertThat(appointmentResponse.getBaseLocationId())
                .isEqualTo(appointment.getBaseLocationType().getBaseLocationId());
        assertThat(appointmentResponse.getIsPrincipalAppointment())
                .isEqualTo(appointment.getIsPrincipleAppointment().toString());
        assertThat(appointmentResponse.getStartDate()).isEqualTo(appointment.getStartDate().toString());
        assertThat(appointmentResponse.getEndDate()).isEqualTo(appointment.getEndDate().toString());
        assertThat(appointmentResponse.getRegionId()).isEqualTo(appointment.getRegionType().getRegionId());
        assertThat(appointmentResponse.getRegionDescEn()).isEqualTo(appointment.getRegionType().getRegionDescEn());
    }

    @Test
    public void test_AppointmentResponseSetter() {
        AppointmentResponse appointmentResponse = new AppointmentResponse();

        appointmentResponse.setAppointmentId("appointmentId");
        appointmentResponse.setBaseLocationId("baseLocationId");
        appointmentResponse.setIsPrincipalAppointment("true");
        appointmentResponse.setStartDate("01/01/2021");
        appointmentResponse.setEndDate("01/01/2021");
        appointmentResponse.setRoleId("roleId");
        appointmentResponse.setRoleDescEn("roleDescEn");
        appointmentResponse.setContractTypeId("contractTypeId");
        appointmentResponse.setContractTypeDescEn("contractTypeDescEn");
        appointmentResponse.setRegionId("regionId");
        appointmentResponse.setRegionDescEn("regionDescEn");

        assertThat(appointmentResponse.getAppointmentId()).isEqualTo("appointmentId");
        assertThat(appointmentResponse.getBaseLocationId()).isEqualTo("baseLocationId");
        assertThat(appointmentResponse.getIsPrincipalAppointment()).isEqualTo("true");
        assertThat(appointmentResponse.getStartDate()).isEqualTo("01/01/2021");
        assertThat(appointmentResponse.getEndDate()).isEqualTo("01/01/2021");
        assertThat(appointmentResponse.getRoleId()).isEqualTo("roleId");
        assertThat(appointmentResponse.getRoleDescEn()).isEqualTo("roleDescEn");
        assertThat(appointmentResponse.getContractTypeId()).isEqualTo("contractTypeId");
        assertThat(appointmentResponse.getContractTypeDescEn()).isEqualTo("contractTypeDescEn");
        assertThat(appointmentResponse.getRegionId()).isEqualTo("regionId");
        assertThat(appointmentResponse.getRegionDescEn()).isEqualTo("regionDescEn");
    }
}
