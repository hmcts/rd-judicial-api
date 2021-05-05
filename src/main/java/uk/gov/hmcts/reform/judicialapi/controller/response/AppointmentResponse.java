package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentResponse {

    @JsonProperty
    private String appointmentId;
    @JsonProperty
    private String roleId;
    @JsonProperty
    private String roleDescEn;
    @JsonProperty
    private String contractTypeId;
    @JsonProperty
    private String contractTypeDescEn;
    @JsonProperty
    private String baseLocationId;
    @JsonProperty
    private String regionId;
    @JsonProperty
    private String regionDescEn;
    @JsonProperty
    private String isPrincipalAppointment;
    @JsonProperty
    private String startDate;
    @JsonProperty
    private String endDate;

    public AppointmentResponse(Appointment appointment) {
        this.appointmentId = appointment.getOfficeAppointmentId().toString();
        this.roleId = isNull(appointment.getRoleType()) ? "" : appointment.getRoleType().getRoleId();
        this.roleDescEn = isNull(appointment.getRoleType()) ? "" : appointment.getRoleType().getRoleDescEn();
        this.contractTypeId = isNull(appointment.getContractType()) ? "" :
                appointment.getContractType().getContractTypeId();
        this.contractTypeDescEn = isNull(appointment.getContractType()) ? "" :
                appointment.getContractType().getContractTypeDescEn();
        this.baseLocationId = isNull(appointment.getBaseLocationType()) ? "" :
                appointment.getBaseLocationType().getBaseLocationId();
        this.regionId = isNull(appointment.getRegionType()) ? "" : appointment.getRegionType().getRegionId();
        this.regionDescEn = isNull(appointment.getRegionType()) ? "" : appointment.getRegionType().getRegionDescEn();
        this.isPrincipalAppointment = isNull(appointment.getIsPrincipleAppointment()) ? "" :
                appointment.getIsPrincipleAppointment().toString();
        this.startDate = isNull(appointment.getStartDate()) ? "" : appointment.getStartDate().toString();
        this.endDate = isNull(appointment.getEndDate()) ? "" : appointment.getEndDate().toString();
    }


}
