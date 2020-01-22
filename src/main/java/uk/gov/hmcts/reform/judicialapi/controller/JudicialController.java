package uk.gov.hmcts.reform.judicialapi.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import javax.validation.constraints.NotNull;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialRoleTypeEntityResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialUserProfileEntityResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialUserProfileResponse;
import uk.gov.hmcts.reform.judicialapi.service.JudicialRoleTypeService;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserProfileService;

@RequestMapping(path = "refdata/v1/judicial")
@RestController
@Slf4j
@NoArgsConstructor
public class JudicialController {

    @Autowired
    protected JudicialRoleTypeService judicialRoleTypeService;

    @Autowired
    protected JudicialUserProfileService judicialUserProfileService;

    @Value("${exui.role.pui-case-worker:}")
    protected String puiCaseWorker;

    @ApiOperation(
            value = "Retrieves all judicial roles"
    )

    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of judicial role types",
                    response = JudicialRoleTypeEntityResponse.class
            ),
            @ApiResponse(
                    code = 403,
                    message = "Forbidden Error: Access denied"
            ),
            @ApiResponse(
                    code = 500,
                    message = "Server Error",
                    response = String.class
            )
    })


    //@Secured("caseworker")
    @GetMapping(value = "/roles",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<JudicialRoleTypeEntityResponse> getJudicialRoles() {

        JudicialRoleTypeEntityResponse judicialRoleTypeEntityResponse = judicialRoleTypeService.retrieveJudicialRoles();

        return new ResponseEntity<>(judicialRoleTypeEntityResponse, HttpStatus.OK);
    }


    @ApiOperation(
            value = "Retrieve Judicial user profile by email",
            authorizations = {
                    @Authorization(value = "ServiceAuthorization"),
                    @Authorization(value = "Authorization")
            }
    )

    @ApiParam(
            name = "email",
            type = "string",
            value = "The email of the desired user to be retrieved",
            required = false)

    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Retrieved Judicial User profile",
                    response = JudicialUserProfileResponse.class
            ),
            @ApiResponse(
                    code = 403,
                    message = "Forbidden Error: Access denied"
            ),
            @ApiResponse(
                    code = 500,
                    message = "Server Error",
                    response = String.class
            )
    })

    @GetMapping(value = "/user/emailId",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    //@Secured("caseworker")
    protected ResponseEntity<JudicialUserProfileEntityResponse> retrieveUserProfileByEmail(
            @ApiParam(hidden = true) @NotNull @RequestParam("email") String email) {

        return retrieveUserProfileByEmailId(email);

    }

    protected ResponseEntity<JudicialUserProfileEntityResponse> retrieveUserProfileByEmailId(String email) {

        JudicialUserProfileEntityResponse judicialUsersResponse = null;

        judicialUsersResponse = judicialUserProfileService.retrieveUserprofilebyemailId(email);
        return ResponseEntity
                .status(200)
                .body(judicialUsersResponse);
    }

}
