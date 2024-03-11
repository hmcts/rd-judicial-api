package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.feign.IdamFeignClient;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkIdamWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamOpenIdTokenResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.IdamElasticSearchService;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataExceptionHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.elinks.util.SendEmail;
import uk.gov.hmcts.reform.judicialapi.elinks.util.SqlConstants;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELASTICSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAMSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAM_ERROR_MESSAGE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAM_TOKEN_ERROR_MESSAGE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECT_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.SIDAM_IDS_UPDATED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USER_PROFILE;

@Slf4j
@Component
public class IdamElasticSearchServiceImpl implements IdamElasticSearchService {

    @Value("${logging-component-name}")
    String loggingComponentName;

    @Value("${elastic.search.query}")
    String idamSearchQuery;

    @Value("${idam.find.query}")
    String idamFindQuery;

    @Value("${elastic.search.recordsPerPage}")
    int recordsPerPage;

    @Autowired
    IdamFeignClient idamFeignClient;

    @Autowired
    IdamTokenConfigProperties props;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SendEmail sendEmail;

    @Autowired
    private ProfileRepository userProfileRepository;

    @Autowired
    ElinkDataExceptionHelper elinkDataExceptionHelper;

    @Autowired
    ElinkDataIngestionSchedularAudit elinkDataIngestionSchedularAudit;

    @Value("${elinks.people.page}")
    String page;

    @Override
    public String getIdamBearerToken(LocalDateTime schedulerStartTime) {
        IdamOpenIdTokenResponse idamOpenIdTokenResponse = null;
        try {

            String authorisation = props.getAuthorization();
            Map<String, String> formParams = new HashMap<>();
            formParams.put("grant_type", "password");
            String[] userDetails = authorisation.split(":");
            formParams.put("username", userDetails[0].trim());
            formParams.put("password", userDetails[1].trim());
            formParams.put("client_id", props.getClientId());
            formParams.put("redirect_uri", props.getRedirectUri());
            formParams.put("client_secret", props.getClientAuthorization());
            formParams.put("scope", "openid profile roles manage-user create-user search-user");

            idamOpenIdTokenResponse = idamFeignClient.getOpenIdToken(formParams);

            if (idamOpenIdTokenResponse == null) {
                throw new ElinksException(HttpStatus.FORBIDDEN, IDAM_TOKEN_ERROR_MESSAGE,
                        IDAM_TOKEN_ERROR_MESSAGE);
            }
        } catch (Exception e) {
            elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                now(),
                RefDataElinksConstants.JobStatus.FAILED.getStatus(),ELASTICSEARCH);
            throw new ElinksException(HttpStatus.FORBIDDEN, IDAM_TOKEN_ERROR_MESSAGE,
                    IDAM_TOKEN_ERROR_MESSAGE);
        }
        return idamOpenIdTokenResponse.getAccessToken();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Object> getIdamElasticSearchSyncFeed() {

        log.info("Calling idam elastic search");

        Map<String, String> params = new HashMap<>();
        params.put("size",String.valueOf(recordsPerPage));
        params.put("query",String.format(idamSearchQuery,idamElasticSearchQueryHours()));
        log.debug("{}:: search elk query {}", loggingComponentName, params.get("query"));
        Set<IdamResponse> judicialUsers = new HashSet<>();
        int count = 0;
        int totalCount = 0;
        ResponseEntity<Object> responseEntity = null;
        Response response = null;

        LocalDateTime schedulerStartTime = now();
        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
            schedulerStartTime,
            null,
            RefDataElinksConstants.JobStatus.IN_PROGRESS.getStatus(), ELASTICSEARCH);

