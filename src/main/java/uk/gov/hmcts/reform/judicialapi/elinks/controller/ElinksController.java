package uk.gov.hmcts.reform.judicialapi.elinks.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkDeletedWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ELinksService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.IdamElasticSearchService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.FORBIDDEN_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.INTERNAL_SERVER_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.NO_DATA_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.UNAUTHORIZED_ERROR;

@RestController
@RequestMapping(
        path = "/refdata/internal/elink"
)
@Slf4j
@AllArgsConstructor
@Hidden
@SuppressWarnings("all")
public class ElinksController {


    @Autowired
    ELinksService eLinksService;

    @Autowired
    ElinksPeopleService elinksPeopleService;

    @Autowired
    IdamElasticSearchService idamElasticSearchService;

    @Autowired
    PublishSidamIdService publishSidamIdService;

    @ApiOperation(
        value = "", hidden = true)
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Get list of location and populate region type.",
                    response = ElinkLocationWrapperResponse.class
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
                    code = 429,
                    message = TOO_MANY_REQUESTS
            ),
            @ApiResponse(
                    code = 500,
                    message = INTERNAL_SERVER_ERROR
            )
    })
    @GetMapping (path = "/reference_data/location",
            produces =V2.MediaType.SERVICE)
    public ResponseEntity<ElinkBaseLocationWrapperResponse> loadLocation(){


        return eLinksService.retrieveLocation();
    }

    @ApiOperation(
        value = "", hidden = true)
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Get list of idam users.",
                    response = Object.class
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
                    code = 500,
                    message = INTERNAL_SERVER_ERROR
            )
    })
    @GetMapping (path = "/people",
        produces = V2.MediaType.SERVICE)
    @ResponseBody
    public ResponseEntity<ElinkPeopleWrapperResponse> loadPeople() {

        return elinksPeopleService.updatePeople();

    }

    @ApiOperation(
        value = "", hidden = true)
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Get list of idam users.",
                    response = IdamResponse.class
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
                    code = 500,
                    message = INTERNAL_SERVER_ERROR
            )
    })
    @GetMapping (path = "/idam/elastic/search",
        produces = V2.MediaType.SERVICE)
    public ResponseEntity<Object> idamElasticSearch() {

        ResponseEntity<Object> response =  idamElasticSearchService.getIdamElasticSearchSyncFeed();

        return response;
    }

    @ApiOperation(
        value = "", hidden = true)
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Get list of leavers.",
                    response = ElinkLeaversWrapperResponse.class
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
                    code = 429,
                    message = TOO_MANY_REQUESTS
            ),
            @ApiResponse(
                    code = 500,
                    message = INTERNAL_SERVER_ERROR
            )
    })
    @GetMapping (path = "/leavers",
        produces = V2.MediaType.SERVICE)
    public ResponseEntity<ElinkLeaversWrapperResponse> loadLeavers(){
        return eLinksService.retrieveLeavers();
    }

    @ApiOperation(
        value = "", hidden = true)
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "Get Deleted Idam Users ",
            response = ElinkLeaversWrapperResponse.class
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
            code = 429,
            message = TOO_MANY_REQUESTS
        ),
        @ApiResponse(
            code = 500,
            message = INTERNAL_SERVER_ERROR
        )
    })
    @GetMapping (path = "/deleted",
        produces = V2.MediaType.SERVICE)
    public ResponseEntity<ElinkDeletedWrapperResponse> loadDeleted(){
        return eLinksService.retrieveDeleted();
    }

    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "Publish all SIDAM id's to ASB",
            response = String.class
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
            code = 429,
            message = TOO_MANY_REQUESTS
        ),
        @ApiResponse(
            code = 500,
            message = INTERNAL_SERVER_ERROR
        )
    })
    @GetMapping(path = "/sidam/asb/publish",
        produces = V2.MediaType.SERVICE)
    public ResponseEntity<SchedulerJobStatusResponse> publishSidamIdToAsb() {
        return publishSidamIdService.publishSidamIdToAsb();
    }

}
