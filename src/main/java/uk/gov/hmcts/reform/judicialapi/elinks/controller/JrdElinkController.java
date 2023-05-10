package uk.gov.hmcts.reform.judicialapi.elinks.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinkUserService;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import javax.validation.Valid;


@RequestMapping(
    path = "/refdata/judicial/users"
)
@RestController
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class JrdElinkController {


    @Autowired
    ElinkUserService elinkUserService;

    @ApiOperation(
            value = "This Version 2 endpoint will be used for user search based on partial query. When the consumers "
                    + "inputs any 3 characters, they will call this api to fetch "
                    + "the required result.",
            notes = "**Valid IDAM role is required to access this endpoint**",
            authorizations = {
                    @Authorization(value = "ServiceAuthorization"),
                    @Authorization(value = "Authorization")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Retrieve the user profiles for the given request. ",
                    response = UserSearchResponseWrapper.class
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
                    code = 500,
                    message = "Internal Server Error"
            )
    })
    @PostMapping(
            path = "/search",
            consumes = V2.MediaType.SERVICE,
            produces = V2.MediaType.SERVICE
    )
    public ResponseEntity<Object> retrieveUsers(@Valid @RequestBody UserSearchRequest userSearchRequest) {
        return elinkUserService.retrieveElinkUsers(userSearchRequest);
    }


}
