package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.AppointmentsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.AuthorisationsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.LeaversResultsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.PeopleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.ResultsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JrdRegionMappingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationMapppingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleDeleteService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleLeaverService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleService;
import uk.gov.hmcts.reform.judicialapi.elinks.util.CommonUtil;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataExceptionHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksResponsesHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.elinks.util.SendEmail;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.naming.InvalidNameException;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.DateUtil.convertToLocalDate;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.DateUtil.convertToLocalDateTime;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENTID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENTIDNOTAVAILABLE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENTID_IS_NULL;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENT_ID__KEY;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENT_TABLE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.AUTHORISATION_ID__KEY;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.AUTHORISATION_TABLE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.CFTREGIONIDFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DATA_UPDATE_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.EMAILID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.END_DATE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.INVALIDROLEID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.INVALIDROLENAMES;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.INVALID_JUDICIARY_ROLE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.INVALID_ROLES;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIALROLETYPE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIARY_ROLE_ID__KEY;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.KNOWN_AS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LAST_WORKING_DATE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONIDFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECTIDISDUPLICATED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECTIDISPRESENT;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECT_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECT_ID_KEY;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PARENTIDFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PERSONALCODE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PERSONAL_CODE_KEY;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.RETIREMENT_DATE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ROLENAME;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.START_DATE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.THREAD_INVOCATION_EXCEPTION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.TYPEIDFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USERPROFILEEMAILID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USERPROFILEFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USERPROFILEISPRESENT;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USER_PROFILE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USER_PROFILE_KNOWN_AS;

@Slf4j
@Service
public class ElinksPeopleServiceImpl implements ElinksPeopleService {

    @Autowired
    private ElinksFeignClient elinksFeignClient;

    @Autowired
    private BaseLocationRepository baseLocationRepository;

    @Autowired
    ElinkDataExceptionHelper elinkDataExceptionHelper;

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    @Autowired
    private AuthorisationsRepository authorisationsRepository;

    @Autowired
    SendEmail sendEmail;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private JudicialRoleTypeRepository judicialRoleTypeRepository;

    @Autowired
    private LocationMapppingRepository locationMappingRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private JrdRegionMappingRepository regionMappingRepository;

    @Autowired
    private DataloadSchedularAuditRepository dataloadSchedularAuditRepository;

    @Autowired
    ElinkDataIngestionSchedularAudit elinkDataIngestionSchedularAudit;

    @Autowired
    ElinksPeopleDeleteService elinksPeopleDeleteService;

    @Autowired
    ElinksPeopleLeaverService elinksPeopleLeaverService;

    @Autowired
    CommonUtil commonUtil;

    private boolean partialSuccessFlag = false;

    private Map<String,UserProfile> userProfileCache = new HashMap<String,UserProfile>();
    private List<UserProfile> userProfilesSnapshot = new ArrayList<>();

