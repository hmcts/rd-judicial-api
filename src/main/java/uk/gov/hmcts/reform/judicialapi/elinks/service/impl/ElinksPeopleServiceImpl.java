package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleService;
import uk.gov.hmcts.reform.judicialapi.elinks.util.CommonUtil;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataExceptionHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksResponsesHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.elinks.util.SendEmail;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.naming.InvalidNameException;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENTID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENTIDNOTAVAILABLE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENTID_IS_NULL;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENT_TABLE;
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
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.INVALIDROLEID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.INVALIDROLENAMES;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.INVALIDROLETYPE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.INVALID_ROLES;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIALROLETYPE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONIDFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECTIDISDUPLICATED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECTIDISPRESENT;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECT_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PARENTIDFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PERSONALCODE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ROLENAME;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.THREAD_INVOCATION_EXCEPTION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.TYPEIDFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USERPROFILEEMAILID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USERPROFILEFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USERPROFILEISPRESENT;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USER_PROFILE;

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

    @Autowired ElinksPeopleDeleteServiceimpl elinksPeopleDeleteServiceimpl;

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
        } catch (Exception ex) {
            log.error("People service exception", ex);
            auditStatus(schedulerStartTime, RefDataElinksConstants.JobStatus.FAILED.getStatus());
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

    private Response getPeopleResponseFromElinks(int currentPage, LocalDateTime schedulerStartTime) {
        String updatedSince = getUpdateSince();
        try {
            return elinksFeignClient.getPeopleDetails(updatedSince, perPage, String.valueOf(currentPage),
                    Boolean.parseBoolean(includePreviousAppointments));
        } catch (FeignException ex) {
            auditStatus(schedulerStartTime, RefDataElinksConstants.JobStatus.FAILED.getStatus());
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }
    }

    private void pauseThread(Long thredPauseTime, LocalDateTime schedulerStartTime) {
        try {
            Thread.sleep(thredPauseTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            auditStatus(schedulerStartTime, RefDataElinksConstants.JobStatus.FAILED.getStatus());
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

    private void processPeopleResponse(PeopleRequest elinkPeopleResponseRequest, LocalDateTime schedulerStartTime,
                                       int pageValue) {
        try {
            // filter the profiles that do have email address for leavers
            elinkPeopleResponseRequest.getResultsRequests()
                .forEach(resultsRequest -> savePeopleDetails(resultsRequest,schedulerStartTime,pageValue));

        } catch (Exception ex) {
            auditStatus(schedulerStartTime, RefDataElinksConstants.JobStatus.FAILED.getStatus());
            throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, DATA_UPDATE_ERROR, DATA_UPDATE_ERROR);
        }

    }

    private void savePeopleDetails(
        ResultsRequest resultsRequest, LocalDateTime schedulerStartTime, int pageValue) {

        if (saveUserProfile(resultsRequest,schedulerStartTime,pageValue)) {
            try {
                elinksPeopleDeleteServiceimpl.deleteAuth(resultsRequest);
                saveAppointmentDetails(resultsRequest.getPersonalCode(), resultsRequest
                    .getObjectId(), resultsRequest.getAppointmentsRequests(),schedulerStartTime,pageValue);
                saveAuthorizationDetails(resultsRequest.getPersonalCode(), resultsRequest
                    .getObjectId(), resultsRequest.getAuthorisationsRequests(),schedulerStartTime,pageValue);
                saveRoleDetails(resultsRequest.getPersonalCode(), resultsRequest.getJudiciaryRoles(),pageValue);
            } catch (Exception exception) {
                log.error("saveUserProfile exception", exception.getMessage());
                log.warn("saveUserProfile is failed  " + resultsRequest.getPersonalCode());
                partialSuccessFlag = true;
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    now(),
                    resultsRequest.getPersonalCode(),
                    PERSONALCODE, exception.getMessage(), USER_PROFILE,resultsRequest.getPersonalCode(),pageValue);
            }

        }
    }

    private void saveRoleDetails(String personalCode, List<RoleRequest> judiciaryRoles, int pageValue) {

        for (RoleRequest roleRequest: judiciaryRoles) {

            try {
                if (StringUtils.isBlank(roleRequest.getJudiciaryRoleNameId())) {
                    throw new InvalidNameException();
                }
                judicialRoleTypeRepository.save(JudicialRoleType.builder()
                    .title(roleRequest.getName())
                    .startDate(convertToLocalDateTime(roleRequest.getStartDate()))
                    .endDate(convertToLocalDateTime(roleRequest.getEndDate()))
                    .personalCode(personalCode)
                    .jurisdictionRoleId(roleRequest.getJudiciaryRoleId())
                    .jurisdictionRoleNameId(roleRequest.getJudiciaryRoleNameId())
                    .build());

            } catch (Exception e) {
                log.error("Save Role Details exception", e.getMessage());
                log.warn("Judicial additional role  not loaded for " + personalCode);
                partialSuccessFlag = true;
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    now(),
                    roleRequest.getJudiciaryRoleId(),
                    INVALIDROLEID, INVALIDROLETYPE, JUDICIALROLETYPE,personalCode,pageValue);
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
                    .lastWorkingDate(convertToLocalDate(resultsRequest.getLastWorkingDate()))
                    .activeFlag(true)
                    .sidamId(sidamId)
                    .createdDate(createdOn)
                    .objectId(resultsRequest.getObjectId())
                    .initials(resultsRequest.getInitials())
                    .title(resultsRequest.getTitle())
                    .lastLoadedDate(now())
                    .retirementDate(convertToLocalDate(resultsRequest.getRetirementDate()))
                    .build();
                userProfileCache.put(resultsRequest.getPersonalCode(),userProfile);
                profileRepository.save(userProfile);
                return true;
            }   catch (Exception e) {
                log.error("saveUserProfile exception", e.getMessage());
                log.warn("User Profile not loaded for " + resultsRequest.getPersonalCode());
                partialSuccessFlag = true;
                String personalCode = resultsRequest.getPersonalCode();
                String errorDescription = appendFieldWithErrorDescription(
                    USERPROFILEFAILURE, resultsRequest.getPersonalCode());
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    now(),
                    resultsRequest.getPersonalCode(),
                    PERSONALCODE, errorDescription, USER_PROFILE,personalCode,pageValue);
                return false;
            }
        }
        return false;
    }

    private boolean validateUserProfile(ResultsRequest resultsRequest,LocalDateTime schedulerStartTime, int pageValue) {

        if (StringUtils.isEmpty(resultsRequest.getEmail())) {
            log.warn("Mapped Base location not found in base table " + resultsRequest.getPersonalCode());
            partialSuccessFlag = true;
            String errorField = resultsRequest.getPersonalCode();
            String errorDescription = appendFieldWithErrorDescription(USERPROFILEEMAILID, errorField);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                now(),
                resultsRequest.getPersonalCode(),
                EMAILID, errorDescription, USER_PROFILE,resultsRequest.getPersonalCode(),pageValue);
            return false;
        } else if (!isNull(userProfileCache.get(resultsRequest.getPersonalCode()))) {
            log.warn("User Profile not loaded for " + resultsRequest.getPersonalCode());
            partialSuccessFlag = true;
            String errorDescription = appendFieldWithErrorDescription(
                USERPROFILEISPRESENT, resultsRequest.getPersonalCode());
            String personalCode = resultsRequest.getPersonalCode();
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                resultsRequest.getPersonalCode(),
                PERSONALCODE,errorDescription, USER_PROFILE,personalCode,pageValue);
            return false;
        } else if (!isNull(resultsRequest.getObjectId())
            && !resultsRequest.getObjectId().isEmpty() && !objectIdisPresent(resultsRequest).isEmpty()) {
            log.warn("Duplicate Object id " + resultsRequest.getPersonalCode());
            partialSuccessFlag = true;
            String personalCode = resultsRequest.getPersonalCode();
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                resultsRequest.getObjectId(),
                OBJECT_ID,OBJECTIDISDUPLICATED, USER_PROFILE,personalCode,pageValue);
            return false;
        } else if (!isNull(resultsRequest.getObjectId())
            && !resultsRequest.getObjectId().isEmpty() && objectIdisPresentInDb(resultsRequest)) {
            log.warn("Duplicate Object id " + resultsRequest.getPersonalCode());
            partialSuccessFlag = true;
            String personalCode = resultsRequest.getPersonalCode();
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                resultsRequest.getPersonalCode(),
                    OBJECT_ID,OBJECTIDISPRESENT, USER_PROFILE,personalCode,pageValue);
            return false;
        }
        return true;
    }

    private boolean objectIdisPresentInDb(ResultsRequest resultsRequest) {
        return userProfilesSnapshot.stream()
            .anyMatch(userProfile -> resultsRequest.getObjectId().equals(userProfile.getObjectId())
                && !resultsRequest.getPersonalCode().equals(userProfile.getPersonalCode()));
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


    private void saveAppointmentDetails(String personalCode, String objectId,
                                        List<AppointmentsRequest> appointmentsRequests,
                                        LocalDateTime schedulerStartTime, int pageValue)
            throws JsonProcessingException {

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
                    .startDate(convertToLocalDate(appointmentsRequest.getStartDate()))
                    .endDate(convertToLocalDate(appointmentsRequest.getEndDate()))
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
                appointmentsRepository.save(appointment);
            } catch (Exception e) {
                log.error("Save Appointment exception", e.getMessage());
                log.warn("failed to load appointment details for " + appointmentsRequest.getAppointmentId());
                partialSuccessFlag = true;
                String errorDescription = appendFieldWithErrorDescription(LOCATIONIDFAILURE, baseLocationId);
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    now(),
                    appointmentsRequest.getAppointmentId(),
                    BASE_LOCATION_ID, errorDescription, APPOINTMENT_TABLE,personalCode,pageValue);
            }
        }
    }

    private void saveAuthorizationDetails(String personalCode, String objectId,
                                          List<AuthorisationsRequest> authorisationsRequests,
                                          LocalDateTime schedulerStartTime,int pageValue) {

        for (AuthorisationsRequest authorisationsRequest : authorisationsRequests) {
            try {
                authorisationsRepository
                    .save(uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation.builder()
                        .jurisdiction(authorisationsRequest.getJurisdiction())
                        .startDate(convertToLocalDate(authorisationsRequest.getStartDate()))
                        .endDate(convertToLocalDate(authorisationsRequest.getEndDate()))
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
                log.error("Save Authorisation exception", e.getMessage());
                log.warn("failed to load Authorisation details for " + authorisationsRequest.getAuthorisationId());
                partialSuccessFlag = true;
                String errorDescription;
                if (isNull(authorisationsRequest.getAppointmentId())) {
                    errorDescription = APPOINTMENTID_IS_NULL;
                } else {
                    errorDescription = appendFieldWithErrorDescription(
                            APPOINTMENTIDNOTAVAILABLE, authorisationsRequest.getAppointmentId());
                }
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                        schedulerStartTime,
                    authorisationsRequest.getAuthorisationId(),
                    APPOINTMENTID, errorDescription, AUTHORISATION_TABLE,personalCode,pageValue);
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
            String errorDescription = appendFieldWithErrorDescription(LOCATIONIDFAILURE, baseLocationId);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime,
                appointmentsRequest.getAppointmentId(),
                BASE_LOCATION_ID, errorDescription, APPOINTMENT_TABLE,personalCode,pageValue);
            return false;
        } else if (StringUtils.isBlank(appointmentsRequest.getType())) {
            log.warn("The Type field is null for the given Appointment.");
            partialSuccessFlag = true;
            String errorDescription = TYPEIDFAILURE;
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime,
                    appointmentsRequest.getAppointmentId(),
                    BASE_LOCATION_ID, errorDescription, APPOINTMENT_TABLE,personalCode,pageValue);
            return false;
        }  else if (StringUtils.isBlank(fetchBaseLocationId(appointmentsRequest))) {
            log.warn("Mapped parentId not found in locationType table " + appointmentsRequest.getBaseLocationId());
            partialSuccessFlag = true;
            String errorDescription = appendFieldWithErrorDescription(appointmentsRequest.getBaseLocationId(),
                    PARENTIDFAILURE);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime,
                    appointmentsRequest.getAppointmentId(),
                    BASE_LOCATION_ID, errorDescription + LOCATIONFAILURE, APPOINTMENT_TABLE,personalCode,pageValue);
            return false;
        } else if (StringUtils.isEmpty(fetchRegionId(appointmentsRequest.getLocation()))) {
            log.warn("Mapped  location not found in jrd lrd mapping table " + appointmentsRequest.getLocation());
            partialSuccessFlag = true;
            String location = appointmentsRequest.getLocation();
            String errorDescription = appendFieldWithErrorDescription(CFTREGIONIDFAILURE, location);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime,
                appointmentsRequest.getAppointmentId(),
                LOCATION, errorDescription, APPOINTMENT_TABLE,personalCode,pageValue);
            return false;
        } else if (INVALID_ROLES.contains(appointmentsRequest.getRoleName())) {
            log.warn("Role Name is Invalid " + appointmentsRequest.getRoleName());
            partialSuccessFlag = true;
            String errorDescription = appendFieldWithErrorDescription(INVALIDROLENAMES,
                appointmentsRequest.getRoleName());
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                now(),
                appointmentsRequest.getAppointmentId(),
                ROLENAME, errorDescription, APPOINTMENT_TABLE,personalCode,pageValue);
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

        String regionId = null;
        if ("Unassigned".equals(location) || StringUtils.isEmpty(location) || "Unknown".equals(location)) {
            regionId = "0";
        } else {
            regionId = regionMappingRepository.fetchRegionIdfromRegion(location);
        }
        return regionId;

    }

    // Append the string to add error description for the given format
    private String appendFieldWithErrorDescription(String errorDescription, String wordToAppend) {

        String wordAfterWhichAppend = ":";
        return errorDescription.substring(0,
                errorDescription.indexOf(wordAfterWhichAppend)
                + wordAfterWhichAppend.length())
                + " " + wordToAppend + " "
                + errorDescription.substring(errorDescription.indexOf(wordAfterWhichAppend)
                + wordAfterWhichAppend.length(), errorDescription.length());
    }



    private LocalDate convertToLocalDate(String date) {
        if (Optional.ofNullable(date).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
        }
        return null;
    }

    private LocalDateTime convertToLocalDateTime(String date) {
        if (Optional.ofNullable(date).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            return LocalDateTime.parse(date, formatter);
        }
        return null;
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
