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
public class AuthorisationsRequest {

    @JsonProperty("jurisdiction")
    private String jurisdiction;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("ticket_id")
    private String ticketCode;
    @JsonProperty("ticket")
    private String ticket;
    @JsonProperty("authorisation_id")
    private String authorisationId;
    @JsonProperty("appointment_id")
    private String appointmentId;
    @JsonProperty("jurisdiction_id")
    private String jurisdictionId;

}