    @Value("${elinks.people.lastUpdated}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String lastUpdated;

    @Value("${elinks.people.perPage}")
    private String perPage;

    @Value("${elinks.people.threadPauseTime}")
    private String threadPauseTime;

    @Value("${elinks.people.threadRetriggerPauseTime}")
    private String threadRetriggerPauseTime;

    @Value("${elinks.people.retriggerStatus}")
    private List<Integer> retriggerStatusCode;

    @Value("${elinks.people.updatedSinceEnabled:false}")
    private boolean isCustomizeUpdatedSince;

    @Value("${elinks.people.page}")
    private String page;

    @Value("${elinks.people.retriggerThreshold}")
    private int retriggerThreshold;

    @Value("${elinks.people.includePreviousAppointments}")
    private String includePreviousAppointments;

    @Autowired
    ElinksResponsesHelper elinksResponsesHelper;

    @Override
    public ResponseEntity<ElinkPeopleWrapperResponse> updatePeople() {

        partialSuccessFlag = false;
        userProfileCache.clear();
        boolean isMorePagesAvailable = true;
        HttpStatus httpStatus = null;
        LocalDateTime schedulerStartTime = now();
        String status = RefDataElinksConstants.JobStatus.SUCCESS.getStatus();

        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                null,
                RefDataElinksConstants.JobStatus.IN_PROGRESS.getStatus(), PEOPLEAPI);
        userProfilesSnapshot = profileRepository.findAll();
        List<LeaversResultsRequest> leavers = new ArrayList<>();
        List<String> deletedUsers = new ArrayList<>();
        int pageValue = Integer.parseInt(page);
        int retryCount = 0;
        try {
            log.info("calling elinks people service");
            do {
                Response peopleApiResponse = getPeopleResponseFromElinks(pageValue++, schedulerStartTime);
                peopleApiResponse = elinksResponsesHelper.saveElinksResponse(PEOPLEAPI, peopleApiResponse);
                httpStatus = HttpStatus.valueOf(peopleApiResponse.status());
                ResponseEntity<Object> responseEntity;

                if (httpStatus.is2xxSuccessful()) {
                    responseEntity = JsonFeignResponseUtil.toResponseEntity(peopleApiResponse, PeopleRequest.class);
                    PeopleRequest elinkPeopleResponseRequest = (PeopleRequest) responseEntity.getBody();
                    if (Optional.ofNullable(elinkPeopleResponseRequest).isPresent()
                        && Optional.ofNullable(elinkPeopleResponseRequest.getPagination()).isPresent()
                        && Optional.ofNullable(elinkPeopleResponseRequest.getResultsRequests()).isPresent()) {
                        isMorePagesAvailable = elinkPeopleResponseRequest.getPagination().getMorePages();
                        processPeopleResponse(elinkPeopleResponseRequest, schedulerStartTime, pageValue);
                        leavers.addAll(retrieveLeavers(elinkPeopleResponseRequest));
                        deletedUsers.addAll(retrieveDeleted(elinkPeopleResponseRequest));
                    } else {
                        auditStatus(schedulerStartTime, RefDataElinksConstants.JobStatus.FAILED.getStatus());
                        throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR,
                            ELINKS_ACCESS_ERROR);
                    }
                } else if (retriggerStatusCode.contains(httpStatus.value()) && retryCount < retriggerThreshold) {
                    log.info(":::: Too Many Requests ");
                    pauseThread(Long.valueOf(threadRetriggerPauseTime), schedulerStartTime);
                    --pageValue;
                    retryCount++;
                    continue;
                } else {
                    auditStatus(schedulerStartTime, RefDataElinksConstants.JobStatus.FAILED.getStatus());
                    handleELinksErrorResponse(httpStatus);
                }
                pauseThread(Long.valueOf(threadPauseTime), schedulerStartTime);
            } while (isMorePagesAvailable);
            processLeaversResponse(leavers);
            processDeletedResponse(deletedUsers);
        } catch (Exception ex) {
            log.error("People service exception", ex);
            auditStatus(schedulerStartTime, RefDataElinksConstants.JobStatus.FAILED.getStatus(), ex);
            throw ex;
        }
        sendEmail.sendEmail(schedulerStartTime);

        if (partialSuccessFlag) {
            status = RefDataElinksConstants.JobStatus.PARTIAL_SUCCESS.getStatus();
        }

        userProfileCache.clear();
        userProfilesSnapshot = null;
        auditStatus(schedulerStartTime, status);
        ElinkPeopleWrapperResponse response = new ElinkPeopleWrapperResponse();
        response.setMessage(PEOPLE_DATA_LOAD_SUCCESS);

