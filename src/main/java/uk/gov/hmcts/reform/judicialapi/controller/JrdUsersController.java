package uk.gov.hmcts.reform.judicialapi.controller;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserProfileRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserSearchResponse;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;
import uk.gov.hmcts.reform.judicialapi.versions.V1;

import javax.validation.Valid;

import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.FORBIDDEN_ERROR;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.INTERNAL_SERVER_ERROR;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.NO_DATA_FOUND;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.UNAUTHORIZED_ERROR;

@RequestMapping(
    path = "/refdata/judicial/users"
)
@RestController
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("checkstyle:Indentation")
public class JrdUsersController {


    @Autowired
    JudicialUserService judicialUserService;

    @Operation(
            summary = "This API returns judicial user profiles with their appointments and authorisations",
            description = "**IDAM Roles to access API** :\n jrd-system-user,\n jrd-admin",
            security = {
                @SecurityRequirement(name = "Authorization"),
                @SecurityRequirement(name = "ServiceAuthorization")
            }
    )

    @ApiResponse(
                    responseCode = "200",
                    description = "Retrieve the set of judicial user profiles as per given request",
                    content = @Content(schema = @Schema(implementation = OrmResponse.class))
            )
    @ApiResponse(
                    responseCode = "400",
                    description = BAD_REQUEST,
                    content = @Content
            )
    @ApiResponse(
                    responseCode = "401",
                    description = "User Authentication Failed",
                    content = @Content
            )
    @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized",
                    content = @Content
            )
    @ApiResponse(
                    responseCode = "404",
                    description = "No Users Found",
                    content = @Content
            )
    @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content
            )

    @PostMapping(
        path = "/fetch",
        consumes = V1.MediaType.SERVICE,
        produces = V1.MediaType.SERVICE
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

    @Operation(
            summary = "This endpoint will be used for user search based on partial query. When the consumers "
                    + "inputs any 3 characters, they will call this api to fetch "
                    + "the required result.",
            description = "**Valid IDAM role is required to access this endpoint**",
            security = {
                @SecurityRequirement(name = "Authorization"),
                @SecurityRequirement(name = "ServiceAuthorization")
            }
    )

    @ApiResponse(
                    responseCode = "200",
                    description = "Retrieve the user profiles for the given request. ",
                    content = @Content(schema = @Schema(implementation = UserSearchResponse.class))
            )
    @ApiResponse(
                    responseCode = "400",
                    description = BAD_REQUEST,
                    content = @Content
            )
    @ApiResponse(
                    responseCode = "401",
                    description = "User Authentication Failed",
                    content = @Content
            )
    @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized",
                    content = @Content
            )
    @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content
            )

    @PostMapping(
            path = "/search",
            consumes = V1.MediaType.SERVICE,
            produces = V1.MediaType.SERVICE
    )
    public ResponseEntity<Object> searchUsers(@Valid @RequestBody UserSearchRequest userSearchRequest) {

        return judicialUserService.retrieveUserProfile(userSearchRequest);
    }

    @Operation(
            summary = "This API to return judicial user profiles along with their active appointments "
                    + "and authorisations for the given request CCD Service Name or Objectid or SIDAMID",
            description = "**IDAM Roles to access API** :\n jrd-system-user,\n jrd-admin",
            security = {
                @SecurityRequirement(name = "Authorization"),
                @SecurityRequirement(name = "ServiceAuthorization")
            }
    )

    @ApiResponse(
                    responseCode = "200",
                    description = "The User profiles have been retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileRefreshResponse.class))
            )
    @ApiResponse(
                    responseCode = "400",
                    description = BAD_REQUEST,
                    content = @Content
            )
    @ApiResponse(
                    responseCode = "401",
                    description = UNAUTHORIZED_ERROR,
                    content = @Content
            )
    @ApiResponse(
                    responseCode = "403",
                    description = FORBIDDEN_ERROR,
                    content = @Content
            )
    @ApiResponse(
                    responseCode = "404",
                    description = NO_DATA_FOUND,
                    content = @Content
            )
    @ApiResponse(
                    responseCode = "500",
                    description = INTERNAL_SERVER_ERROR,
                    content = @Content
            )

    @PostMapping(
            produces = V1.MediaType.SERVICE
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
