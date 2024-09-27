package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.LeaversRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.response.DeletedResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.response.ElinksDeleteApiResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinksResponsesRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.BaseLocationResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkDeletedWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ELinksService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleDeleteService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleLeaverService;
import uk.gov.hmcts.reform.judicialapi.elinks.util.CommonUtil;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataExceptionHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksResponsesHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.AUDIT_DATA_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DATA_UPDATE_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDSUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKSRESPONSES;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_DATA_STORE_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSSUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.THREAD_INVOCATION_EXCEPTION;

@Service
@Slf4j
public class ELinksServiceImpl implements ELinksService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ElinksResponsesRepository elinksResponsesRepository;

    @Autowired
    BaseLocationRepository baseLocationRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    private ElinksPeopleLeaverService elinksPeopleLeaverService;

    @Autowired
    ElinksPeopleDeleteService elinksPeopleDeleteService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private DataloadSchedularAuditRepository dataloadSchedularAuditRepository;

    @Value("${elinks.people.perPage}")
    private String perPage;

    @Value("${elinks.people.page}")
    private String page;

    @Value("${elinks.cleanElinksResponsesDays}")
    private Long cleanElinksResponsesDays;

    @Value("${elinks.delJohProfilesYears:7}")
    private Long delJohProfilesYears;

    @Value("${elinks.delJohProfiles:false}")
    private boolean delJohProfiles;

    @Value("${elinks.people.updatedSinceEnabled:false}")
    private boolean isCustomizeUpdatedSince;

    @Autowired
    ElinksFeignClient elinksFeignClient;

    @Value("${elinks.people.threadPauseTime}")
    private String threadPauseTime;

    @Autowired
    CommonUtil commonUtil;

    @Value("${elinks.people.lastUpdated}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String lastUpdated;

    @Autowired
    ElinkDataIngestionSchedularAudit elinkDataIngestionSchedularAudit;

    @Autowired
    ElinksResponsesHelper elinksResponsesHelper;

    @Autowired
    ElinkDataExceptionHelper elinkDataExceptionHelper;

    @Autowired
    AppointmentsRepository appointmentsRepository;

    @Autowired
    AuthorisationsRepository authorisationsRepository;

    @Autowired
    JudicialRoleTypeRepository judicialRoleTypeRepository;

    @Override
    public ResponseEntity<ElinkBaseLocationWrapperResponse> retrieveLocation() {

        log.info("Get location details ELinksService.retrieveLocation ");

        LocalDateTime schedulerStartTime = now();

        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                null,
                RefDataElinksConstants.JobStatus.IN_PROGRESS.getStatus(), LOCATIONAPI);

        Response locationsResponse;
        HttpStatus httpStatus;
        ResponseEntity<ElinkBaseLocationWrapperResponse> result = null;
        try {
            log.info("Calling Elinks location service");
            locationsResponse = elinksFeignClient.getLocationDetails();
            locationsResponse = elinksResponsesHelper.saveElinksResponse(LOCATION, locationsResponse);

            httpStatus = HttpStatus.valueOf(locationsResponse.status());
            log.info("Get location details response status ELinksService.retrieveLocation" + httpStatus.value());
            if (httpStatus.is2xxSuccessful()) {
                ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toResponseEntity(locationsResponse,
                        ElinkBaseLocationResponse.class);
                ElinkBaseLocationResponse elinkBaseLocationResponse =
                        (ElinkBaseLocationResponse) responseEntity.getBody();
                if (nonNull(responseEntity.getBody())) {
                    if (nonNull(elinkBaseLocationResponse) && elinkBaseLocationResponse.getResults().size() > 0) {
                        List<BaseLocationResponse> baseLocationResponses = elinkBaseLocationResponse.getResults();

                        List<BaseLocation> baselocations = baseLocationResponses.stream()
                                .map(BaseLocationResponse::toBaseLocationEntity)
                                .toList();
                        result = loadBaseLocationData(baselocations);
                    } else {
                        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                                schedulerStartTime,
                                now(),
                                RefDataElinksConstants.JobStatus.FAILED.getStatus(), LOCATIONAPI);
                        throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
                    }
                } else {
                    elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                            schedulerStartTime,
                            now(),
                            RefDataElinksConstants.JobStatus.FAILED.getStatus(), LOCATIONAPI);
                    throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
                }
            } else {

                elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                        schedulerStartTime,
                        now(),
                        RefDataElinksConstants.JobStatus.FAILED.getStatus(), LOCATIONAPI);
                handleELinksErrorResponse(httpStatus);
            }
        } catch (FeignException ex) {
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        } catch (JSONException ex) {
            log.error("json exception elinks location response",ex);
            elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime,
                    now(),
                    RefDataElinksConstants.JobStatus.FAILED.getStatus(), LOCATIONAPI, ex.getMessage());
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        } catch (Exception ex) {
            log.error("Exception on elinks location",ex);
            elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime,
                    now(),
                    RefDataElinksConstants.JobStatus.FAILED.getStatus(), LOCATIONAPI, ex.getMessage());
            throw ex;
        }
        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                now(),
                RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), LOCATIONAPI);

        return result;
    }

    private void handleELinksErrorResponse(HttpStatus httpStatus) {

        if (HttpStatus.BAD_REQUEST.value() == httpStatus.value()) {

            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_BAD_REQUEST,
                    ELINKS_ERROR_RESPONSE_BAD_REQUEST);
        } else if (HttpStatus.UNAUTHORIZED.value() == httpStatus.value()) {

            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_UNAUTHORIZED,
                    ELINKS_ERROR_RESPONSE_UNAUTHORIZED);
        } else if (HttpStatus.FORBIDDEN.value() == httpStatus.value()) {

            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_FORBIDDEN,
                    ELINKS_ERROR_RESPONSE_FORBIDDEN);
        } else if (HttpStatus.NOT_FOUND.value() == httpStatus.value()) {

            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_NOT_FOUND,
                    ELINKS_ERROR_RESPONSE_NOT_FOUND);
        } else if (HttpStatus.TOO_MANY_REQUESTS.value() == httpStatus.value()) {

            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS,
                    ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS);
        } else {
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }
    }

    private ResponseEntity<ElinkBaseLocationWrapperResponse> loadBaseLocationData(List<BaseLocation> baselocations) {
        ResponseEntity<ElinkBaseLocationWrapperResponse> result;
        try {

            baseLocationRepository.saveAll(baselocations);

            ElinkBaseLocationWrapperResponse elinkLocationWrapperResponse = new ElinkBaseLocationWrapperResponse();
            elinkLocationWrapperResponse.setMessage(BASE_LOCATION_DATA_LOAD_SUCCESS);


            result = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(elinkLocationWrapperResponse);
        } catch (DataAccessException dae) {

            throw new ElinksException(HttpStatus.INTERNAL_SERVER_ERROR, ELINKS_DATA_STORE_ERROR,
                    ELINKS_DATA_STORE_ERROR);
        }

        return result;
    }


    private Response getLeaversResponseFromElinks(int currentPage) {
        String leftSince = getUpdateSince();
        try {
            return elinksFeignClient.getLeaversDetails(leftSince, perPage, String.valueOf(currentPage));
        } catch (FeignException ex) {
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }
    }

    private String getUpdateSince() {
        String updatedSince;
        LocalDateTime maxSchedulerEndTime;

        if (isCustomizeUpdatedSince) {
            updatedSince = commonUtil.getUpdatedDateFormat(lastUpdated);
        } else {
            try {
                maxSchedulerEndTime = dataloadSchedularAuditRepository.findLatestSchedularEndTimeForLeavers();
            } catch (Exception ex) {
                throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, AUDIT_DATA_ERROR, AUDIT_DATA_ERROR);
            }
            if (Optional.ofNullable(maxSchedulerEndTime).isEmpty()) {
                updatedSince = commonUtil.getUpdatedDateFormat(lastUpdated);
            } else {
                updatedSince = maxSchedulerEndTime.toString();
                updatedSince = updatedSince.substring(0, updatedSince.indexOf('T'));
            }
        }

        log.info("Leavers Service updatedSince : " + updatedSince);
        return updatedSince;
    }

    @Override
    @Transactional("transactionManager")
    public ResponseEntity<ElinkLeaversWrapperResponse> retrieveLeavers() {
        boolean isMorePagesAvailable = true;
        HttpStatus httpStatus = null;
        LocalDateTime schedulerStartTime = now();
        log.info("Calling Elinks Leavers service");
        ElinkLeaversWrapperResponse elinkLeaversWrapperResponse = new ElinkLeaversWrapperResponse();
        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                null,
                RefDataElinksConstants.JobStatus.IN_PROGRESS.getStatus(), LEAVERSAPI);
        int pageValue = Integer.parseInt(page);
        do {
            Response leaverApiResponse = getLeaversResponseFromElinks(pageValue++);
            leaverApiResponse = elinksResponsesHelper.saveElinksResponse(LEAVERSAPI, leaverApiResponse);
            httpStatus = HttpStatus.valueOf(leaverApiResponse.status());
            ResponseEntity<Object> responseEntity;

            if (httpStatus.is2xxSuccessful()) {
                responseEntity = JsonFeignResponseUtil.toResponseEntity(leaverApiResponse, LeaversRequest.class);
                LeaversRequest elinkLeaverResponseRequest = (LeaversRequest) responseEntity.getBody();
                log.info(":::: elinkPeopleResponseRequest " + elinkLeaverResponseRequest);
                if (Optional.ofNullable(elinkLeaverResponseRequest).isPresent()
                        && Optional.ofNullable(elinkLeaverResponseRequest.getPagination()).isPresent()
                        && Optional.ofNullable(elinkLeaverResponseRequest.getLeaversResultsRequests()).isPresent()) {
                    isMorePagesAvailable = elinkLeaverResponseRequest.getPagination().getMorePages();
                    elinksPeopleLeaverService.processLeavers(elinkLeaverResponseRequest.getLeaversResultsRequests());

                } else {
                    elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                            schedulerStartTime,
                            now(),
                            RefDataElinksConstants.JobStatus.FAILED.getStatus(), LEAVERSAPI);
                    throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
                }
            } else {
                elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                        schedulerStartTime,
                        now(),
                        RefDataElinksConstants.JobStatus.FAILED.getStatus(), LEAVERSAPI);
                handleELinksErrorResponse(httpStatus);
            }
            pauseThread(Long.valueOf(threadPauseTime));
        } while (isMorePagesAvailable);

        elinkLeaversWrapperResponse.setMessage(LEAVERSSUCCESS);

        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                now(),
                RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), LEAVERSAPI);

        return ResponseEntity
                .status(httpStatus)
                .body(elinkLeaversWrapperResponse);
    }

    private static void pauseThread(Long thredPauseTime) {
        try {
            Thread.sleep(thredPauseTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, THREAD_INVOCATION_EXCEPTION,
                    THREAD_INVOCATION_EXCEPTION);
        }
    }

    private Response getDeletedResponseFromElinks(int currentPage) {
        String leftSince = getDeletedSince();
        try {
            return elinksFeignClient.getDeletedDetails(leftSince, perPage, String.valueOf(currentPage));
        } catch (FeignException ex) {
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }
    }

    private String getDeletedSince() {
        String updatedSince;
        LocalDateTime maxSchedulerEndTime;
        if (isCustomizeUpdatedSince) {
            updatedSince = commonUtil.getUpdatedDateFormat(lastUpdated);
        } else {
            try {
                maxSchedulerEndTime = dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime();
            } catch (Exception ex) {
                throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, AUDIT_DATA_ERROR, AUDIT_DATA_ERROR);
            }
            if (Optional.ofNullable(maxSchedulerEndTime).isEmpty()) {
                updatedSince = commonUtil.getUpdatedDateFormat(lastUpdated);
            } else {
                updatedSince = maxSchedulerEndTime.toString();
                updatedSince = updatedSince.substring(0, updatedSince.indexOf('T'));
            }
        }

        log.info("Deleted Service updatedSince : " + updatedSince);
        return updatedSince;
    }

    @Override
    @Transactional("transactionManager")
    public ResponseEntity<ElinkDeletedWrapperResponse> retrieveDeleted() {
        boolean isMorePagesAvailable = true;
        HttpStatus httpStatus = null;
        LocalDateTime schedulerStartTime = now();
        log.info("Calling Elinks Deleted service");
        ElinkDeletedWrapperResponse elinkDeletedWrapperResponse = new ElinkDeletedWrapperResponse();

        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
            schedulerStartTime,
            null,
            RefDataElinksConstants.JobStatus.IN_PROGRESS.getStatus(), DELETEDAPI);

        int pageValue = Integer.parseInt(page);
        do {
            Response deletedApiResponse = getDeletedResponseFromElinks(pageValue++);
            deletedApiResponse = elinksResponsesHelper.saveElinksResponse(DELETEDAPI, deletedApiResponse);
            httpStatus = HttpStatus.valueOf(deletedApiResponse.status());
            ResponseEntity<Object> responseEntity;

            if (httpStatus.is2xxSuccessful()) {
                responseEntity = JsonFeignResponseUtil
                    .toResponseEntity(deletedApiResponse, ElinksDeleteApiResponse.class);
                ElinksDeleteApiResponse elinkDeletedResponseRequest = (ElinksDeleteApiResponse)
                    responseEntity.getBody();
                if (Optional.ofNullable(elinkDeletedResponseRequest).isPresent()
                    && Optional.ofNullable(elinkDeletedResponseRequest
                    .getPagination()).isPresent()
                    && Optional.ofNullable(elinkDeletedResponseRequest.getDeletedResponse()).isPresent()) {
                    isMorePagesAvailable = elinkDeletedResponseRequest.getPagination().getMorePages();
                    processDeletedResponse(elinkDeletedResponseRequest);

                } else {
                    elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                        schedulerStartTime,
                        now(),
                        RefDataElinksConstants.JobStatus.FAILED.getStatus(), DELETEDAPI);
                    throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
                }
            } else {
                elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime,
                    now(),
                    RefDataElinksConstants.JobStatus.FAILED.getStatus(), DELETEDAPI);
                handleELinksErrorResponse(httpStatus);
            }
            pauseDeletedThread(Long.valueOf(threadPauseTime));
        } while (isMorePagesAvailable);

        elinkDeletedWrapperResponse.setMessage(DELETEDSUCCESS);

        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
            schedulerStartTime,
            now(),
            RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), DELETEDAPI);

        return ResponseEntity
            .status(httpStatus)
            .body(elinkDeletedWrapperResponse);
    }

    private static void pauseDeletedThread(Long thredPauseTime) {
        try {
            Thread.sleep(thredPauseTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, THREAD_INVOCATION_EXCEPTION,
                THREAD_INVOCATION_EXCEPTION);
        }
    }


    private void processDeletedResponse(ElinksDeleteApiResponse elinkDeletedResponseRequest) {
        try {
            updateDeleted(elinkDeletedResponseRequest.getDeletedResponse());
        } catch (Exception ex) {
            throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, DATA_UPDATE_ERROR, DATA_UPDATE_ERROR);
        }
    }

    public void updateDeleted(List<DeletedResponse> deletedResponse) {

        List<Triple<String, String, String>> deletedId = new ArrayList<>();

        String updateDeletedId = "UPDATE dbjudicialdata.judicial_user_profile SET date_of_deletion = Date(?) , "
                + "deleted_flag = ?,active_flag=false WHERE personal_code = ?";

        deletedResponse.stream().filter(request -> nonNull(request.getPersonalCode())).forEach(s ->
                deletedId.add(Triple.of(s.getPersonalCode(), s.getDeleted(), s.getDeletedOn())));
        log.info("Insert Query batch Response from Deleted" + deletedId.size());
        jdbcTemplate.batchUpdate(
                updateDeletedId,
                deletedId,
                10,
                (ps, argument) -> {
                    ps.setString(1, argument.getRight());
                    ps.setBoolean(2, Boolean.valueOf(argument.getMiddle()));
                    ps.setString(3, argument.getLeft());
                });
    }


    @Transactional("transactionManager")
    public void cleanUpElinksResponses() {
        try {
            elinksResponsesRepository
                    .deleteByCreatedDateBefore(LocalDateTime.now().minusDays(cleanElinksResponsesDays));
            log.info("Cleaning Elinks Responses Table completed Successfully");
        } catch (Exception exception) {
            log.warn("Cleaning Elinks Responses Table failed", exception);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    now(), null,
                    "elinks_responses", "Error while deleting records from elinks_responses table",
                    ELINKSRESPONSES, null, null, exception.getMessage());
        }
    }


    @Transactional("transactionManager")
    public void deleteJohProfiles(LocalDateTime schedulerStartTime) {
        try {
            if (delJohProfiles) {
                List<UserProfile> userProfiles = profileRepository.findByDeletedFlag(true);

                List<String> personalCodes = userProfiles.stream().map(UserProfile::getPersonalCode).toList();
                if (!personalCodes.isEmpty()) {
                    elinksPeopleDeleteService.clearDeletedPeople(personalCodes);
                    log.info("Deleted JOH UserProfiles Successfully");
                    elinkDataExceptionHelper.auditException(personalCodes, schedulerStartTime);
                }
            }
        } catch (Exception exception) {
            log.warn("Deleting JOH User Profiles failed");
        }
    }
}
