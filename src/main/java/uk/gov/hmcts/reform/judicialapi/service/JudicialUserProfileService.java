package uk.gov.hmcts.reform.judicialapi.service;

import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialUserProfileEntityResponse;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;

public interface JudicialUserProfileService {

    JudicialUserProfile findJudicialUserprofilebyemailaddress(String email);

    JudicialUserProfileEntityResponse retrieveUserprofilebyemailId(String emailId);
}
