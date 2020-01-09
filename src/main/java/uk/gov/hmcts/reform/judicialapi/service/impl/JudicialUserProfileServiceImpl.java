package uk.gov.hmcts.reform.judicialapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;
import uk.gov.hmcts.reform.judicialapi.persistence.JudicialUserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.util.JrdUtil;

public class JudicialUserProfileServiceImpl {

    @Autowired
    JudicialUserProfileRepository judicialUserProfileRepository;

    public JudicialUserProfile findJudicialUserProfileByEmailAddress(String email) {
        JudicialUserProfile user = judicialUserProfileRepository.findUserProfileByEmailAddress(JrdUtil.removeAllSpaces(email));

        if (user == null) {
            throw new EmptyResultDataAccessException(1);
        }

        return user;
    }

}
