package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uk.gov.hmcts.reform.judicialapi.domain.AuthorisationType;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialOfficeAuthorisation;

@Getter
public class JudicialOfficeAuthorisationsResponse {

    @JsonProperty
     private String authorisationId;

    @JsonProperty
    private String authorisationDescEn;

    @JsonProperty
    private String jurisdictionId;

    @JsonProperty
    private String jurisdictionDescEn;

    @JsonProperty
    private String authorisationDate;


    public JudicialOfficeAuthorisationsResponse (JudicialOfficeAuthorisation judicialOfficeAuthorisation, AuthorisationType authorisationType) {
        this.authorisationId = judicialOfficeAuthorisation.toString();
        this.authorisationDescEn = authorisationType.getJurisdictionDescEn();
        this.jurisdictionId = authorisationType.getJurisdictionId();
        this.jurisdictionDescEn = authorisationType.getAuthorisationDescEn();
        this.authorisationDate = judicialOfficeAuthorisation.toString();

    }

}
