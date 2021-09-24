package uk.gov.hmcts.reform.judicialapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserSearchResponse;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.reform.judicialapi.util.JrdConstant.USER_DATA_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;

@Slf4j
@Service
public class JudicialUserServiceImpl implements JudicialUserService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Value("${defaultPageSize}")
    Integer defaultPageSize;

    @Override
    public ResponseEntity<Object> fetchJudicialUsers(Integer size, Integer page, List<String> sidamIds) {
        Pageable pageable = createPageableObject(page, size, defaultPageSize);
        Page<UserProfile> pagedUserProfiles = userProfileRepository.findBySidamIdIn(sidamIds, pageable);

        List<UserProfile> userProfiles = pagedUserProfiles.getContent();

        if (CollectionUtils.isEmpty(userProfiles)) {
            throw new ResourceNotFoundException("Data not found");
        }

        List<OrmResponse> ormResponses = userProfiles.stream()
                .map(OrmResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(200)
                .body(ormResponses);
    }

    @Override
    public ResponseEntity<Object> retrieveUserProfile(UserSearchRequest userSearchRequest) {
        var userProfiles = userProfileRepository
                .findBySearchString(userSearchRequest.getSearchString()
                .toLowerCase(), userSearchRequest.getServiceCode(), userSearchRequest.getLocation());

        if (CollectionUtils.isEmpty(userProfiles)) {
            throw new ResourceNotFoundException(USER_DATA_NOT_FOUND);
        }

        var userSearchResponses = userProfiles
                .stream()
                .map(UserSearchResponse::new)
                .collect(Collectors.toUnmodifiableList());

        return ResponseEntity
                .status(200)
                .body(userSearchResponses);

    }

}
