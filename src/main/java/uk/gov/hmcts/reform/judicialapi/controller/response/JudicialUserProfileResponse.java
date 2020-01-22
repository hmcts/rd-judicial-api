package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;


public class JudicialUserProfileResponse {

    @JsonProperty
    private final String emailId;


    private JudicialUserProfileResponse(JudicialUserProfile userProfile) {

        this.emailId = userProfile.getEmailId();
    }

    public String getJudicialUserProfile(String emailId) {

        return getJudicialUserProfile(emailId);
    }

}