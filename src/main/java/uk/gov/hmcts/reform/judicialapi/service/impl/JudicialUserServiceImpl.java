package uk.gov.hmcts.reform.judicialapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private int defaultPageSize;


    @Override
    public List<OrmResponse> fetchJudicialUsers(Integer size, Integer page) {

        Pageable pageable = createPageableObject(page, size);
        Page<UserProfile> pagedUserProfiles = userProfileRepository.findAll(pageable);

        List<UserProfile> userProfiles = pagedUserProfiles.getContent();

        List<OrmResponse> ormResponses = userProfiles.stream()
                .map(OrmResponse::new)
                .collect(Collectors.toList());

        return ormResponses;
    }

    public Pageable createPageableObject(Integer page, Integer size) {
        if (size == null) {
            size = defaultPageSize;
        }
        return PageRequest.of(page, size);
    }


    @Value("${defaultPageSize}")
    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }
}
