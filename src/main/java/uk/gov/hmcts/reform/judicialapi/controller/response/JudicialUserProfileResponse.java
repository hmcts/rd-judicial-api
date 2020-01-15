package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;

@Getter
@Setter
@NoArgsConstructor
public class JudicialUserProfileResponse {

    @JsonProperty
    private String elinksId;

    @JsonProperty
    private String title;

    @JsonProperty
    private String knownAs;

    @JsonProperty
    private String surname;

    @JsonProperty
    private String fullName;

    @JsonProperty
    private String postNominals;

    @JsonProperty
    private String emailId;

    @JsonProperty
    private LocalDateTime joiningDate;

    @JsonProperty
    private LocalDateTime lastWorkingDate;

    @JsonProperty
    private boolean activeFlag;

    @JsonProperty
    private  List<JudicialOfficeAppointmentResponse> judicialOfficeAppointmentsResponse;

    @JsonProperty
    private List<JudicialOfficeAuthorisationsResponse> judicialOfficeAuthorisationsResponseList;

    public JudicialUserProfileResponse(JudicialUserProfile user) {
        this.elinksId = user.getElinksId();
        this.title = user.getTitle();
        this.knownAs = user.getKnownAs();
        this.surname = user.getSurname();
        this.fullName = user.getFullName();
        this.postNominals = user.getPostNominals();
        this.emailId = user.getEmailId();
        this.joiningDate = user.getJoiningDate();
        this.lastWorkingDate = user.getLastWorkingDate();
        this.activeFlag = user.isActiveFlag();
    }

}