        log.info("Calling idam client");
        try {
            String bearerToken = "Bearer ".concat(getIdamBearerToken(schedulerStartTime));
            do {
                params.put("page", String.valueOf(count));
                response = idamFeignClient.getUserFeed(bearerToken, params);
                logIdamResponses(response);
                responseEntity = JsonFeignResponseUtil.toResponseEntity(response,
                        new TypeReference<Set<IdamResponse>>() {});
                if (response.status() == 200) {

                    Set<IdamResponse> users = (Set<IdamResponse>) responseEntity.getBody();
                    judicialUsers.addAll(users);

                    List<String> headerCount = responseEntity.getHeaders().get("X-Total-Count");
                    if (headerCount != null && !headerCount.isEmpty()
                            && !headerCount.get(0).isEmpty()) {

                        totalCount = Integer.parseInt(headerCount.get(0));
                        log.debug("{}:: Header Records count from Idam :: " + totalCount, loggingComponentName);
                    }

                } else {
                    log.error("{}:: Idam Search Service Failed :: ", loggingComponentName);
                    throw new ElinksException(responseEntity.getStatusCode(), IDAM_ERROR_MESSAGE,
                            IDAM_ERROR_MESSAGE);
                }
                count++;
                log.debug("{}:: batch count :: ", count);
            } while (totalCount > 0 && recordsPerPage * count < totalCount);
        } catch (Exception ex) {
            //There is No header.
            log.error("{}:: X-Total-Count header not return Idam Search Service::{}", loggingComponentName, ex);
            elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime,
                    now(),
                    RefDataElinksConstants.JobStatus.FAILED.getStatus(), ELASTICSEARCH);
            throw new ElinksException(HttpStatus.valueOf(response.status()), ex.getMessage(),
                    IDAM_ERROR_MESSAGE);
        }

        validateObjectIds(judicialUsers,schedulerStartTime);
        sendEmail.sendEmail(schedulerStartTime);

        updateSidamIds(judicialUsers);
        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
            schedulerStartTime,
            now(),
            RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), ELASTICSEARCH);

        return ResponseEntity
                .status(response.status())
                .body(judicialUsers);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Object> getIdamDetails() {

        log.info("Calling idam details");

        LocalDateTime schedulerStartTime = now();
        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
            schedulerStartTime,
            null,
            RefDataElinksConstants.JobStatus.IN_PROGRESS.getStatus(), IDAMSEARCH);
        Set<IdamResponse> judicialUsers = new HashSet<>();
        List<UserProfile> userProfiles = userProfileRepository.fetchObjectIdFromCurrentDate();
        String bearerToken = userProfiles.size() > 0 ? "Bearer ".concat(getIdamBearerToken(schedulerStartTime)) : "";
        userProfiles.forEach(userProfile -> {
            Map<String, String> params = new HashMap<>();
            params.put("query", idamFindQuery.concat(userProfile.getObjectId()));
            log.debug("{}:: search elk query {}", loggingComponentName, params.get("query"));
            int totalCount = 0;
            ResponseEntity<Object> responseEntity = null;
            Response response = null;
            try {
                response = idamFeignClient.getUserFeed(bearerToken, params);
                logIdamResponses(response);
                responseEntity = JsonFeignResponseUtil.toResponseEntity(response,
                        new TypeReference<Set<IdamResponse>>() {
                        });
                if (response.status() == 200) {

                    Set<IdamResponse> users = (Set<IdamResponse>) responseEntity.getBody();
                    judicialUsers.addAll(users);

                    List<String> headerCount = responseEntity.getHeaders().get("X-Total-Count");
                    if (headerCount != null && !headerCount.isEmpty()
                            && !headerCount.get(0).isEmpty()) {

                        totalCount = Integer.parseInt(headerCount.get(0));
                        log.debug("{}:: Header Records count from Idam :: " + totalCount, loggingComponentName);
                    }

                } else {
                    log.error("{}:: Idam Search Service Failed :: ", loggingComponentName);
                    throw new ElinksException(responseEntity.getStatusCode(), IDAM_ERROR_MESSAGE,
                            IDAM_ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                //There is No header.
                log.error("{}:: X-Total-Count header not return Idam Search Service::{}", loggingComponentName, ex);
                elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                        schedulerStartTime,
                        now(),
                        RefDataElinksConstants.JobStatus.FAILED.getStatus(), IDAMSEARCH);
                throw new ElinksException(HttpStatus.valueOf(response.status()), ex.getMessage(),
                        IDAM_ERROR_MESSAGE);
            }
        });

        validateObjectIds(judicialUsers,schedulerStartTime);

        updateSidamIds(judicialUsers);
        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
            schedulerStartTime,
            now(),
            RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), IDAMSEARCH);

        ElinkIdamWrapperResponse response = new ElinkIdamWrapperResponse();
        response.setMessage(SIDAM_IDS_UPDATED);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    public void validateObjectIds(Set<IdamResponse> sidamUsers,LocalDateTime schedulerStartTime) {

        Map<String,String> sidamObjectId = new HashMap<>();

        sidamUsers.stream().filter(user -> nonNull(user.getSsoId())).forEach(s ->
                sidamObjectId.put(s.getSsoId(), s.getId()));

        List<String> jrdObjectIdsList = userProfileRepository.fetchObjectId();

        Map<String,String> filteredObjectIds = sidamObjectId.entrySet()
                .stream()
                .filter(entry -> !jrdObjectIdsList.contains(entry.getKey()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        String errorDescription = "Object ID, received from Idam, is not present in Judicial Reference Data";
        int pageValue = Integer.parseInt(page);


        if (!filteredObjectIds.isEmpty()) {
            for (var entry : filteredObjectIds.entrySet()) {
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                        schedulerStartTime,
                        entry.getKey(),
                        OBJECT_ID, errorDescription, USER_PROFILE, entry.getValue(),pageValue);
            }
        }
    }

    private void logIdamResponses(Response response) {
        log.debug("Logging Response from IDAM");
        if (response != null) {
            log.debug("{}:: Response code from idamClient.getUserFeed {}", loggingComponentName, response.status());
            if (response.status() != 200 && response.body() != null) {
                log.debug("{}:: Response body from Idam Client ::{}", loggingComponentName, response.status());
            }
        }
    }

    private Long idamElasticSearchQueryHours() {

        RowMapper<Timestamp> rowMapper = (rs, i) -> rs.getTimestamp(1);
        List<Timestamp> resultSet = jdbcTemplate.query(SqlConstants.SELECT_IDM_JOB_STATUS_SQL,rowMapper);

        Timestamp maxSchedulerEndTime = CollectionUtils.isNotEmpty(resultSet) ? resultSet.get(0) : null;
        log.debug("idamElasticSearchQuery  date from audit table {}",maxSchedulerEndTime);
        return maxSchedulerEndTime == null ? 72 : Math.addExact(ChronoUnit.HOURS.between(
                maxSchedulerEndTime.toLocalDateTime(),LocalDateTime.now()), 1);
    }

    public void updateSidamIds(Set<IdamResponse> sidamUsers) {
        List<Pair<String, String>> sidamObjectId = new ArrayList<>();

        String updateSidamIds = "UPDATE dbjudicialdata.judicial_user_profile SET sidam_id = ? "
                + "WHERE object_id = ? ";
        sidamUsers.stream().filter(user -> nonNull(user.getSsoId())).forEach(s ->
                sidamObjectId.add(Pair.of(s.getId(), s.getSsoId())));
        log.debug("Insert Query batch Response from IDAM " + sidamObjectId.size());
        jdbcTemplate.batchUpdate(
                updateSidamIds,
                sidamObjectId,
                10,
                new ParameterizedPreparedStatementSetter<Pair<String, String>>() {
                    public void setValues(PreparedStatement ps, Pair<String, String> argument) throws SQLException {
                        ps.setString(1, argument.getLeft());
                        ps.setString(2, argument.getRight());
                    }
                });

    }
}