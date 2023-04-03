package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    public UserSearchResponse(UserSearchResponse userProfile) {
        this.title = userProfile.getTitle();
        this.knownAs = userProfile.getKnownAs();
        this.surname = userProfile.getSurname();
        this.fullName = userProfile.getFullName();
        this.emailId = userProfile.getEmailId();
        this.personalCode = userProfile.getPersonalCode();
        this.idamId = userProfile.getIdamId();
    }

    private String getStringValueFromBoolean(Boolean value) {
        if (value != null) {
            return value ? "Y" : "N";
        }
        return "";
    }
}
