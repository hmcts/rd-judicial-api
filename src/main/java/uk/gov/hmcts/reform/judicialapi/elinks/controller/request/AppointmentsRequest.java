package uk.gov.hmcts.reform.judicialapi.elinks.controller.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentsRequest {

    @JsonProperty("per_id")
    private String perId;
    @JsonProperty("base_location_id")
    private String baseLocationId;
    @JsonProperty("region_id")
    private String regionId;
    @JsonProperty("is_principle_appointment")
    private Boolean isPrincipleAppointment;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("active_flag")
    private Boolean activeFlag;
    @JsonProperty("personal_code")
    private String personalCode;
    @JsonProperty("object_id")
    private String objectId;
    @JsonProperty("appointment")
    private String appointmentRolesMapping;
    @JsonProperty("appointment_type")
    private String appointmentType;
    @JsonProperty("work_pattern")
    private String workPattern;

}
