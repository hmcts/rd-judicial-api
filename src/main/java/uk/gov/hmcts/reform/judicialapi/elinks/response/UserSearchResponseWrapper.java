package uk.gov.hmcts.reform.judicialapi.elinks.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;

import java.io.Serializable;


@JsonRootName("UserSearchResponse")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserSearchResponseWrapper implements Serializable {
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
    private String initials;
    @JsonProperty
    private String postNominals;

    @JsonProperty
    private String personalCode;


    public UserSearchResponseWrapper(UserProfile userProfile) {
        this.title = userProfile.getTitle();
        this.knownAs = userProfile.getKnownAs();
        this.surname = userProfile.getSurname();
        this.fullName = userProfile.getFullName();
        this.emailId = userProfile.getEjudiciaryEmailId();
        this.personalCode = userProfile.getPersonalCode();
        this.idamId = userProfile.getSidamId();
        this.postNominals = userProfile.getPostNominals();
        this.initials = userProfile.getInitials();
    }


    private String getStringValueFromBoolean(Boolean value) {
        if (value != null) {
            return value ? "Y" : "N";
        }
        return "";
    }
}
