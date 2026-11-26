package uk.gov.hmcts.reform.judicialapi.elinks.response;

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
@com.fasterxml.jackson.databind.annotation.JsonNaming(
    com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy.class)
@tools.jackson.databind.annotation.JsonNaming(
    tools.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthorisationRefreshResponse implements Serializable {

    private String jurisdiction;

    private String ticketDescription;

    private String ticketCode;

    private List<String> serviceCodes;

    private String startDate;

    private String endDate;

    private String appointmentId;

    private String authorisationId;

    private String jurisdictionId;


}
