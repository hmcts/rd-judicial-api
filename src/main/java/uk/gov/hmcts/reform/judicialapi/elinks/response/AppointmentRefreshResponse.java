package uk.gov.hmcts.reform.judicialapi.elinks.response;

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
    
    private String cftRegionID;

    private String cftRegion;
    
    private String isPrincipalAppointment;

    private String appointment;

    private String appointmentType;

    private List<String> serviceCodes;

    private String startDate;

    private String endDate;

    private String appointmentId;

    private String roleNameId;

    private String type;

    private String contractTypeId;

}
