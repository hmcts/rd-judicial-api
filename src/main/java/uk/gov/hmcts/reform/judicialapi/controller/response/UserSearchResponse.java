package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.io.Serializable;

@NoArgsConstructor
@Getter
public class UserSearchResponse implements Serializable {
    @JsonProperty
    private String title;
    @JsonProperty
    private String knownAs;
    @JsonProperty
    private String surname;
    @JsonProperty
    private String fullName;
    @JsonProperty
    private String emailId;
    @JsonProperty
    private String idamId;

    @JsonProperty
    private String personalCode;

    @JsonProperty
    private String isJudge;

    @JsonProperty
    private String isPanelMember;

    @JsonProperty
    private String isMagistrate;

    public UserSearchResponse(UserProfile userProfile) {
        this.title = userProfile.getPostNominals();
        this.knownAs = userProfile.getKnownAs();
        this.surname = userProfile.getSurname();
        this.fullName = userProfile.getFullName();
        this.emailId = userProfile.getEjudiciaryEmailId();
        this.personalCode = userProfile.getPersonalCode();
        this.idamId = userProfile.getSidamId();
        this.isJudge = this.getStringValueFromBoolean(userProfile.getIsJudge());
        this.isPanelMember = this.getStringValueFromBoolean(userProfile.getIsPanelMember());
        this.isMagistrate = this.getStringValueFromBoolean(userProfile.getIsMagistrate());
    }

    private String getStringValueFromBoolean(Boolean value) {
        if (value != null) {
            return value ? "Y" : "N";
        }
        return "";
    }
}
