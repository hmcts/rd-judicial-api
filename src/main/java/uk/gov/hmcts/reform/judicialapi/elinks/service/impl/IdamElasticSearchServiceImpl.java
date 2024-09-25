package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.idam.client.models.TokenRequest;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELASTICSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAMSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAM_ERROR_MESSAGE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAM_ID_KEY;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAM_TOKEN_ERROR_MESSAGE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECT_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECT_ID_KEY;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.SIDAM_IDS_UPDATED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USER_PROFILE;

@Slf4j
@Component
public class IdamElasticSearchServiceImpl implements IdamElasticSearchService {

    public static final String SCOPE = "openid profile roles manage-user create-user search-user";

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
            String[] userDetails = authorisation.split(":");
            TokenRequest tokenRequest = new TokenRequest(props.getClientId(),
                    props.getClientAuthorization(),
                    "password",
                    props.getRedirectUri(),
                    userDetails[0].trim(),
                    userDetails[1].trim(),
                    SCOPE, "", "");

            idamOpenIdTokenResponse = idamFeignClient.getOpenIdToken(tokenRequest);

            if (idamOpenIdTokenResponse == null) {
                throw new ElinksException(HttpStatus.FORBIDDEN, IDAM_TOKEN_ERROR_MESSAGE,
                        IDAM_TOKEN_ERROR_MESSAGE);
            }
        } catch (Exception e) {
            elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                now(),
                RefDataElinksConstants.JobStatus.FAILED.getStatus(),ELASTICSEARCH, e.getMessage());
            throw new ElinksException(HttpStatus.FORBIDDEN, IDAM_TOKEN_ERROR_MESSAGE,
                    IDAM_TOKEN_ERROR_MESSAGE);
        }
        return idamOpenIdTokenResponse.getAccessToken();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Object> getIdamElasticSearchSyncFeed() {

        log.info("Calling idam elastic search");

        Set<IdamResponse> judicialUsers = new HashSet<>();
        int count = 0;
        int totalCount = 0;

        LocalDateTime schedulerStartTime = now();
        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
            schedulerStartTime,
            null,
            RefDataElinksConstants.JobStatus.IN_PROGRESS.getStatus(), ELASTICSEARCH);

        log.info("Calling idam client");
        try {
            String bearerToken = "Bearer ".concat(getIdamBearerToken(schedulerStartTime));
            do {
                List<IdamResponse> users = idamFeignClient.searchUsers(bearerToken,
                        String.format(idamSearchQuery,idamElasticSearchQueryHours()),
                        String.valueOf(recordsPerPage),
                        String.valueOf(count));
                judicialUsers.addAll(users);
                count++;
                log.debug("{}:: batch count :: ", count);
            } while (totalCount > 0 && recordsPerPage * count < totalCount);
        } catch (Exception ex) {
            //There is No header.
            log.error("{}:: Error processing IDAM elastic search query ::{}", loggingComponentName, ex);
            elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime,
                    now(),
                    RefDataElinksConstants.JobStatus.FAILED.getStatus(), ELASTICSEARCH, ex.getMessage());
            throw new ElinksException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
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
                .ok()
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
        List<UserProfile> userProfiles = userProfileRepository.fetchObjectIdMissingSidamId();
        int userProfileSize = userProfiles.size();
        String bearerToken = userProfileSize > 0 ? "Bearer ".concat(getIdamBearerToken(schedulerStartTime)) : "";
        log.debug("{}:: Number of User profiles from JRD :: " + userProfileSize, loggingComponentName);
        AtomicReference<Boolean> isPartialSuccess = new AtomicReference<>(false);
        userProfiles.forEach(userProfile -> {
            Map<String, String> params = new HashMap<>();
            String query = idamFindQuery.concat(userProfile.getObjectId());
            params.put("query", query);
            log.debug("{}:: search elk query {}", loggingComponentName, query);
            try {
                List<IdamResponse> responses = idamFeignClient.searchUsers(bearerToken, query, null, null);
                judicialUsers.addAll(responses);
            } catch (Exception ex) {
                String errorDescription = "IDAM Search Service failed";
                log.error("{}:: " + errorDescription + " ::{}", loggingComponentName, ex);
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                        schedulerStartTime,
                        IDAMSEARCH,
                        IDAMSEARCH,
                        errorDescription,
                        IDAMSEARCH,
                        "Object ID:" + userProfile.getObjectId(),
                        0,
                        ex.getMessage());
                isPartialSuccess.set(true);
            }
        });

        validateObjectIds(judicialUsers, schedulerStartTime);

        updateSidamIds(judicialUsers);
        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                now(),
                isPartialSuccess.get()
                        ? RefDataElinksConstants.JobStatus.PARTIAL_SUCCESS.getStatus()
                        : RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),
                IDAMSEARCH);

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
                        schedulerStartTime, OBJECT_ID_KEY + entry.getKey(),
                        OBJECT_ID, errorDescription, USER_PROFILE,
                        IDAM_ID_KEY + entry.getValue(), pageValue);
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
        Map<String, String> objectIdSidamIdMap = new HashMap<>();
        sidamUsers.stream().filter(user -> nonNull(user.getSsoId()))
                .forEach(s -> objectIdSidamIdMap.put(s.getSsoId(), s.getId()));

        List<UserProfile> userProfiles = userProfileRepository
                .fetchUserProfiles(objectIdSidamIdMap.keySet().stream().toList());

        List<Pair<String, String>> sidamObjectId = new ArrayList<>();
        userProfiles.forEach(userProfile -> {
            String idamId = objectIdSidamIdMap.get(userProfile.getObjectId());
            String userProfileIdamId = userProfile.getSidamId();
            if (!idamId.equalsIgnoreCase(userProfileIdamId)) {
                sidamObjectId.add(Pair.of(idamId, userProfile.getObjectId()));
            }
        });
        log.debug("Insert Query batch Response from IDAM " + sidamObjectId.size());
        String updateSidamIds = "UPDATE dbjudicialdata.judicial_user_profile SET sidam_id = ? "
                + "WHERE object_id = ? ";
        jdbcTemplate.batchUpdate(
                updateSidamIds,
                sidamObjectId,
                10,
                (ps, argument) -> {
                    log.debug("SIDAM Id {} and Object ID {} to update " + argument.getLeft(), argument.getRight());
                    ps.setString(1, argument.getLeft());
                    ps.setString(2, argument.getRight());
                });

    }
}