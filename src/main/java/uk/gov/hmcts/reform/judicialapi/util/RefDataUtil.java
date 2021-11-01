package uk.gov.hmcts.reform.judicialapi.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uk.gov.hmcts.reform.judicialapi.controller.request.RoleDetail;
import uk.gov.hmcts.reform.judicialapi.controller.request.TestUserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.IdamUserProfileResponse;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Getter
public class RefDataUtil {
    public static final String DEFAULT_USER_PASSWORD = "Hmcts1234";
    public static final String DEFAULT_USER_ROLE = "judiciary";

    private RefDataUtil() {
    }

    public static Pageable createPageableObject(Integer page, Integer size, Integer defaultPageSize) {

        if (isNull(size)) {
            size = defaultPageSize;
        }
        page = nonNull(page) ? page : 0;
        return PageRequest.of(page, size);
    }

    public static TestUserRequest createTestUser(UserProfile userProfile){
        TestUserRequest accountDetails = new TestUserRequest();

        accountDetails.setEmail(userProfile.getEjudiciaryEmailId());
        accountDetails.setForename(userProfile.getFullName());
        accountDetails.setPassword(DEFAULT_USER_PASSWORD);
        accountDetails.setId(userProfile.getObjectId());

        accountDetails.setSurname(userProfile.getSurname());


        List<RoleDetail> roleCodes = new ArrayList<>();
        RoleDetail roleCode;
        roleCode = new RoleDetail();
        roleCode.setCode(DEFAULT_USER_ROLE);
        roleCodes.add(roleCode);

        accountDetails.setRoles(roleCodes);

        return accountDetails;
    }

    public static IdamUserProfileResponse createIdamUserProfileResponse(TestUserRequest userProfile){

        IdamUserProfileResponse idamUserProfileResponse = new IdamUserProfileResponse();

        idamUserProfileResponse.setSurname(userProfile.getSurname());
        idamUserProfileResponse.setFullName(userProfile.getForename());
        idamUserProfileResponse.setEmailId(userProfile.getEmail());

        return idamUserProfileResponse;
    }

}
