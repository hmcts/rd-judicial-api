package uk.gov.hmcts.reform.judicialapi.elinks.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserProfileRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinkUserService;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import javax.validation.Valid;

import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.BAD_REQUEST;


@RequestMapping(
    path = "/refdata/judicial/users"
)
@RestController
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("checkstyle:Indentation")
public class JrdElinkController {

    @Autowired
    ElinkUserService elinkUserService;

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

    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Retrieve the user profiles for the given request. ",
            content = @Content(schema = @Schema(implementation = UserSearchResponseWrapper.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = BAD_REQUEST,
            content = @Content
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "User Authentication Failed",
            content = @Content
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content = @Content
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content
    )

    @PostMapping(
            path = "/search",
            produces = V2.MediaType.SERVICE
    )
   public ResponseEntity<Object> retrieveUsers(@Valid @RequestBody UserSearchRequest userSearchRequest) {
        return elinkUserService.retrieveElinkUsers(userSearchRequest);
    }

    @Operation(
            summary = "This endpoint will be used for user search based on partial query. When the consumers "
                    + "inputs any 3 characters, they will call this api to fetch "
                    + "the required result.",
            description = "**IDAM Roles to access API** :\n jrd-system-user,\n jrd-admin",
            security = {
                    @SecurityRequirement(name = "Authorization"),
                    @SecurityRequirement(name = "ServiceAuthorization")
            }
    )

    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Retrieve the user profiles for the given request. ",
            content = @Content(schema = @Schema(implementation = UserProfileRefreshResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = BAD_REQUEST,
            content = @Content
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "User Authentication Failed",
            content = @Content
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content = @Content
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content
    )
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
            @RequestHeader(name = "sort_column", required = false)
            @Parameter(name = "sort_column", description = "Example Notations for the sort columns are personalCode,"
                    + "sidamId, emailId, fullName, surName, objectId etc.,")
            String sortColumn) {
        log.info("starting refreshUserProfile with RefreshRoleRequest {}, pageSize = {}, pageNumber = {}, "
                        + "sortDirection = {}, sortColumn = {}", refreshRoleRequest,
                pageSize, pageNumber,sortDirection,sortColumn);

        return elinkUserService.refreshUserProfile(refreshRoleRequest, pageSize, pageNumber,
                sortDirection, sortColumn);
    }
}
