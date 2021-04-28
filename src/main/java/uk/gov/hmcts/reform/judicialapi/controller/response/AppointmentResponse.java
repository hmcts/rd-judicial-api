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
    private String roleDesc_En;
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
        this.roleId = appointment.getRoleType().getRole_id();
        this.roleDesc_En = appointment.getRoleType().getRoleDescEn();
        this.contractTypeId = appointment.getContractType().getContractTypeId();
        this.contractTypeDescEn = appointment.getContractType().getContractTypeDescEn();
        this.baseLocationId = appointment.getBaseLocationType().getBaseLocationId();
        this.regionId = appointment.getRegionType().getRegionId();
        this.regionDescEn = appointment.getRegionType().getRegionDescEn();
        this.isPrincipalAppointment = appointment.getIsPrincipleAppointment().toString();
        this.startDate = appointment.getStartDate().toString();
        this.endDate = appointment.getEndDate().toString();
    }


}
