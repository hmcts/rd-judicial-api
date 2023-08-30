package uk.gov.hmcts.reform.judicialapi.elinks.controller.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentsRequest {

    @JsonProperty("base_location_id")
    private String baseLocationId;
    //TBC
    @JsonProperty("circuit")
    private String circuit;
    //TBC
    @JsonProperty("location")
    private String location;
    @JsonProperty("is_principal")
    private Boolean isPrincipleAppointment;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("type")
    private String type;
    @JsonProperty("appointment_id")
    private String appointmentId;
    @JsonProperty("contract_type")
    private String contractType;
    @JsonProperty("role_name")
    private String roleName;
    @JsonProperty("role_name_id")
    private String roleNameId;
    @JsonProperty("contract_type_id")
    private String contractTypeId;
}
