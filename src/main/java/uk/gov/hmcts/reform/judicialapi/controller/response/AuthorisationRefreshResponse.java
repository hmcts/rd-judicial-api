package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AuthorisationRefreshResponse implements Serializable {

    @JsonProperty("jurisdiction")
    private String jurisdiction;

    @JsonProperty("ticketDescription")
    private String ticketDescription;

    @JsonProperty("ticketCode")
    private String ticketCode;

    @JsonProperty("serviceCode")
    private String serviceCode;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

}
