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
public class AuthorisationsRequest {

    @JsonProperty("per_id")
    private Integer perId;
    @JsonProperty("jurisdiction")
    private String jurisdiction;
    @JsonProperty("lower_level")
    private String lowerLevel;
    @JsonProperty("ticket_code")
    private String ticketCode;
    @JsonProperty("personal_code")
    private String personalCode;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("ticket_id")
    private String ticketId;
    @JsonProperty("object_id")
    private String objectId;

}