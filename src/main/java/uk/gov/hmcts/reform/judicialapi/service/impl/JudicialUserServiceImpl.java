package uk.gov.hmcts.reform.judicialapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudicialUserServiceImpl implements JudicialUserService {

    @Autowired
    private UserProfileRepository userProfileRepository;


    @Override
    public List<OrmResponse> fetchJudicialUsers(Integer size, Integer page) {

        List<OrmResponse> ormResponses;
        List<UserProfile> userProfiles = userProfileRepository.findAll();


        ormResponses = userProfiles.stream()
                .map(OrmResponse::new)
                .collect(Collectors.toList());

        return ormResponses;
    }
}
