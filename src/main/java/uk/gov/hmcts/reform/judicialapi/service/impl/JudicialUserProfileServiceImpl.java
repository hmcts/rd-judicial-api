package uk.gov.hmcts.reform.judicialapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialUserProfileEntityResponse;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;
import uk.gov.hmcts.reform.judicialapi.persistence.JudicialUserProfileRepository;


public class JudicialUserProfileServiceImpl {

    @Autowired
    JudicialUserProfileRepository judicialUserProfileRepository;

    public JudicialUserProfileEntityResponse retrieveUserProfile(String email) {
        JudicialUserProfile userProfile = judicialUserProfileRepository.findUserProfileByEmailAddress(email);
        if (userProfile == null) {
            throw new EmptyResultDataAccessException(1);

        }
        return new JudicialUserProfileEntityResponse(userProfile);
    }
}
