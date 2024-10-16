package uk.gov.hmcts.reform.judicialapi.elinks.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
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

    @Operation(description = "", hidden = true)
    @ApiResponse(responseCode = "200", description = "Get list of location and populate region type.")
    @ApiResponse(responseCode = "400", description = BAD_REQUEST)
    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_ERROR)
    @ApiResponse(responseCode = "403", description = FORBIDDEN_ERROR)
    @ApiResponse(responseCode = "404", description = NO_DATA_FOUND)
    @ApiResponse(responseCode = "429", description = TOO_MANY_REQUESTS)
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
    @GetMapping (path = "/reference_data/location",
            produces =V2.MediaType.SERVICE)
    public ResponseEntity<ElinkBaseLocationWrapperResponse> loadLocation(){
        return eLinksService.retrieveLocation();
    }

    @Operation(description = "", hidden = true)
    @ApiResponse(responseCode = "200", description = "Get list of idam users.")
    @ApiResponse(responseCode = "400", description = BAD_REQUEST)
    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_ERROR)
    @ApiResponse(responseCode = "403", description = FORBIDDEN_ERROR)
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
    @GetMapping (path = "/people",
        produces = V2.MediaType.SERVICE)
    @ResponseBody
    public ResponseEntity<ElinkPeopleWrapperResponse> loadPeople() {

        return elinksPeopleService.updatePeople();

    }

    @Operation(description = "", hidden = true)
    @ApiResponse(responseCode = "200", description = "Get list of idam users.")
    @ApiResponse(responseCode = "400", description = BAD_REQUEST)
    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_ERROR)
    @ApiResponse(responseCode = "403", description = FORBIDDEN_ERROR)
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
    @GetMapping (path = "/idam/elastic/search",
        produces = V2.MediaType.SERVICE)
    public ResponseEntity<Object> idamElasticSearch() {

        ResponseEntity<Object> response =  idamElasticSearchService.getIdamElasticSearchSyncFeed();

        return response;
    }

    @Operation(description = "", hidden = true)
    @ApiResponse(responseCode = "200", description = "Get list of idam users.")
    @ApiResponse(responseCode = "400", description = BAD_REQUEST)
    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_ERROR)
    @ApiResponse(responseCode = "403", description = FORBIDDEN_ERROR)
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
    @GetMapping (path = "/idam/find",
        produces = V2.MediaType.SERVICE)
    public ResponseEntity<Object> fetchIdamIds() {

        ResponseEntity<Object> response =  idamElasticSearchService.getIdamDetails();

        return response;
    }

    @Operation(description = "", hidden = true)
    @ApiResponse(responseCode = "200", description = "Get list of leavers.")
    @ApiResponse(responseCode = "400", description = BAD_REQUEST)
    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_ERROR)
    @ApiResponse(responseCode = "403", description = FORBIDDEN_ERROR)
    @ApiResponse(responseCode = "404", description = NO_DATA_FOUND)
    @ApiResponse(responseCode = "429", description = TOO_MANY_REQUESTS)
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
    @GetMapping (path = "/leavers",
        produces = V2.MediaType.SERVICE)
    public ResponseEntity<ElinkLeaversWrapperResponse> loadLeavers(){
        return eLinksService.retrieveLeavers();
    }

    @Operation(description = "", hidden = true)
    @ApiResponse(responseCode = "200", description = "Get Deleted Idam Users ")
    @ApiResponse(responseCode = "400", description = BAD_REQUEST)
    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_ERROR)
    @ApiResponse(responseCode = "403", description = FORBIDDEN_ERROR)
    @ApiResponse(responseCode = "404", description = NO_DATA_FOUND)
    @ApiResponse(responseCode = "429", description = TOO_MANY_REQUESTS)
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
    @GetMapping (path = "/deleted",
        produces = V2.MediaType.SERVICE)
    public ResponseEntity<ElinkDeletedWrapperResponse> loadDeleted(){
        return eLinksService.retrieveDeleted();
    }

    @ApiResponse(responseCode = "200", description = "Publish all SIDAM id's to ASB")
    @ApiResponse(responseCode = "400", description = BAD_REQUEST)
    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_ERROR)
    @ApiResponse(responseCode = "403", description = FORBIDDEN_ERROR)
    @ApiResponse(responseCode = "404", description = NO_DATA_FOUND)
    @ApiResponse(responseCode = "429", description = TOO_MANY_REQUESTS)
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
    @GetMapping(path = "/sidam/asb/publish",
        produces = V2.MediaType.SERVICE)
    public ResponseEntity<SchedulerJobStatusResponse> publishSidamIdToAsb() {
        return publishSidamIdService.publishSidamIdToAsb();
    }
}
