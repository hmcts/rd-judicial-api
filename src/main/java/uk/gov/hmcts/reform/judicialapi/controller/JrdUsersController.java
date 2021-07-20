package uk.gov.hmcts.reform.judicialapi.controller;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfileWithServiceName;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;
import uk.gov.hmcts.reform.judicialapi.util.RequestUtils;

import javax.validation.constraints.NotEmpty;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.UNAUTHORIZED_ERROR;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.FORBIDDEN_ERROR;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.NO_DATA_FOUND;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.INTERNAL_SERVER_ERROR;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.REQUIRED_PARAMETER_SERVICE_NAMES_IS_EMPTY;

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

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @Value("${refresh.pageSize}")
    private int configPageSize;

    @Value("${refresh.sortColumn}")
    private String configSortColumn;

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
            value = "This API returns the User profiles based on Service Name and Pagination parameters",
            notes = "**IDAM Role to access API** :\n cwd-system-user",
            authorizations = {
                    @Authorization(value = "ServiceAuthorization"),
                    @Authorization(value = "Authorization")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "The User profiles have been retrieved successfully",
                    response = UserProfileWithServiceName.class
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
    @GetMapping(
            path = "/",
            produces = APPLICATION_JSON_VALUE
    )
    @Secured({"jrd-system-user", "jrd-admin"})
    public ResponseEntity<Object> fetchUserProfileByServiceNames(
            @RequestParam(name = "serviceName") @NotEmpty String serviceName,
            @RequestParam(name = "page_size", required = false) Integer pageSize,
            @RequestParam(name = "page_number", required = false) Integer pageNumber,
            @RequestParam(name = "sort_direction", required = false) String sortDirection,
            @RequestParam(name = "sort_column", required = false) String sortColumn
    ) {
        log.info("{}:: Fetching the User Profile to refresh role assignment for service names {}",
                loggingComponentName, serviceName);
        if (StringUtils.isBlank(serviceName)) {
            throw new InvalidRequestException(REQUIRED_PARAMETER_SERVICE_NAMES_IS_EMPTY);
        }

        PageRequest pageRequest = RequestUtils.validateAndBuildPaginationObject(pageNumber, pageSize,
                sortColumn, sortDirection, configPageSize, configSortColumn,
                UserProfile.class);

        return judicialUserService.fetchUserProfileByServiceNames(serviceName, pageRequest);
    }

}
