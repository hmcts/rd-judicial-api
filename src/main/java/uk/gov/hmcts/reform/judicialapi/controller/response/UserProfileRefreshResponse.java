package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRefreshResponse implements Serializable {

    @JsonProperty("SidamId")
    private String sidamId;

    @JsonProperty("ObjectId")
    private String objectId;

    @JsonProperty("knownAs")
    private String knownAs;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("postNominals")
    private String postNominals;

    @JsonProperty("emailId")
    private String emailId;

    @JsonProperty("appointments")
    private List<AppointmentRefreshResponse> appointments;

    @JsonProperty("authorisations")
    private List<AuthorisationRefreshResponse> authorisations;

}
