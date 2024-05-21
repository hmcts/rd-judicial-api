package uk.gov.hmcts.reform.judicialapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(
            description = "This API create idam user profile for all the judicial user profiles.",

            security = {
                @SecurityRequirement(name = "ServiceAuthorization"),
                @SecurityRequirement(name = "Authorization")
            }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Create list of  idam user profiles for judicial user profiles",
            content = @Content(schema = @Schema(implementation = IdamUserProfileResponse.class))
            ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content
            ),
        @ApiResponse(
            responseCode = "401",
            description = "User Authentication Failed",
            content = @Content
            ),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content = @Content
            ),
        @ApiResponse(
            responseCode = "404",
            description = "No Users Found",
            content = @Content
            ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content
            )
    })
    @GetMapping(path = "/testing-support/sidam/actions/create-users",
            produces = "application/json")

    public ResponseEntity<Object> createIdamUserProfiles() {
        return idamUserProfileService.createIdamUserProfiles();
    }
}