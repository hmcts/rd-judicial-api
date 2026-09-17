package uk.gov.hmcts.reform.judicialapi.elinks.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultsRequest {

    @JsonProperty("personal_code")
    private String personalCode;
    @JsonProperty("known_as")
    private String knownAs;
    @JsonProperty("surname")
    private String surname;
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("post_nominals")
    private String postNominals;
    @JsonProperty("email")
    private String email;
    @JsonProperty("leaving_on")
    private String lastWorkingDate;
    @JsonProperty("id")
    private String objectId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("retirement_date")
    private String retirementDate;
    @JsonProperty("last_updated")
    private String lastUpdated;
    //TBC
    @JsonProperty("initials")
    private String initials;
    @JsonProperty("appointments")
    private List<AppointmentsRequest> appointmentsRequests;
    @JsonProperty("authorisations_with_dates")
    private List<AuthorisationsRequest> authorisationsRequests;
    @JsonProperty("judiciary_roles")
    private List<RoleRequest> judiciaryRoles;
    //Leaversfields
    @JsonProperty("per_id")
    private String perId;
    @JsonProperty("leaver")
    private String leaver;
    @JsonProperty("left_on")
    private String leftOn;
    //deleted fields
    @JsonProperty("deleted")
    private String deleted;
    @JsonProperty("deleted_on")
    private String deletedOn;

}