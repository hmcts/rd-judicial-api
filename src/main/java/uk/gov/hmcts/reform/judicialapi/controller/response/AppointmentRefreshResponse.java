package uk.gov.hmcts.reform.judicialapi.controller.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRefreshResponse implements Serializable {


    @JsonProperty("baseLocationId")
    private String baseLocationId;

    @JsonProperty("epimms_id")
    private String epimmsId;

    @JsonProperty("courtName")
    private String courtName;

    @JsonProperty("CFTRegionID")
    private String cftRegionID;

    @JsonProperty("CFTRegion")
    private String cftRegion;

    @JsonProperty("locationId")
    private String locationId;

    @JsonProperty("location")
    private String location;

    @JsonProperty("isPrincipalAppointment")
    private String isPrincipalAppointment;

    @JsonProperty("appointment")
    private String appointment;

    @JsonProperty("appointment_type")
    private String appointmentType;

    @JsonProperty("service_code")
    private String serviceCode;

    @JsonProperty("roles")
    private List<String> roles;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

}
