package uk.gov.hmcts.reform.judicialapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.judicialapi.controller.feign.IdamUserFeignClient;
import uk.gov.hmcts.reform.judicialapi.controller.request.TestUserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.IdamUserProfileResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserSearchResponse;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.repository.ServiceCodeMappingRepository;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;
import uk.gov.hmcts.reform.judicialapi.util.RefDataUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.reform.judicialapi.util.JrdConstant.USER_DATA_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.util.JrdConstant.IDAM_USER_CREATED_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.util.JrdConstant.IDAM_USER_CREATED_FAIL;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;

@Slf4j
@Service
public class JudicialUserServiceImpl implements JudicialUserService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ServiceCodeMappingRepository serviceCodeMappingRepository;

    @Autowired
    private IdamUserFeignClient idamUserFeignClient;

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
        var ticketCode = new ArrayList<String>();

        if (userSearchRequest.getServiceCode() != null) {
            var serviceCodeMappings = serviceCodeMappingRepository
                    .findByServiceCodeIgnoreCase(userSearchRequest.getServiceCode());

            serviceCodeMappings
                    .forEach(s -> ticketCode.add(s.getTicketCode()));
        }

        var userProfiles = userProfileRepository
                .findBySearchString(userSearchRequest.getSearchString().toLowerCase(),
                        userSearchRequest.getServiceCode(), userSearchRequest.getLocation(), ticketCode);

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

    @Override
    public ResponseEntity<Object> createIdamUserProfiles() {
        var userProfiles = userProfileRepository.findAll();


        var idamTestUsers = userProfiles
                .stream()
                .map(RefDataUtil::createTestUser)
                .collect(Collectors.toUnmodifiableList());

        var idamUserProfileResponses = new ArrayList<IdamUserProfileResponse>();
        //TODO to test for singleUserCreation

        ArrayList<TestUserRequest> idamSingleUsers = new ArrayList<>();
        idamSingleUsers.add(idamTestUsers.get(0));

        idamSingleUsers.forEach(idamUser->{
            try{
                var idamUserFeignResponse = idamUserFeignClient.createUserProfile(idamUser);

                if(HttpStatus.CREATED.equals(idamUserFeignResponse.status())){
                    var idamUserProfileSuccessResponse = RefDataUtil.createIdamUserProfileResponse(idamUser);
                    idamUserProfileSuccessResponse.setMessage(IDAM_USER_CREATED_SUCCESS);
                    idamUserProfileResponses.add(idamUserProfileSuccessResponse);
                }
            }catch (Exception exception){
                var idamUserProfileFailureResponse = RefDataUtil.createIdamUserProfileResponse(idamUser);
                idamUserProfileFailureResponse.setMessage(IDAM_USER_CREATED_FAIL);
                idamUserProfileResponses.add(idamUserProfileFailureResponse);
            }
        });

        return ResponseEntity
                .status(200)
                .body(idamUserProfileResponses);
    }

}
