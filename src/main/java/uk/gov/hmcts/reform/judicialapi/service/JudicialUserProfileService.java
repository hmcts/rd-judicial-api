package uk.gov.hmcts.reform.judicialapi.service;

import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;

public interface JudicialUserProfileService {

    JudicialUserProfile findJudicialUserProfileByEmailAddress(String email);
}
