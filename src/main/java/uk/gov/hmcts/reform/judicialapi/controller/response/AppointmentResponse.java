package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;

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
        this.roleId = appointment.getRoleType() == null ? "" : appointment.getRoleType().getRoleId();
        this.roleDescEn = appointment.getRoleType() == null ? "" : appointment.getRoleType().getRoleDescEn();
        this.contractTypeId = appointment.getContractType() == null ? "" :
                appointment.getContractType().getContractTypeId();
        this.contractTypeDescEn = appointment.getContractType() == null ? "" :
                appointment.getContractType().getContractTypeDescEn();
        this.baseLocationId = appointment.getBaseLocationType() == null ? "" :
                appointment.getBaseLocationType().getBaseLocationId();
        this.regionId = appointment.getRegionType() == null ? "" : appointment.getRegionType().getRegionId();
        this.regionDescEn = appointment.getRegionType() == null ? "" : appointment.getRegionType().getRegionDescEn();
        this.isPrincipalAppointment = appointment.getIsPrincipleAppointment() == null ? "" :
                appointment.getIsPrincipleAppointment().toString();
        this.startDate = appointment.getStartDate() == null ? "" : appointment.getStartDate().toString();
        this.endDate = appointment.getEndDate() == null ? "" : appointment.getEndDate().toString();
    }


}
