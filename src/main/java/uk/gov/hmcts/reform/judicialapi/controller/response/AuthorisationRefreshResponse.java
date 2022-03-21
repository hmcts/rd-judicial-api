package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthorisationRefreshResponse implements Serializable {

    private String jurisdiction;

    private String ticketDescription;

    private String ticketCode;

    private String serviceCode;

    private String startDate;

    private String endDate;

}
