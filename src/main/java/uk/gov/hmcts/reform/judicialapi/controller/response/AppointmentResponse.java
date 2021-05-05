package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.StringUtils.EMPTY;

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
        
        if(nonNull(appointment)) {
            this.appointmentId = String.valueOf(appointment.getOfficeAppointmentId());
            this.roleId = isNull(appointment.getRoleType()) ? EMPTY : appointment.getRoleType().getRoleId();
            this.roleDescEn = isNull(appointment.getRoleType()) ? EMPTY : appointment.getRoleType().getRoleDescEn();
            this.contractTypeId = isNull(appointment.getContractType()) ? EMPTY :
                appointment.getContractType().getContractTypeId();
            this.contractTypeDescEn = isNull(appointment.getContractType()) ? EMPTY :
                appointment.getContractType().getContractTypeDescEn();
            this.baseLocationId = isNull(appointment.getBaseLocationType()) ? EMPTY :
                appointment.getBaseLocationType().getBaseLocationId();
            this.regionId = isNull(appointment.getRegionType()) ? EMPTY : appointment.getRegionType().getRegionId();
            this.regionDescEn = isNull(appointment.getRegionType()) ? EMPTY : appointment.getRegionType().getRegionDescEn();
            this.isPrincipalAppointment = isNull(appointment.getIsPrincipleAppointment()) ? EMPTY :
                appointment.getIsPrincipleAppointment().toString();
            this.startDate = isNull(appointment.getStartDate()) ? EMPTY : appointment.getStartDate().toString();
            this.endDate = isNull(appointment.getEndDate()) ? EMPTY : appointment.getEndDate().toString();
        }
    }
}
