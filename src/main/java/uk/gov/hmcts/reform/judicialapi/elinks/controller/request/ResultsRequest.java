package uk.gov.hmcts.reform.judicialapi.elinks.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultsRequest {

    @JsonProperty("per_id")
    private String perId;
    @JsonProperty("personal_code")
    private String personalCode;
    @JsonProperty("known_as")
    private String knownAs;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("surname")
    private String surname;
    @JsonProperty("post_nominals")
    private String postNominals;
    @JsonProperty("email")
    private String email;
    @JsonProperty("last_working_date")
    private String lastWorkingDate;
    @JsonProperty("object_id")
    private String objectId;
    @JsonProperty("initials")
    private String initials;
    @JsonProperty("appointments")
    private List<AppointmentsRequest> appointmentsRequests;
    @JsonProperty("authorisations_with_dates")
    private List<AuthorisationsRequest> authorisationsRequests;

}