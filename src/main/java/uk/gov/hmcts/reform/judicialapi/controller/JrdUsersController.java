package uk.gov.hmcts.reform.judicialapi.controller;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserProfileRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserSearchResponse;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.UNAUTHORIZED_ERROR;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.FORBIDDEN_ERROR;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.NO_DATA_FOUND;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.INTERNAL_SERVER_ERROR;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(
    path = "/refdata/judicial/users"
)
@RestController
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class JrdUsersController {


    @Autowired
    JudicialUserService judicialUserService;

    @ApiOperation(
            value = "This API returns judicial user profiles with their appointments and authorisations",
            notes = "**IDAM Roles to access API** :\n jrd-system-user,\n jrd-admin",
            authorizations = {
                 @Authorization(value = "ServiceAuthorization"),
                 @Authorization(value = "Authorization")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Retrieve the set of judicial user profiles as per given request",
                    response = OrmResponse.class
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
    @PostMapping(
        path = "/fetch",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    @Secured({"jrd-system-user", "jrd-admin"})
    public ResponseEntity<Object> fetchUsers(@RequestParam(value = "page_size", required = false) Integer size,
                                             @RequestParam(value = "page_number", required = false) Integer page,
                                             @RequestBody UserRequest userRequest) {

        if (CollectionUtils.isEmpty(userRequest.getUserIds())) {
            throw new InvalidRequestException("The list of user ids is empty");
        }

        return judicialUserService.fetchJudicialUsers(size, page, userRequest.getUserIds());
    }

    @ApiOperation(
            value = "This endpoint will be used for user search based on partial query. When the consumers "
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
                    response = UserSearchResponse.class
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
    @PostMapping(
            path = "/search",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> searchUsers(@Valid @RequestBody UserSearchRequest userSearchRequest) {

        return judicialUserService.retrieveUserProfile(userSearchRequest);
    }

    @ApiOperation(
            value = "This API to return judicial user profiles along with their active appointments "
                    + "and authorisations for the given request CCD Service Name or Objectid or SIDAMID",
            notes = "**IDAM Roles to access API** :\n jrd-system-user,\n jrd-admin",
            authorizations = {
                    @Authorization(value = "ServiceAuthorization"),
                    @Authorization(value = "Authorization")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "The User profiles have been retrieved successfully",
                    response = UserProfileRefreshResponse.class
            ),
            @ApiResponse(
                    code = 400,
                    message = BAD_REQUEST
            ),
            @ApiResponse(
                    code = 401,
                    message = UNAUTHORIZED_ERROR
            ),
            @ApiResponse(
                    code = 403,
                    message = FORBIDDEN_ERROR
            ),
            @ApiResponse(
                    code = 404,
                    message = NO_DATA_FOUND
            ),
            @ApiResponse(
                    code = 500,
                    message = INTERNAL_SERVER_ERROR
            )
    })
    @PostMapping(
            path = "",
            produces = APPLICATION_JSON_VALUE
    )
    @Secured({"jrd-system-user", "jrd-admin"})
    public ResponseEntity<Object> refreshUserProfile(
            @RequestBody RefreshRoleRequest refreshRoleRequest,
            @RequestHeader(name = "page_size", required = false) Integer pageSize,
            @RequestHeader(name = "page_number", required = false) Integer pageNumber,
            @RequestHeader(name = "sort_direction", required = false) String sortDirection,
            @RequestHeader(name = "sort_column", required = false) String sortColumn
    ) {
        log.info("starting refreshUserProfile with RefreshRoleRequest {}, pageSize = {}, pageNumber = {}, "
                + "sortDirection = {}, sortColumn = {}", refreshRoleRequest,
                pageSize, pageNumber,sortDirection,sortColumn);

        return judicialUserService.refreshUserProfile(refreshRoleRequest, pageSize, pageNumber,
                sortDirection, sortColumn);
    }


}
