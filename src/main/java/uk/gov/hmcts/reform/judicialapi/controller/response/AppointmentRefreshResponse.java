package uk.gov.hmcts.reform.judicialapi.controller.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AppointmentRefreshResponse implements Serializable {


    private String baseLocationId;

    private String epimmsId;

    private String courtName;

    private String cftRegionID;

    private String cftRegion;

    private String locationId;

    private String location;

    private String isPrincipalAppointment;

    private String appointment;

    private String appointmentType;

    private String serviceCode;

    private List<String> roles;

    private String startDate;

    private String endDate;


}
