package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;

@Getter
@Setter
@NoArgsConstructor
public class AuthorisationResponse {

    @JsonProperty
    private String authorisationId;
    @JsonProperty
    private String jurisdiction;

    public AuthorisationResponse(Authorisation authorisation) {
        this.authorisationId = authorisation.getOfficeAuthId().toString();
        this.jurisdiction = authorisation.getJurisdiction() == null ? "" : authorisation.getJurisdiction();
    }

}
