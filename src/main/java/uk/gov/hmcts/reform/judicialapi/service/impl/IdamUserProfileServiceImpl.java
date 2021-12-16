package uk.gov.hmcts.reform.judicialapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.controller.feign.IdamUserFeignClient;
import uk.gov.hmcts.reform.judicialapi.controller.request.RoleDetail;
import uk.gov.hmcts.reform.judicialapi.controller.request.TestUserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.IdamUserProfileResponse;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.repository.IdamUserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.IdamUserProfileService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class IdamUserProfileServiceImpl implements IdamUserProfileService {

    private static final String DEFAULT_USER_PASS = "Hmcts1234";
    private static final String DEFAULT_USER_ROLE = "judiciary";


    private static final String IDAM_USER_CREATED_SUCCESS = "Idam user profile create successfully";
    private static final String IDAM_USER_CREATED_FAIL = "Idam user creation failed";

    @Autowired
    private IdamUserProfileRepository idamUserProfileRepository;

    @Autowired
    private IdamUserFeignClient idamUserFeignClient;


    @Override
    public ResponseEntity<Object> createIdamUserProfiles() {
        var userProfiles = idamUserProfileRepository.findAllJudicialUserProfiles();


        var idamTestUsers = userProfiles
                .stream()
                .map(this::createTestUser)
                .collect(Collectors.toUnmodifiableList());


        var idamUserProfileResponses = new ArrayList<IdamUserProfileResponse>();
        idamTestUsers.forEach(idamUser -> {
            try {
                var idamUserFeignResponse = idamUserFeignClient.createUserProfile(idamUser);

                if (HttpStatus.CREATED.value() == idamUserFeignResponse.status()) {
                    setIdamuserCreationMsg(idamUser, idamUserProfileResponses, IDAM_USER_CREATED_SUCCESS);
                } else {
                    setIdamuserCreationMsg(idamUser, idamUserProfileResponses, IDAM_USER_CREATED_FAIL);
                }
            } catch (Exception exception) {
                setIdamuserCreationMsg(idamUser, idamUserProfileResponses, IDAM_USER_CREATED_FAIL);
            }
        });

        return ResponseEntity
                .status(200)
                .body(idamUserProfileResponses);
    }

    private void setIdamuserCreationMsg(TestUserRequest idamUser,
                                        ArrayList<IdamUserProfileResponse> idamUserProfileResponses,
                                        String idamUserCreatedMsg) {
        var idamUserProfileSuccessResponse = createIdamUserProfileResponse(idamUser);
        idamUserProfileSuccessResponse.setMessage(idamUserCreatedMsg);
        idamUserProfileResponses.add(idamUserProfileSuccessResponse);
    }

    public  TestUserRequest createTestUser(UserProfile userProfile) {
        TestUserRequest accountDetails = new TestUserRequest();

        accountDetails.setEmail(userProfile.getEjudiciaryEmailId());
        accountDetails.setForename(userProfile.getKnownAs());
        accountDetails.setPass(DEFAULT_USER_PASS);
        accountDetails.setSsoId(userProfile.getObjectId());

        accountDetails.setSurname(userProfile.getSurname());


        List<RoleDetail> roleCodes = new ArrayList<>();
        RoleDetail roleCode;
        roleCode = new RoleDetail();
        roleCode.setCode(DEFAULT_USER_ROLE);
        roleCodes.add(roleCode);

        accountDetails.setRoles(roleCodes);

        return accountDetails;
    }

    public  IdamUserProfileResponse createIdamUserProfileResponse(TestUserRequest userProfile) {

        IdamUserProfileResponse idamUserProfileResponse = new IdamUserProfileResponse();

        idamUserProfileResponse.setSurname(userProfile.getSurname());
        idamUserProfileResponse.setKnownAs(userProfile.getForename());
        idamUserProfileResponse.setEmailId(userProfile.getEmail());

        return idamUserProfileResponse;
    }

}
