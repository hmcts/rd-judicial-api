package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.PeopleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.ResultsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Location;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.BaseLocationResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.LocationResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ELinksService;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.AUDIT_DATA_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DATA_UPDATE_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_DATA_STORE_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSSUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.THREAD_INVOCATION_EXCEPTION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.UPDATED_SINCE;

@Service
@Slf4j
public class ELinksServiceImpl implements ELinksService {

    @Autowired
    BaseLocationRepository baseLocationRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private DataloadSchedularAuditRepository dataloadSchedularAuditRepository;

    @Value("${elinks.people.perPage}")
    private String perPage;

    @Value("${elinks.people.page}")
    private String page;

    @Autowired
    ElinksFeignClient elinksFeignClient;

    @Value("${elinks.people.threadPauseTime}")
    private String threadPauseTime;



    @Override
    public ResponseEntity<ElinkBaseLocationWrapperResponse> retrieveBaseLocation() {

        log.info("Get location details ELinksService.retrieveBaseLocation ");

        Response baseLocationsResponse;
        HttpStatus httpStatus;
        ResponseEntity<ElinkBaseLocationWrapperResponse> result = null;
        try {

            baseLocationsResponse = elinksFeignClient.getBaseLocationDetails();

            httpStatus = HttpStatus.valueOf(baseLocationsResponse.status());

            log.info("Get location details response status ELinksService.retrieveBaseLocation" + httpStatus.value());
            if (httpStatus.is2xxSuccessful()) {
                ResponseEntity<Object> responseEntity = JsonFeignResponseUtil
                        .toResponseEntity(baseLocationsResponse,
                                ElinkBaseLocationResponse.class);


                if (nonNull(responseEntity.getBody())) {
                    ElinkBaseLocationResponse elinkLocationResponse = (ElinkBaseLocationResponse)
                            responseEntity.getBody();
                    if (nonNull(elinkLocationResponse) && nonNull(elinkLocationResponse.getResults())) {
                        List<BaseLocationResponse> locationResponseList = elinkLocationResponse.getResults();

                        List<BaseLocation> baselocations = locationResponseList.stream()
                                .map(BaseLocationResponse::toBaseLocationEntity)
                                .toList();
                        result = loadBaseLocationData(baselocations);
                    }
                }
            } else {
                handleELinksErrorResponse(httpStatus);
            }


        } catch (FeignException ex) {
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }
        return result;
    }

    @Override
    public ResponseEntity<ElinkLocationWrapperResponse> retrieveLocation() {

        log.info("Get location details ELinksService.retrieveLocation ");

        Response locationsResponse;
        HttpStatus httpStatus;
        ResponseEntity<ElinkLocationWrapperResponse> result = null;
        try {

            locationsResponse = elinksFeignClient.getLocationDetails();

            httpStatus = HttpStatus.valueOf(locationsResponse.status());

            log.info("Get location details response status ELinksService.retrieveLocation" + httpStatus.value());
            if (httpStatus.is2xxSuccessful()) {
                ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toResponseEntity(locationsResponse,
                        ElinkLocationResponse.class);


                ElinkLocationResponse elinkLocationResponse = (ElinkLocationResponse) responseEntity.getBody();
                if (nonNull(elinkLocationResponse)) {
                    List<LocationResponse> locationResponseList = elinkLocationResponse.getResults();

                    List<Location> locations = locationResponseList.stream()
                            .map(locationRes -> new Location(locationRes.getId(), locationRes.getName(),
                                    StringUtils.EMPTY))
                            .toList();
                    result = loadLocationData(locations);
                }

            } else {
                handleELinksErrorResponse(httpStatus);
            }


        } catch (FeignException ex) {
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }
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

    private ResponseEntity<ElinkLocationWrapperResponse> loadLocationData(List<Location> locations) {
        ResponseEntity<ElinkLocationWrapperResponse> result;
        try {

            locationRepository.saveAll(locations);

            ElinkLocationWrapperResponse elinkLocationWrapperResponse = new ElinkLocationWrapperResponse();
            elinkLocationWrapperResponse.setMessage(LOCATION_DATA_LOAD_SUCCESS);


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
            return elinksFeignClient.getLeaversDetials(leftSince, perPage, String.valueOf(currentPage));
        } catch (FeignException ex) {
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }
    }

    private String getUpdateSince() {
        String updatedSince;
        LocalDateTime maxSchedulerEndTime;
        try {
            maxSchedulerEndTime = dataloadSchedularAuditRepository.findLatestSchedularEndTime();
        } catch (Exception ex) {
            throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, AUDIT_DATA_ERROR, AUDIT_DATA_ERROR);
        }
        if (Optional.ofNullable(maxSchedulerEndTime).isEmpty()) {
            updatedSince = UPDATED_SINCE;
        } else {
            updatedSince = maxSchedulerEndTime.toString();
            updatedSince = updatedSince.substring(0, updatedSince.indexOf('T'));
        }
        log.info("updatedSince : " + updatedSince);
        return updatedSince;
    }

