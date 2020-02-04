package uk.gov.hmcts.reform.judicialapi.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialRoleTypeEntityResponse;
import uk.gov.hmcts.reform.judicialapi.service.JudicialRoleTypeService;

@RequestMapping(path = "refdata/v1/judicial")
@RestController
@Slf4j
public class JudicialController {

    @Autowired
    protected JudicialRoleTypeService judicialRoleTypeService;

    @ApiOperation(
            value = "Retrieves all judicial roles",
            authorizations = {
            @Authorization(value = "ServiceAuthorization")
        }
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
            produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<JudicialRoleTypeEntityResponse> getJudicialRoles() {

        JudicialRoleTypeEntityResponse judicialRoleTypeEntityResponse = judicialRoleTypeService.retrieveJudicialRoles();

        return new ResponseEntity<>(judicialRoleTypeEntityResponse, HttpStatus.OK);
    }
}
