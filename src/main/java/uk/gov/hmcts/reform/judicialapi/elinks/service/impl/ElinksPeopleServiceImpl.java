package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.AppointmentsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.AuthorisationsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.PeopleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.ResultsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Location;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.LocationMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationMapppingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleService;
import uk.gov.hmcts.reform.judicialapi.elinks.util.CommonUtil;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.AUDIT_DATA_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DATA_UPDATE_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.REGION_DEFAULT_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.THREAD_INVOCATION_EXCEPTION;

@Slf4j
@Service
public class ElinksPeopleServiceImpl implements ElinksPeopleService {

    public static final String EPIMMS_ID = "epimmsId";
    public static final String SERVICE_CODE = "serviceCode";
    @Autowired
    private ElinksFeignClient elinksFeignClient;

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    @Autowired
    private AuthorisationsRepository authorisationsRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private LocationMapppingRepository locationMappingRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private DataloadSchedularAuditRepository dataloadSchedularAuditRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonUtil commonUtil;

    @Value("${elinks.people.lastUpdated}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String lastUpdated;

    @Value("${elinks.people.perPage}")
    private String perPage;

    @Value("${elinks.people.threadPauseTime}")
    private String threadPauseTime;

    @Value("${elinks.people.page}")
    private String page;

    @Value("${elinks.people.includePreviousAppointments}")
    private String includePreviousAppointments;

    @Override
    @Transactional("transactionManager")
    public ResponseEntity<ElinkPeopleWrapperResponse> updatePeople() {
        boolean isMorePagesAvailable = true;
        HttpStatus httpStatus = null;

        int pageValue = Integer.parseInt(page);
        do {
            Response peopleApiResponse = getPeopleResponseFromElinks(pageValue++);
            httpStatus = HttpStatus.valueOf(peopleApiResponse.status());
            ResponseEntity<Object> responseEntity;

            if (httpStatus.is2xxSuccessful()) {
                responseEntity = JsonFeignResponseUtil.toResponseEntity(peopleApiResponse, PeopleRequest.class);
                PeopleRequest elinkPeopleResponseRequest = (PeopleRequest) responseEntity.getBody();
                if (Optional.ofNullable(elinkPeopleResponseRequest).isPresent()
                        && Optional.ofNullable(elinkPeopleResponseRequest.getPagination()).isPresent()
                        && Optional.ofNullable(elinkPeopleResponseRequest.getResultsRequests()).isPresent()) {
                    isMorePagesAvailable = elinkPeopleResponseRequest.getPagination().getMorePages();
                    processPeopleResponse(elinkPeopleResponseRequest);
                } else {
                    throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
                }
            } else {
                handleELinksErrorResponse(httpStatus);
            }
            pauseThread(Long.valueOf(threadPauseTime));
        } while (isMorePagesAvailable);

        updateEpimsServiceCodeMapping();

        ElinkPeopleWrapperResponse response = new ElinkPeopleWrapperResponse();
        response.setMessage(PEOPLE_DATA_LOAD_SUCCESS);

        return ResponseEntity
                .status(httpStatus)
                .body(response);
    }

    private Response getPeopleResponseFromElinks(int currentPage) {
        String updatedSince = getUpdateSince();
        try {
            return elinksFeignClient.getPeopleDetials(updatedSince, perPage, String.valueOf(currentPage),
                    Boolean.parseBoolean(includePreviousAppointments));
        } catch (FeignException ex) {
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }
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

    private String getUpdateSince() {
        String updatedSince;
        LocalDateTime maxSchedulerEndTime;
        try {
            maxSchedulerEndTime = dataloadSchedularAuditRepository.findLatestSchedularEndTime();
        } catch (Exception ex) {
            throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, AUDIT_DATA_ERROR, AUDIT_DATA_ERROR);
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


    private void processPeopleResponse(PeopleRequest elinkPeopleResponseRequest) {
        try {
            // filter the profiles that do have email address for leavers
            List<ResultsRequest> resultsRequests = elinkPeopleResponseRequest.getResultsRequests()
                    .stream()
                    .filter(resultsRequest -> nonNull(resultsRequest.getEmail()))
                    .toList();

            List<UserProfile> userProfiles = resultsRequests.stream()
                    .map(this::buildUserProfileDto)
                    .toList();

            profileRepository.saveAll(userProfiles);

            // Delete the personalCodes in appointment table
            List<String>  personalCodesToDelete = userProfiles.stream().map(UserProfile::getPersonalCode).toList();

            appointmentsRepository.deleteByPersonalCodeIn(personalCodesToDelete);
            List<uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment> appointments =  resultsRequests.stream()
                    .filter(resultsRequest -> !CollectionUtils.isEmpty(resultsRequest.getAppointmentsRequests()))
                    .map(this::buildAppointmentDto)
                    .flatMap(Collection::stream)
                    .toList();

            if (!CollectionUtils.isEmpty(appointments)) {
                appointmentsRepository.saveAll(appointments);
            }
            authorisationsRepository.deleteByPersonalCodeIn(personalCodesToDelete);

            List<uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation> authorisations =
                     resultsRequests.stream()
                     .filter(resultsRequest -> !CollectionUtils.isEmpty(resultsRequest.getAppointmentsRequests()))
                     .map(this::buildAuthorisationsDto)
                     .flatMap(Collection::stream)
                     .toList();

            if (!CollectionUtils.isEmpty(authorisations)) {
                authorisationsRepository.saveAll(authorisations);
            }
        } catch (Exception ex) {
            throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, DATA_UPDATE_ERROR, DATA_UPDATE_ERROR);
        }

    }

    private List<uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation> buildAuthorisationsDto(ResultsRequest
                                                                                                       resultsRequest) {
        final List<AuthorisationsRequest> authorisationsRequests = resultsRequest.getAuthorisationsRequests();
        final List<Authorisation> authorisationList = new ArrayList<>();

        for (AuthorisationsRequest authorisationsRequest : authorisationsRequests) {
            authorisationList.add(uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation.builder()
                    .personalCode(resultsRequest.getPersonalCode())
                    .objectId(resultsRequest.getObjectId())
                    .jurisdiction(authorisationsRequest.getJurisdiction())
                    .startDate(convertToLocalDateTime(authorisationsRequest.getStartDate()))
                    .endDate(convertToLocalDateTime(authorisationsRequest.getEndDate()))
                    .createdDate(LocalDateTime.now())
                    .lastUpdated(LocalDateTime.now())
                    .lowerLevel(authorisationsRequest.getLowerLevel())
                    .ticketCode(authorisationsRequest.getTicketCode())
                    .build());
        }
        return authorisationList;
    }


    private List<uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment>
        buildAppointmentDto(ResultsRequest resultsRequest) {

        final List<AppointmentsRequest> appointmentsRequests = resultsRequest.getAppointmentsRequests();
        final List<Appointment> appointmentList = new ArrayList<>();

        for (AppointmentsRequest appointment: appointmentsRequests) {

            appointmentList.add(uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment.builder()
                .personalCode(resultsRequest.getPersonalCode())
                .objectId(resultsRequest.getObjectId())
                .baseLocationId(appointment.getBaseLocationId())
                .regionId(regionMapping(appointment))
                .isPrincipleAppointment(appointment.getIsPrincipleAppointment())
                .startDate(convertToLocalDate(appointment.getStartDate()))
                .endDate(convertToLocalDate(appointment.getEndDate()))
                .createdDate(LocalDateTime.now())
                .lastLoadedDate(LocalDateTime.now())
                .appointmentRolesMapping(appointment.getAppointmentRolesMapping())
                .appointmentType(appointment.getAppointmentType())
                .workPattern(appointment.getWorkPattern())
                .build());
        }
        return appointmentList;
    }

    // moved the logic to outside
    public void updateEpimsServiceCodeMapping() {

        List<Triple<String, String,String>> epimsLocationId = new ArrayList<>();
        List<String> locationIds = appointmentsRepository.fetchAppointmentBaseLocation();
        List<LocationMapping> locationMappings  = locationMappingRepository.findAllById(locationIds);

        String updateEpimmsid = "UPDATE dbjudicialdata.judicial_office_appointment SET epimms_id = ? , "
                    + " service_code = ? WHERE base_location_id = ?";
        locationMappings.stream().filter(location -> nonNull(location.getJudicialBaseLocationId())).forEach(s ->
                epimsLocationId.add(Triple.of(s.getJudicialBaseLocationId(), s.getEpimmsId(),s.getServiceCode())));
        log.info("Insert Query batch Response from IDAM" + epimsLocationId.size());
        jdbcTemplate.batchUpdate(
                    updateEpimmsid,
                epimsLocationId,
                    10,
                    new ParameterizedPreparedStatementSetter<Triple<String, String, String>>() {
                public void setValues(PreparedStatement ps, Triple<String, String, String> argument)
                                throws SQLException {
                    ps.setString(1, argument.getMiddle());
                    ps.setString(2, argument.getRight());
                    ps.setString(3, argument.getLeft());
                    }
                });
    }

    private UserProfile buildUserProfileDto(
            ResultsRequest resultsRequest) {

        return UserProfile.builder()
                .personalCode(resultsRequest.getPersonalCode())
                .knownAs(resultsRequest.getKnownAs())
                .surname(resultsRequest.getSurname())
                .fullName(resultsRequest.getFullName())
                .postNominals(resultsRequest.getPostNominals())
                .ejudiciaryEmailId(resultsRequest.getEmail())
                .lastWorkingDate(convertToLocalDate(resultsRequest.getLastWorkingDate()))
                .activeFlag(true)
                .createdDate(LocalDateTime.now())
                .lastLoadedDate(LocalDateTime.now())
                .objectId(resultsRequest.getObjectId())
                .initials(resultsRequest.getInitials())
                .build();
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter).atStartOfDay();
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
