package uk.gov.hmcts.reform.judicialapi.elinks.controller;

import java.util.List;

import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.FORBIDDEN_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.INTERNAL_SERVER_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JOB_DETAILS_UPDATE_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.NO_DATA_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.UNAUTHORIZED_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.SqlContants.GET_DISTINCT_SIDAM_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.SqlContants.UPDATE_JOB_SQL;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ELinksService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.IdamElasticSearchService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

@RestController
@RequestMapping(
        path = "/refdata/internal/topicPublish"
)
@Slf4j
@AllArgsConstructor
@SuppressWarnings("all")
public class TestTopicPublishController {

    @Autowired
    ELinksService eLinksService;

    @Autowired
    ElinksPeopleService elinksPeopleService;

    @Autowired
    IdamElasticSearchService idamElasticSearchService;

    @Autowired
    PublishSidamIdService publishSidamIdService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ElinkTopicPublisher elinkTopicPublisher;


    @ApiResponse(responseCode = "200", description = "Publish all SIDAM id's to ASB")
    @ApiResponse(responseCode = "400", description = BAD_REQUEST)
    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_ERROR)
    @ApiResponse(responseCode = "403", description = FORBIDDEN_ERROR)
    @ApiResponse(responseCode = "404", description = NO_DATA_FOUND)
    @ApiResponse(responseCode = "429", description = TOO_MANY_REQUESTS)
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
    @GetMapping(path = "/publish",
        produces = V2.MediaType.SERVICE)
    @Secured({"jrd-system-user", "jrd-admin"})
    public ResponseEntity<SchedulerJobStatusResponse> publishSidamIdToAsb() {
        try {
        // Get all sidam id's from the judicial_user_profile table
        List<String> sidamIds = jdbcTemplate.query(GET_DISTINCT_SIDAM_ID, RefDataConstants.ROW_MAPPER);

        sidamIds =
            sidamIds.stream()
                .map(String::strip)
                .filter(s -> !s.isBlank())
                .toList();

        Integer sidamIdcount = sidamIds.size();
            String jobId = "1234";
        elinkTopicPublisher.sendMessage(sidamIds, jobId);

        int i = jdbcTemplate.update(UPDATE_JOB_SQL, "SUCCESS", Integer.valueOf(jobId));

        SchedulerJobStatusResponse response = SchedulerJobStatusResponse.builder().id(jobId)
            .jobStatus("SUCCESS").sidamIdsCount(sidamIdcount).statusCode(HttpStatus.OK.value()).build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }catch (Exception ex) {
            throw new ElinksException(HttpStatus.FORBIDDEN, JOB_DETAILS_UPDATE_ERROR, JOB_DETAILS_UPDATE_ERROR);
        }
    }


    @ApiResponse(responseCode = "200", description = "Publish all SIDAM id's to ASB")
    @ApiResponse(responseCode = "400", description = BAD_REQUEST)
    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_ERROR)
    @ApiResponse(responseCode = "403", description = FORBIDDEN_ERROR)
    @ApiResponse(responseCode = "404", description = NO_DATA_FOUND)
    @ApiResponse(responseCode = "429", description = TOO_MANY_REQUESTS)
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
    @PostMapping(path = "/publish",
        produces = V2.MediaType.SERVICE)
    @Secured({"jrd-system-user", "jrd-admin"})
    public ResponseEntity<SchedulerJobStatusResponse> publishSidamIdToAsbIdsFromReqBody(
        @RequestBody RefreshRoleRequest refreshRoleRequest) {
        try {
            log.info("******************Authenticated roles: {}");
            String jobId = "1234";
            elinkTopicPublisher.sendMessage(refreshRoleRequest.getSidamIds(), jobId);

            SchedulerJobStatusResponse response = SchedulerJobStatusResponse.builder().id(jobId)
                .jobStatus("SUCCESS").sidamIdsCount(refreshRoleRequest.getSidamIds().size())
                .statusCode(HttpStatus.OK.value()).build();

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception ex) {
            throw new ElinksException(HttpStatus.FORBIDDEN, JOB_DETAILS_UPDATE_ERROR, JOB_DETAILS_UPDATE_ERROR);
        }
    }
}
