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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.ElinkEmailConfiguration;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.AppointmentsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.AuthorisationsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.PeopleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.ResultsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Location;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JrdRegionMappingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationMapppingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.IEmailService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.dto.Email;
import uk.gov.hmcts.reform.judicialapi.elinks.util.CommonUtil;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataExceptionHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.EmailTemplate;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENTID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENTIDFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENTIDNOTAVAILABLE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENTID_IS_NULL;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENT_TABLE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.AUTHORISATION_TABLE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.CFTREGIONIDFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.CONTENT_TYPE_HTML;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DATA_UPDATE_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DATE_PATTERN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.EMAILID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.INVALIDROLENAMES;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.INVALID_ROLES;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIALROLETYPE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONIDFAILURE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECTIDISDUPLICATED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECTIDISPRESENT;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PERSONALCODE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.REGION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.REGION_DEFAULT_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ROLENAME;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.THREAD_INVOCATION_EXCEPTION;
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
    ElinkEmailConfiguration emailConfiguration;

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
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonUtil commonUtil;

    private boolean partialSuccessFlag = false;

    private Map<String,UserProfile> userProfileCache = new HashMap<String,UserProfile>();
    private List<UserProfile> userProfilesSnapshot = new ArrayList<>();

    @Autowired
    ElinkDataExceptionRepository elinkDataExceptionRepository;

    @Value("${logging-component-name}")
    private String logComponentName;

    @Value("${elinks.people.lastUpdated}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String lastUpdated;

    @Value("${elinks.people.perPage}")
    private String perPage;

    @Value("${elinks.people.threadPauseTime}")
    private String threadPauseTime;

    @Value("${elinks.people.threadRetriggerPauseTime}")
    private String threadRetriggerPauseTime;

    @Value("${elinks.people.page}")
    private String page;

    @Value("${elinks.people.includePreviousAppointments}")
    private String includePreviousAppointments;

    @Autowired
    EmailTemplate emailTemplate;

    @Autowired
    IEmailService emailService;

    private final Map<String, String> emailConfigMapping = Map.of(LOCATION_ID, REGION,
            BASE_LOCATION_ID, BASE_LOCATION);

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
        do {
            Response peopleApiResponse = getPeopleResponseFromElinks(pageValue++, schedulerStartTime);
            httpStatus = HttpStatus.valueOf(peopleApiResponse.status());
            ResponseEntity<Object> responseEntity;

            if (httpStatus.is2xxSuccessful()) {
                responseEntity = JsonFeignResponseUtil.toResponseEntity(peopleApiResponse, PeopleRequest.class);
                PeopleRequest elinkPeopleResponseRequest = (PeopleRequest) responseEntity.getBody();
                if (Optional.ofNullable(elinkPeopleResponseRequest).isPresent()
                        && Optional.ofNullable(elinkPeopleResponseRequest.getPagination()).isPresent()
                        && Optional.ofNullable(elinkPeopleResponseRequest.getResultsRequests()).isPresent()) {
                    isMorePagesAvailable = elinkPeopleResponseRequest.getPagination().getMorePages();
                    processPeopleResponse(elinkPeopleResponseRequest, schedulerStartTime,pageValue);
                } else {
                    auditStatus(schedulerStartTime, RefDataElinksConstants.JobStatus.FAILED.getStatus());
                    throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
                }
            } else if (HttpStatus.TOO_MANY_REQUESTS.value() == httpStatus.value()) {
                log.info(":::: Too Many Requests ");
                pauseThread(Long.valueOf(threadRetriggerPauseTime),schedulerStartTime);
                --pageValue;
                continue;
            } else {
                auditStatus(schedulerStartTime, RefDataElinksConstants.JobStatus.FAILED.getStatus());
                handleELinksErrorResponse(httpStatus);
            }
            pauseThread(Long.valueOf(threadPauseTime),schedulerStartTime);
        } while (isMorePagesAvailable);

        sendEmail(schedulerStartTime);

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

    private void sendEmail(LocalDateTime schedulerStartTime) {
        List<ElinkDataExceptionRecords> list = elinkDataExceptionRepository
                .findBySchedulerStartTime(schedulerStartTime);

        Map<String, List<ElinkDataExceptionRecords>> map = list
                .stream()
                .collect(Collectors.groupingBy(ElinkDataExceptionRecords::getFieldInError));

        if (map.containsKey(BASE_LOCATION_ID)) {
            sendEmail(new HashSet<>(map.get(BASE_LOCATION_ID)), "baselocation",
                    LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        }
        if (map.containsKey(LOCATION)) {
            sendEmail(new HashSet<>(map.get(LOCATION)), "location",
                    LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        }
        if (map.containsKey(APPOINTMENTID)) {
            sendEmail(new HashSet<>(map.get(APPOINTMENTID)), "appointment",
                    LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        }
        if (map.containsKey(USER_PROFILE)) {
            sendEmail(new HashSet<>(map.get(USER_PROFILE)), "userprofile",
                LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        }
    }

    public int sendEmail(Set<ElinkDataExceptionRecords> data, String type, Object... params) {
        log.info("{} : send Email",logComponentName);
        ElinkEmailConfiguration.MailTypeConfig config = emailConfiguration.getMailTypes()
                .get(type);
        if (config != null && config.isEnabled()) {
            Email email = Email.builder()
                    .contentType(CONTENT_TYPE_HTML)
                    .from(config.getFrom())
                    .to(config.getTo())
                    .subject(String.format(config.getSubject(), params))
                    .messageBody(emailTemplate.getEmailBody(config.getTemplate(), Map.of("resultsRequest", data)))
                    .build();
            return emailService.sendEmail(email);
        }
        return -1;
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
        log.info("updatedSince : " + updatedSince);
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
                saveRoleDetails(resultsRequest.getPersonalCode(), resultsRequest.getJudiciaryRoles());
            } catch (Exception exception) {
                log.warn("saveUserProfile is failed  " + resultsRequest.getPersonalCode());
                partialSuccessFlag = true;
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    now(),
                    resultsRequest.getPersonalCode(),
                    PERSONALCODE, exception.getMessage(), USER_PROFILE,resultsRequest.getPersonalCode());
            }

        }
    }

    private void saveRoleDetails(String personalCode, List<RoleRequest> judiciaryRoles) {

        for (RoleRequest roleRequest: judiciaryRoles) {

            try {
                judicialRoleTypeRepository.save(JudicialRoleType.builder()
                    .title(roleRequest.getName())
                    .startDate(convertToLocalDateTime(roleRequest.getStartDate()))
                    .endDate(convertToLocalDateTime(roleRequest.getEndDate()))
                    .personalCode(personalCode)
                    .jurisdictionRoleId(roleRequest.getJudiciaryRoleId())
                    .build());
            } catch (Exception e) {
                log.warn("Role type  not loaded for " + personalCode);
                partialSuccessFlag = true;
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    now(),
                    personalCode,
                    JUDICIALROLETYPE, e.getMessage(), JUDICIALROLETYPE,personalCode);
            }
        }

    }


    private boolean saveUserProfile(ResultsRequest resultsRequest,LocalDateTime schedulerStartTime, int pageValue) {

        if (validateUserProfile(resultsRequest, schedulerStartTime,pageValue)) {
            try {
                UserProfile userProfile = UserProfile.builder()
                    .personalCode(resultsRequest.getPersonalCode())
                    .knownAs(resultsRequest.getKnownAs())
                    .surname(resultsRequest.getSurname())
                    .fullName(resultsRequest.getFullName())
                    .postNominals(resultsRequest.getPostNominals())
                    .ejudiciaryEmailId(resultsRequest.getEmail())
                    .lastWorkingDate(convertToLocalDate(resultsRequest.getLastWorkingDate()))
                    .activeFlag(true)
                    .createdDate(now())
                    .lastLoadedDate(now())
                    .objectId(resultsRequest.getObjectId())
                    .initials(resultsRequest.getInitials())
                    .title(resultsRequest.getTitle())
                    .retirementDate(convertToLocalDate(resultsRequest.getRetirementDate()))
                    .build();
                userProfileCache.put(resultsRequest.getPersonalCode(),userProfile);
                profileRepository.save(userProfile);
                return true;
            }   catch (Exception e) {
                log.warn("User Profile not loaded for " + resultsRequest.getPersonalCode());
                partialSuccessFlag = true;
                String personalCode = resultsRequest.getPersonalCode();
                String errorDescription = appendFieldWithErrorDescription(
                    USERPROFILEFAILURE, resultsRequest.getPersonalCode(),pageValue);
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    now(),
                    resultsRequest.getPersonalCode(),
                    USER_PROFILE, errorDescription, USER_PROFILE,personalCode);
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
            String errorDescription = appendFieldWithErrorDescription(USERPROFILEEMAILID, errorField, pageValue);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                now(),
                resultsRequest.getPersonalCode(),
                EMAILID, errorDescription, USER_PROFILE,resultsRequest.getPersonalCode());
            return false;
        } else if (!isNull(userProfileCache.get(resultsRequest.getPersonalCode()))) {
            log.warn("User Profile not loaded for " + resultsRequest.getPersonalCode());
            partialSuccessFlag = true;
            String errorDescription = appendFieldWithErrorDescription(
                USERPROFILEISPRESENT, resultsRequest.getPersonalCode(), pageValue);
            String personalCode = resultsRequest.getPersonalCode();
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                resultsRequest.getPersonalCode(),
                USER_PROFILE,errorDescription, USER_PROFILE,personalCode);
            return false;
        } else if (!isNull(resultsRequest.getObjectId())
            && !resultsRequest.getObjectId().isEmpty() && !objectIdisPresent(resultsRequest).isEmpty()) {
            log.warn("Duplicate Object id " + resultsRequest.getPersonalCode());
            partialSuccessFlag = true;
            String personalCode = resultsRequest.getPersonalCode();
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                resultsRequest.getObjectId(),
                USER_PROFILE,OBJECTIDISDUPLICATED, USER_PROFILE,personalCode);
            return false;
        } else if (!isNull(resultsRequest.getObjectId())
            && !resultsRequest.getObjectId().isEmpty() && objectIdisPresentInDb(resultsRequest)) {
            log.warn("Duplicate Object id " + resultsRequest.getPersonalCode());
            partialSuccessFlag = true;
            String personalCode = resultsRequest.getPersonalCode();
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                resultsRequest.getPersonalCode(),
                USER_PROFILE,OBJECTIDISPRESENT, USER_PROFILE,personalCode);
            return false;
        }
        return true;
    }

    private boolean objectIdisPresentInDb(ResultsRequest resultsRequest) {
        return userProfilesSnapshot.stream()
            .anyMatch(userProfile -> resultsRequest.getObjectId().equals(userProfile.getObjectId())
                && !resultsRequest.getPersonalCode().equals(userProfile.getPersonalCode()));
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
                log.warn("failed to load appointment details for " + appointmentsRequest.getAppointmentId());
                partialSuccessFlag = true;
                String errorDescription = appendFieldWithErrorDescription(
                    APPOINTMENTIDFAILURE, appointmentsRequest.getAppointmentId(), pageValue);
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    now(),
                    appointmentsRequest.getAppointmentId(),
                    APPOINTMENT_TABLE, errorDescription, APPOINTMENT_TABLE,personalCode);
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
                log.warn("failed to load Authorisation details for " + authorisationsRequest.getAuthorisationId());
                partialSuccessFlag = true;
                String errorDescription;
                if (isNull(authorisationsRequest.getAppointmentId())) {
                    errorDescription = APPOINTMENTID_IS_NULL;
                } else {
                    errorDescription = appendFieldWithErrorDescription(
                            APPOINTMENTIDNOTAVAILABLE, authorisationsRequest.getAppointmentId(), pageValue);
                }
                elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                        schedulerStartTime,
                    authorisationsRequest.getAuthorisationId(),
                    APPOINTMENTID, errorDescription, AUTHORISATION_TABLE,personalCode);
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

        if (StringUtils.isEmpty(appointmentsRequest.getBaseLocationId())
            || StringUtils.isEmpty(fetchBaseLocationId(appointmentsRequest))) {
            log.warn("Mapped Base location not found in base table " + appointmentsRequest.getBaseLocationId());
            partialSuccessFlag = true;
            String baseLocationId = appointmentsRequest.getBaseLocationId();
            String errorDescription = appendFieldWithErrorDescription(LOCATIONIDFAILURE, baseLocationId, pageValue);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime,
                appointmentsRequest.getAppointmentId(),
                BASE_LOCATION_ID, errorDescription, APPOINTMENT_TABLE,personalCode);
            return false;
        } else if (StringUtils.isEmpty(fetchRegionId(appointmentsRequest.getLocation()))) {
            log.warn("Mapped  location not found in jrd lrd mapping table " + appointmentsRequest.getLocation());
            partialSuccessFlag = true;
            String location = appointmentsRequest.getLocation();
            String errorDescription = appendFieldWithErrorDescription(CFTREGIONIDFAILURE, location, pageValue);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                    schedulerStartTime,
                appointmentsRequest.getAppointmentId(),
                LOCATION, errorDescription, APPOINTMENT_TABLE,personalCode);
            return false;
        } else if (INVALID_ROLES.contains(appointmentsRequest.getRoleName())) {
            log.warn("Role Name is Invalid " + appointmentsRequest.getRoleName());
            partialSuccessFlag = true;
            String errorDescription = appendFieldWithErrorDescription(INVALIDROLENAMES,
                appointmentsRequest.getRoleName(), pageValue);
            elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                now(),
                appointmentsRequest.getAppointmentId(),
                ROLENAME, errorDescription, APPOINTMENT_TABLE,personalCode);
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
    private String appendFieldWithErrorDescription(String errorDescription, String wordToAppend, int pageValue) {

        String wordAfterWhichAppend = ":";
        String wordPageNumber = "PageNumber:";
        return wordPageNumber.concat(String.valueOf(pageValue)).concat("-").concat(errorDescription.substring(0,
                errorDescription.indexOf(wordAfterWhichAppend)
                + wordAfterWhichAppend.length())
                + " " + wordToAppend + " "
                + errorDescription.substring(errorDescription.indexOf(wordAfterWhichAppend)
                + wordAfterWhichAppend.length(), errorDescription.length()));
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

    // Need to be removed after Region ID  CR fix
    private String regionMapping(AppointmentsRequest appointment) {

        String region = appointment.getCircuit() != null ? appointment.getCircuit() : appointment.getLocation();
        Location location = locationRepository.findByRegionDescEnIgnoreCase(region);

        return location != null ? location.getRegionId() : REGION_DEFAULT_ID;
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
