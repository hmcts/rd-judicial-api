package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.StringUtils.EMPTY;

@Getter
@Setter
@NoArgsConstructor
public class AuthorisationResponse {

    @JsonProperty
    private String authorisationId;
    @JsonProperty
    private String jurisdiction;

    public AuthorisationResponse(Authorisation authorisation) {

        if (nonNull(authorisation)) {
            this.authorisationId = String.valueOf(authorisation.getOfficeAuthId());
            this.jurisdiction = isNull(authorisation.getJurisdiction()) ? EMPTY : authorisation.getJurisdiction();
        }
    }

}
