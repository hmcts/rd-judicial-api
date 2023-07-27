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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserProfileRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinkUserService;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import javax.validation.Valid;

import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.FORBIDDEN_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.INTERNAL_SERVER_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.NO_DATA_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.UNAUTHORIZED_ERROR;


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

  /*  @ApiOperation(
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
  /*  @PostMapping(
            path = "/search",
            consumes = V2.MediaType.SERVICE,
            produces = V2.MediaType.SERVICE
    )
  //  public ResponseEntity<Object> retrieveUsers(@Valid @RequestBody UserSearchRequest userSearchRequest) {
      //  return elinkUserService.retrieveElinkUsers(userSearchRequest);
  //  }*/

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
            produces = V2.MediaType.SERVICE
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

        return elinkUserService.refreshUserProfile(refreshRoleRequest, pageSize, pageNumber,
                sortDirection, sortColumn);
    }
}
