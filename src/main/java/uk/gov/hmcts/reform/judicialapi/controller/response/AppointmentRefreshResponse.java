package uk.gov.hmcts.reform.judicialapi.controller.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private String perId;
    @JsonIgnore
    private String officeAppointmentId;
    private String baseLocationId;
    private String epimmsId;
    private String courtName;
    private String regionId;
    private String regionDescEn;
    private String regionDescCy;
    private String locationId;
    private String location;
    private String isPrincipleAppointment;
    private String appointment;
    private String appointmentType;
    private String serviceCode;
    private List<String> roles;
    private String startDate;
    private String endDate;

}
