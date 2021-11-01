package uk.gov.hmcts.reform.judicialapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.controller.feign.IdamUserFeignClient;
import uk.gov.hmcts.reform.judicialapi.controller.request.TestUserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.IdamUserProfileResponse;
import uk.gov.hmcts.reform.judicialapi.repository.IdamUserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.IdamUserProfileService;
import uk.gov.hmcts.reform.judicialapi.util.RefDataUtil;

import java.util.ArrayList;
import java.util.stream.Collectors;


@Slf4j
@Service
public class IdamUserProfileServiceImpl implements IdamUserProfileService {

    private static final String IDAM_USER_CREATED_SUCCESS = "Idam user profile create successfully";
    private static final String IDAM_USER_CREATED_FAIL = "Idam user creation failed";

    @Autowired
    private IdamUserProfileRepository idamUserProfileRepository;

    @Autowired
    private IdamUserFeignClient idamUserFeignClient;


    @Override
    public ResponseEntity<Object> createIdamUserProfiles() {
        var userProfiles = idamUserProfileRepository.findAll();


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