    @Override
    @Transactional("transactionManager")
    public ResponseEntity<ElinkLeaversWrapperResponse> retrieveLeavers() {
        boolean isMorePagesAvailable = true;
        HttpStatus httpStatus = null;
        ElinkLeaversWrapperResponse elinkLeaversWrapperResponse = new ElinkLeaversWrapperResponse();
        int pageValue = Integer.parseInt(page);
        do {
            Response leaverApiResponse = getLeaversResponseFromElinks(pageValue++);
            httpStatus = HttpStatus.valueOf(leaverApiResponse.status());
            ResponseEntity<Object> responseEntity;

            if (httpStatus.is2xxSuccessful()) {
                responseEntity = JsonFeignResponseUtil.toResponseEntity(leaverApiResponse, PeopleRequest.class);
                PeopleRequest elinkLeaverResponseRequest = (PeopleRequest) responseEntity.getBody();
                if (Optional.ofNullable(elinkLeaverResponseRequest).isPresent()
                        && Optional.ofNullable(elinkLeaverResponseRequest.getPagination()).isPresent()
                        && Optional.ofNullable(elinkLeaverResponseRequest.getResultsRequests()).isPresent()) {
                    isMorePagesAvailable = elinkLeaverResponseRequest.getPagination().getMorePages();
                    processLeaverResponse(elinkLeaverResponseRequest);
                } else {
                    throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
                }
            } else {
                handleELinksErrorResponse(httpStatus);
            }
            pauseThread(Long.valueOf(threadPauseTime));
        } while (isMorePagesAvailable);

        elinkLeaversWrapperResponse.setMessage(LEAVERSSUCCESS);

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



    private void processLeaverResponse(PeopleRequest elinkLeaverResponseRequest) {
        try {
            updateLeavers(elinkLeaverResponseRequest.getResultsRequests());
        } catch (Exception ex) {
            throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, DATA_UPDATE_ERROR, DATA_UPDATE_ERROR);
        }

    }

    private LocalDate convertToLocalDate(String date) {
        if (Optional.ofNullable(date).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
        }
        return null;
    }

    public void updateLeavers(List<ResultsRequest> resultsRequests) {

        List<Triple<String, String,String>> leaversId = new ArrayList<>();

        String updateLeaversId = "UPDATE dbjudicialdata.judicial_user_profile SET last_working_date = ? , "
                + "active_flag = ? WHERE personal_code = ?";

        resultsRequests.stream().filter(request -> nonNull(request.getPersonalCode())).forEach(s ->
                leaversId.add(Triple.of(s.getPersonalCode(), s.getLeaver(),s.getLeftOn())));
        log.info("Insert Query batch Response from IDAM" + leaversId.size());
        jdbcTemplate.batchUpdate(
                updateLeaversId,
                leaversId,
                10,
                new ParameterizedPreparedStatementSetter<Triple<String, String, String>>() {
                    public void setValues(PreparedStatement ps, Triple<String, String, String> argument)
                            throws SQLException {
                        ps.setString(1, argument.getRight());
                        ps.setString(2, argument.getMiddle());
                        ps.setString(3, argument.getLeft());
                    }
                });
    }
}