        return ResponseEntity
                .status(httpStatus)
                .body(response);
    }




    private void auditStatus(LocalDateTime schedulerStartTime, String status) {
        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                now(),status,PEOPLEAPI);
    }

    private void auditStatus(LocalDateTime schedulerStartTime, String status, Exception e) {
        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                now(),status,PEOPLEAPI, e.getMessage());
    }

    private Response getPeopleResponseFromElinks(int currentPage, LocalDateTime schedulerStartTime) {
        String updatedSince = getUpdateSince();
        log.info("includePreviousAppointments {}", includePreviousAppointments);
        try {
            return elinksFeignClient.getPeopleDetails(updatedSince, perPage, String.valueOf(currentPage),
                    Boolean.parseBoolean(includePreviousAppointments));
        } catch (FeignException ex) {
            auditStatus(schedulerStartTime, RefDataElinksConstants.JobStatus.FAILED.getStatus(), ex);
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }
    }

    private void pauseThread(Long thredPauseTime, LocalDateTime schedulerStartTime) {
        try {
            Thread.sleep(thredPauseTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            auditStatus(schedulerStartTime, RefDataElinksConstants.JobStatus.FAILED.getStatus(), e);
            throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, THREAD_INVOCATION_EXCEPTION,
                    THREAD_INVOCATION_EXCEPTION);
        }
    }

    private String getUpdateSince() {
        String updatedSince;
        LocalDateTime maxSchedulerEndTime;
        if (isCustomizeUpdatedSince) {
            updatedSince = commonUtil.getUpdatedDateFormat(lastUpdated);
        } else {
            try {
                maxSchedulerEndTime = dataloadSchedularAuditRepository.findLatestSchedularEndTime();
            } catch (Exception ex) {
                throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, DATA_UPDATE_ERROR, DATA_UPDATE_ERROR);
            }
            if (Optional.ofNullable(maxSchedulerEndTime).isEmpty()) {
                updatedSince = commonUtil.getUpdatedDateFormat(lastUpdated);
            } else {
                updatedSince = maxSchedulerEndTime.toString();
                updatedSince = updatedSince.substring(0, updatedSince.indexOf('T'));
            }
        }
        log.info("People Service updatedSince : " + updatedSince);
        return updatedSince;
    }

    private List<LeaversResultsRequest> retrieveLeavers(PeopleRequest elinkPeopleResponseRequest) {
        List<ResultsRequest> leaversResponse = elinkPeopleResponseRequest.getResultsRequests();
        return leaversResponse.stream()
                .filter(request -> isLeaver(request) && nonNull(request.getPersonalCode()))
                .map(request -> mapToLeaversRequest(request)).collect(Collectors.toList());
    }

    private List<String> retrieveDeleted(PeopleRequest elinkPeopleResponseRequest) {
        List<ResultsRequest> deletedResponses = elinkPeopleResponseRequest.getResultsRequests();
        return deletedResponses.stream()
                .filter(request -> isDeleted(request) && nonNull(request.getPersonalCode()))
                .map(request -> request.getPersonalCode()).collect(Collectors.toList());
    }

    private void processDeletedResponse(List<String> personalCodes) {
        try {
            log.info("Deleted Response size {} ", personalCodes.size());
            elinksPeopleDeleteService.deletePeople(personalCodes);
        } catch (Exception ex) {
            log.error("Error while processing delete response during updatePeople {} ", ex.getMessage(), ex);
        }
    }

    private void processLeaversResponse(List<LeaversResultsRequest> leaversRequests) {
        try {
            log.info("Leavers Request size {} ", leaversRequests.size());
            elinksPeopleLeaverService.processLeavers(leaversRequests);
        } catch (Exception ex) {
            log.error("Error while processing leavers response during updatePeople {} ", ex.getMessage(), ex);
        }
    }

    private void processPeopleResponse(PeopleRequest elinkPeopleResponseRequest, LocalDateTime schedulerStartTime,
                                       int pageValue) {
        try {
            // filter the profiles that do have email address for leavers
            elinkPeopleResponseRequest.getResultsRequests()
                .forEach(resultsRequest -> savePeopleDetails(resultsRequest,schedulerStartTime,pageValue));

        } catch (Exception ex) {
            auditStatus(schedulerStartTime, RefDataElinksConstants.JobStatus.FAILED.getStatus(), ex);
            throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, DATA_UPDATE_ERROR, DATA_UPDATE_ERROR);
        }

    }

    private void savePeopleDetails(
        ResultsRequest resultsRequest, LocalDateTime schedulerStartTime, int pageValue) {

        if (saveUserProfile(resultsRequest, schedulerStartTime, pageValue)) {
            try {
                elinksPeopleDeleteService.deleteAuth(resultsRequest);
                saveAppointmentDetails(resultsRequest.getPersonalCode(),
                        resultsRequest.getAppointmentsRequests(),
                        schedulerStartTime,
                        pageValue);
                saveAuthorizationDetails(resultsRequest.getPersonalCode(),
                        resultsRequest.getAuthorisationsRequests(),
                        schedulerStartTime,
                        pageValue);
                saveRoleDetails(resultsRequest.getPersonalCode(),
                        resultsRequest.getJudiciaryRoles(),
                        pageValue);
            } catch (Exception exception) {
                log.error("saveUserProfile exception {} ", exception.getMessage(), exception);
                log.warn("saveUserProfile is failed {} " + resultsRequest.getPersonalCode());
                partialSuccessFlag = true;
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                        now(), PERSONAL_CODE_KEY + resultsRequest.getPersonalCode(),
                        PERSONALCODE, exception.getMessage(), USER_PROFILE,
                        PERSONAL_CODE_KEY + resultsRequest.getPersonalCode(), pageValue, exception.getMessage());
            }

        }
    }

    private LeaversResultsRequest mapToLeaversRequest(ResultsRequest resultsRequest) {
        return LeaversResultsRequest.builder()
                .personalCode(resultsRequest.getPersonalCode())
                .objectId(resultsRequest.getObjectId())
                .perId(resultsRequest.getPerId())
                .leftOn(resultsRequest.getLeftOn())
                .leaver(resultsRequest.getLeaver())
                .build();
    }

    private boolean isLeaver(ResultsRequest resultsRequest) {
        String leaver = Objects.toString(resultsRequest.getLeaver(), "false");
        return "true".equalsIgnoreCase(leaver);
    }

    private boolean isDeleted(ResultsRequest resultsRequest) {
        String deleted = Objects.toString(resultsRequest.getDeleted(), "false");
        return "true".equalsIgnoreCase(deleted);
    }

    private void saveRoleDetails(String personalCode, List<RoleRequest> judiciaryRoles, int pageValue) {

        for (RoleRequest roleRequest: judiciaryRoles) {

            try {
                if (StringUtils.isBlank(roleRequest.getJudiciaryRoleNameId())) {
                    throw new InvalidNameException();
                }
                judicialRoleTypeRepository.save(JudicialRoleType.builder()
                        .title(roleRequest.getName())
                        .startDate(convertToLocalDateTime(START_DATE, roleRequest.getStartDate()))
                        .endDate(convertToLocalDateTime(END_DATE, roleRequest.getEndDate()))
                        .personalCode(personalCode)
                        .jurisdictionRoleId(roleRequest.getJudiciaryRoleId())
                        .jurisdictionRoleNameId(roleRequest.getJudiciaryRoleNameId())
                        .build());

            } catch (Exception e) {
                log.error("Save Role Details exception {}", e.getMessage(), e);
                log.warn("Judicial additional role  not loaded for " + personalCode);
                partialSuccessFlag = true;
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                        now(), JUDICIARY_ROLE_ID__KEY + roleRequest.getJudiciaryRoleId(),
                        INVALIDROLEID, INVALID_JUDICIARY_ROLE, JUDICIALROLETYPE, PERSONAL_CODE_KEY + personalCode,
                        pageValue, e.getMessage());
            }
        }

    }


    private boolean saveUserProfile(ResultsRequest resultsRequest,LocalDateTime schedulerStartTime, int pageValue) {

        if (validateUserProfile(resultsRequest, schedulerStartTime,pageValue)) {
            try {
                LocalDateTime createdOn = null;
                String sidamId = null;
                if (personalCodePresentInDb(resultsRequest).isEmpty()) {
                    createdOn = now();
                } else {
                    createdOn = personalCodePresentInDb(resultsRequest).get(0).getCreatedDate();
                    sidamId = personalCodePresentInDb(resultsRequest).get(0).getSidamId();
                }
                UserProfile userProfile = UserProfile.builder()
                    .personalCode(resultsRequest.getPersonalCode())
                    .knownAs(resultsRequest.getKnownAs())
                    .surname(resultsRequest.getSurname())
                    .fullName(resultsRequest.getFullName())
                    .postNominals(resultsRequest.getPostNominals())
                    .emailId(resultsRequest.getEmail())
                    .lastWorkingDate(convertToLocalDate(LAST_WORKING_DATE, resultsRequest.getLastWorkingDate()))
                    .activeFlag(true)
                    .sidamId(sidamId)
                    .createdDate(createdOn)
                    .objectId(resultsRequest.getObjectId())
                    .initials(resultsRequest.getInitials())
                    .title(resultsRequest.getTitle())
                    .lastLoadedDate(now())
                    .retirementDate(convertToLocalDate(RETIREMENT_DATE, resultsRequest.getRetirementDate()))
                    .build();
                userProfileCache.put(resultsRequest.getPersonalCode(),userProfile);
                profileRepository.save(userProfile);
                return true;
            }   catch (Exception e) {
                log.error("saveUserProfile exception {} ", e.getMessage(), e);
                log.warn("User Profile not loaded for {} " + resultsRequest.getPersonalCode());
                partialSuccessFlag = true;
                String personalCode = resultsRequest.getPersonalCode();
                String errorDescription = formatErrorDescription(
                    USERPROFILEFAILURE, resultsRequest.getPersonalCode());
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                        now(), PERSONAL_CODE_KEY + resultsRequest.getPersonalCode(),
                        PERSONALCODE, errorDescription, USER_PROFILE,
                        PERSONAL_CODE_KEY + personalCode, pageValue, e.getMessage());
                return false;
            }
        }
        return false;
    }

    private boolean validateUserProfile(ResultsRequest resultsRequest,LocalDateTime schedulerStartTime, int pageValue) {

        if (isLeaver(resultsRequest) || isDeleted(resultsRequest)) {
            return false;
        } else if (isNotValidKnownAs(resultsRequest)) {
            log.warn("KnownAs is empty or null for the personal code : " + resultsRequest.getPersonalCode());
            partialSuccessFlag = true;
            String errorField = resultsRequest.getPersonalCode();
            String errorDescription = formatErrorDescription(USER_PROFILE_KNOWN_AS, errorField);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    now(), PERSONAL_CODE_KEY + resultsRequest.getPersonalCode(),
                    KNOWN_AS, errorDescription, USER_PROFILE,
                    PERSONAL_CODE_KEY + resultsRequest.getPersonalCode(), pageValue);
            return false;
        } else if (isNotValidEmail(resultsRequest)) {
            log.warn("Email is empty or null for the personal code : " + resultsRequest.getPersonalCode());
            partialSuccessFlag = true;
            String errorField = resultsRequest.getPersonalCode();
            String errorDescription = formatErrorDescription(USERPROFILEEMAILID, errorField);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS, now(),
                    PERSONAL_CODE_KEY + resultsRequest.getPersonalCode(),
                    EMAILID, errorDescription, USER_PROFILE,
                    PERSONAL_CODE_KEY + resultsRequest.getPersonalCode(), pageValue);
            return false;
        } else if (!isNull(userProfileCache.get(resultsRequest.getPersonalCode()))) {
            log.warn("User Profile not loaded for " + resultsRequest.getPersonalCode());
            partialSuccessFlag = true;
            String errorDescription = formatErrorDescription(
                USERPROFILEISPRESENT, resultsRequest.getPersonalCode());
            String personalCode = resultsRequest.getPersonalCode();
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime, PERSONAL_CODE_KEY + resultsRequest.getPersonalCode(),
                    PERSONALCODE, errorDescription,
                    USER_PROFILE, PERSONAL_CODE_KEY + personalCode, pageValue);
            return false;
        } else if (!isNull(resultsRequest.getObjectId())
            && !resultsRequest.getObjectId().isEmpty() && !objectIdisPresent(resultsRequest).isEmpty()) {
            log.warn("Duplicate Object id " + resultsRequest.getPersonalCode());
            partialSuccessFlag = true;
            String personalCode = resultsRequest.getPersonalCode();
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime, OBJECT_ID_KEY + resultsRequest.getObjectId(),
                    OBJECT_ID, String.format(OBJECTIDISDUPLICATED, resultsRequest.getObjectId()),
                    USER_PROFILE, PERSONAL_CODE_KEY + personalCode, pageValue);
            return false;
        } else if (!isNull(resultsRequest.getObjectId())
            && !resultsRequest.getObjectId().isEmpty() && objectIdisPresentInDb(resultsRequest)) {
            log.warn("Duplicate Object id " + resultsRequest.getPersonalCode());
            partialSuccessFlag = true;
            String personalCode = resultsRequest.getPersonalCode();
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS, schedulerStartTime,
                    PERSONAL_CODE_KEY + resultsRequest.getPersonalCode(),
                    OBJECT_ID, OBJECTIDISPRESENT,
                    USER_PROFILE, PERSONAL_CODE_KEY + personalCode, pageValue);
            return false;
        }
        return true;
    }

    private boolean objectIdisPresentInDb(ResultsRequest resultsRequest) {
        return userProfilesSnapshot.stream()
            .anyMatch(userProfile -> resultsRequest.getObjectId().equals(userProfile.getObjectId())
                && !resultsRequest.getPersonalCode().equals(userProfile.getPersonalCode()));
    }

    private boolean isNotValidEmail(ResultsRequest resultsRequest) {
        boolean isLeaver = isLeaver(resultsRequest);
        boolean isDeleted = isDeleted(resultsRequest);
        return !isLeaver && !isDeleted && StringUtils.isEmpty(resultsRequest.getEmail());
    }

    private boolean isNotValidKnownAs(ResultsRequest resultsRequest) {
        boolean isLeaver = isLeaver(resultsRequest);
        boolean isDeleted = isDeleted(resultsRequest);
        return !isLeaver && !isDeleted && StringUtils.isEmpty(resultsRequest.getKnownAs());
    }

    private List<UserProfile> personalCodePresentInDb(ResultsRequest resultsRequest) {
        return userProfilesSnapshot.stream()
            .filter(userProfile -> resultsRequest.getPersonalCode()
                .equals(userProfile.getPersonalCode()))
            .toList();
    }

    private List<String> objectIdisPresent(ResultsRequest resultsRequest) {
        return userProfileCache.entrySet().stream().filter(userProfileEntry -> resultsRequest
            .getObjectId().equals(userProfileEntry.getValue().getObjectId()))
            .map(Map.Entry::getKey).toList();
    }


    private void saveAppointmentDetails(String personalCode,
                                        List<AppointmentsRequest> appointmentsRequests,
                                        LocalDateTime schedulerStartTime,
                                        int pageValue) {

        final List<AppointmentsRequest> validappointmentsRequests =
            validateAppointmentRequests(appointmentsRequests,personalCode,schedulerStartTime,pageValue);
        Appointment appointment;
        for (AppointmentsRequest appointmentsRequest: validappointmentsRequests) {
            String baseLocationId = fetchBaseLocationId(appointmentsRequest);
            try {
                appointment = Appointment.builder()
                    .baseLocationId(baseLocationId)
                    .regionId(fetchRegionId(appointmentsRequest.getLocation()))
                    .isPrincipleAppointment(appointmentsRequest.getIsPrincipleAppointment())
                    .startDate(convertToLocalDate(START_DATE, appointmentsRequest.getStartDate()))
                    .endDate(convertToLocalDate(END_DATE, appointmentsRequest.getEndDate()))
                    .personalCode(personalCode)
                    .epimmsId(locationMappingRepository.fetchEpimmsIdfromLocationId(baseLocationId))
                    .appointmentMapping(appointmentsRequest.getRoleName())
                    .appointmentType(appointmentsRequest
                        .getContractType())
                    .createdDate(now())
                    .lastLoadedDate(now())
                    .appointmentId(appointmentsRequest.getAppointmentId())
                    .roleNameId(appointmentsRequest.getRoleNameId())
                    .type(appointmentsRequest.getType())
                    .contractTypeId(appointmentsRequest
                        .getContractTypeId())
                    .location(appointmentsRequest.getLocation())
                    .joBaseLocationId(appointmentsRequest.getBaseLocationId())
                    .build();
                logAppointmentMapping(appointment);
                appointmentsRepository.save(appointment);
            } catch (Exception e) {
                log.error("Save Appointment exception {} ", e.getMessage());
                log.warn("failed to load appointment details for {} " + appointmentsRequest.getAppointmentId());
                partialSuccessFlag = true;
                String errorDescription = formatErrorDescription(LOCATIONIDFAILURE, baseLocationId);
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS, now(),
                        APPOINTMENT_ID__KEY + appointmentsRequest.getAppointmentId(),
                        BASE_LOCATION_ID, errorDescription,
                        APPOINTMENT_TABLE, PERSONAL_CODE_KEY + personalCode, pageValue);
            }
        }
    }

    private void logAppointmentMapping(Appointment appointment) {
        String appointmentMapping = appointment.getAppointmentMapping();
        if (appointmentMapping != null && appointmentMapping.length() > 64) {
            log.warn("Appointment Id: {} appointment Role name (appointment mapping)"
                            + " length is more than 64 characters",
                    appointment.getAppointmentId(),
                    appointment.getAppointmentMapping());
        }
    }

    private void saveAuthorizationDetails(String personalCode,
                                          List<AuthorisationsRequest> authorisationsRequests,
                                          LocalDateTime schedulerStartTime,
                                          int pageValue) {

        for (AuthorisationsRequest authorisationsRequest : authorisationsRequests) {
            try {
                authorisationsRepository
                    .save(uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation.builder()
                        .jurisdiction(authorisationsRequest.getJurisdiction())
                        .startDate(convertToLocalDate(START_DATE, authorisationsRequest.getStartDate()))
                        .endDate(convertToLocalDate(END_DATE, authorisationsRequest.getEndDate()))
                        .createdDate(LocalDateTime.now())
                        .lastUpdated(LocalDateTime.now())
                        .lowerLevel(authorisationsRequest.getTicket())
                        .personalCode(personalCode)
                        .ticketCode(authorisationsRequest.getTicketCode())
                        .ticketCode(authorisationsRequest.getTicketCode())
                        .appointmentId(authorisationsRequest.getAppointmentId())
                        .authorisationId(authorisationsRequest.getAuthorisationId())
                        .jurisdictionId(authorisationsRequest.getJurisdictionId())
                        .build());
            } catch (Exception e) {
                log.error("Save Authorisation exception {} ", e.getMessage(), e);
                log.warn("failed to load Authorisation details for {} " + authorisationsRequest.getAuthorisationId());
                partialSuccessFlag = true;
                String errorDescription;
                if (isNull(authorisationsRequest.getAppointmentId())) {
                    errorDescription = formatErrorDescription(APPOINTMENTID_IS_NULL,
                            authorisationsRequest.getAuthorisationId());
                } else {
                    errorDescription = formatErrorDescription(
                            APPOINTMENTIDNOTAVAILABLE, authorisationsRequest.getAppointmentId());
                }
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                        schedulerStartTime, AUTHORISATION_ID__KEY + authorisationsRequest.getAuthorisationId(),
                        APPOINTMENTID, errorDescription,
                        AUTHORISATION_TABLE, PERSONAL_CODE_KEY + personalCode, pageValue);
            }
        }
    }

    private List<AppointmentsRequest> validateAppointmentRequests(List<AppointmentsRequest> appointmentsRequests,
                                                                  String personalCode,
                                                                  LocalDateTime schedulerStartTime, int pageValue) {

        return appointmentsRequests.stream().filter(appointmentsRequest ->
            validAppointments(appointmentsRequest,personalCode,schedulerStartTime,pageValue)).toList();
    }

    private boolean validAppointments(AppointmentsRequest appointmentsRequest, String personalCode, LocalDateTime
        schedulerStartTime, int pageValue) {

        if (StringUtils.isEmpty(appointmentsRequest.getBaseLocationId()) || StringUtils
                .isEmpty(baseLocationRepository.fetchBaseLocationId(appointmentsRequest.getBaseLocationId()))) {
            log.warn("Mapped Base location not found in base table " + appointmentsRequest.getBaseLocationId());
            partialSuccessFlag = true;
            String baseLocationId = appointmentsRequest.getBaseLocationId();
            String errorDescription = formatErrorDescription(LOCATIONIDFAILURE, baseLocationId);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime, APPOINTMENT_ID__KEY + appointmentsRequest.getAppointmentId(),
                    BASE_LOCATION_ID, errorDescription, APPOINTMENT_TABLE,
                    PERSONAL_CODE_KEY + personalCode, pageValue);
            return false;
        } else if (StringUtils.isBlank(appointmentsRequest.getType())) {
            log.warn("The Type field is null for the given Appointment.");
            partialSuccessFlag = true;
            String errorDescription = TYPEIDFAILURE;
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime, APPOINTMENT_ID__KEY + appointmentsRequest.getAppointmentId(),
                    BASE_LOCATION_ID, errorDescription, APPOINTMENT_TABLE,
                    PERSONAL_CODE_KEY + personalCode, pageValue);
            return false;
        }  else if (StringUtils.isBlank(fetchBaseLocationId(appointmentsRequest))) {
            log.warn("Mapped parentId not found in locationType table " + appointmentsRequest.getBaseLocationId());
            partialSuccessFlag = true;
            String errorDescription = formatErrorDescription(PARENTIDFAILURE,
                    appointmentsRequest.getBaseLocationId());
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime, APPOINTMENT_ID__KEY + appointmentsRequest.getAppointmentId(),
                    BASE_LOCATION_ID, errorDescription + LOCATIONFAILURE,
                    APPOINTMENT_TABLE, PERSONAL_CODE_KEY + personalCode, pageValue);
            return false;
        } else if (StringUtils.isEmpty(fetchRegionId(appointmentsRequest.getLocation()))) {
            log.warn("Mapped  location not found in jrd lrd mapping table " + appointmentsRequest.getLocation());
            partialSuccessFlag = true;
            String location = appointmentsRequest.getLocation();
            String errorDescription = formatErrorDescription(CFTREGIONIDFAILURE, location);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime, APPOINTMENT_ID__KEY + appointmentsRequest.getAppointmentId(),
                    LOCATION, errorDescription, APPOINTMENT_TABLE,
                    PERSONAL_CODE_KEY + personalCode, pageValue);
            return false;
        } else if (INVALID_ROLES.contains(appointmentsRequest.getRoleName())) {
            log.warn("Role Name is Invalid " + appointmentsRequest.getRoleName());
            partialSuccessFlag = true;
            String errorDescription = formatErrorDescription(INVALIDROLENAMES,
                appointmentsRequest.getRoleName());
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    now(), APPOINTMENT_ID__KEY + appointmentsRequest.getAppointmentId(),
                    ROLENAME, errorDescription, APPOINTMENT_TABLE,
                    PERSONAL_CODE_KEY + personalCode, pageValue);
            return false;
        }
        return true;
    }

    private String fetchBaseLocationId(AppointmentsRequest appointment) {

        String baseLocationId = null;
        if ("Tribunals".equals(appointment.getType())) {
            baseLocationId = baseLocationRepository.fetchParentId(appointment.getBaseLocationId());
        } else if (!StringUtils.isEmpty(appointment.getType())) {
            baseLocationId = appointment.getBaseLocationId();
        }
        return baseLocationId;

    }

    private String fetchRegionId(String location) {
        location = Objects.toString(location, "").strip();
        if ("Unassigned".equals(location) || StringUtils.isEmpty(location) || "Unknown".equals(location)) {
            return "0";
        }
        return regionMappingRepository.fetchRegionIdfromRegion(location);
    }

    private String formatErrorDescription(String errorDescription, String wordToAppend) {
        return String.format(errorDescription, wordToAppend);
    }

    private void handleELinksErrorResponse(HttpStatus httpStatus) {

        int value = httpStatus.value();

        if (HttpStatus.BAD_REQUEST.value() == value) {
            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_BAD_REQUEST,
                    ELINKS_ERROR_RESPONSE_BAD_REQUEST);
        } else if (HttpStatus.UNAUTHORIZED.value() == value) {
            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_UNAUTHORIZED,
                    ELINKS_ERROR_RESPONSE_UNAUTHORIZED);
        } else if (HttpStatus.FORBIDDEN.value() == value) {
            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_FORBIDDEN,
                    ELINKS_ERROR_RESPONSE_FORBIDDEN);
        } else if (HttpStatus.NOT_FOUND.value() == value) {
            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_NOT_FOUND,
                    ELINKS_ERROR_RESPONSE_NOT_FOUND);
        } else if (HttpStatus.TOO_MANY_REQUESTS.value() == value) {
            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS,
                    ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS);
        } else {
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }
    }

}
