package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdamUserProfileResponse implements Serializable {
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
    private String message;

    public IdamUserProfileResponse(UserProfile userProfile) {
        this.title = userProfile.getPostNominals();
        this.knownAs = userProfile.getKnownAs();
        this.surname = userProfile.getSurname();
        this.fullName = userProfile.getFullName();
        this.emailId = userProfile.getEjudiciaryEmailId();
        this.idamId = userProfile.getSidamId();
    }
}
