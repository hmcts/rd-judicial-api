package uk.gov.hmcts.reform.judicialapi.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.controller.response.IdamUserProfileResponse;
import uk.gov.hmcts.reform.judicialapi.service.IdamUserProfileService;


@RestController
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@RequestMapping(path = "/refdata/judicial/users")
@ConditionalOnExpression("${testing.support.enabled:false}")
public class TestingSupportController {

    @Autowired
    IdamUserProfileService idamUserProfileService;

    @ApiOperation(
            value = "This API create idam user profile for all the judicial user profiles.",

            authorizations = {
                    @Authorization(value = "ServiceAuthorization"),
                    @Authorization(value = "Authorization")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Create list of  idam user profiles for judicial user profiles",
                    response = IdamUserProfileResponse.class
            ),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request"
            ),
            @ApiResponse(
                    code = 401,
                    message = "User Authentication Failed"
            ),
            @ApiResponse(
                    code = 403,
                    message = "Unauthorized"
            ),
            @ApiResponse(
                    code = 404,
                    message = "No Users Found"
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error"
            )
    })
    @GetMapping(path = "/testing-support/sidam/actions/create-users",
            produces = "application/json")

    public ResponseEntity<Object> createIdamUserProfiles() {
        return idamUserProfileService.createIdamUserProfiles();
    }
}