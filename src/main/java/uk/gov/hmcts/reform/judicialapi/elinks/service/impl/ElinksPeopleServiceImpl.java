package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.AppointmentsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.AuthorisationsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.PeopleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.ResultsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.LocationMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointementsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationMapppingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleService;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.AUDIT_DATA_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DATA_UPDATE_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.THREAD_INVOCATION_EXCEPTION;

@Slf4j
@Service
public class ElinksPeopleServiceImpl implements ElinksPeopleService {

    public static final String EPIMMS_ID = "epimmsId";
    public static final String SERVICE_CODE = "serviceCode";
    @Autowired
    private ElinksFeignClient elinksFeignClient;

    @Autowired
    private AppointementsRepository appointementsRepository;

    @Autowired
    private AuthorisationsRepository authorisationsRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private LocationMapppingRepository locationMapppingRepository;

    @Autowired
    private DataloadSchedularAuditRepository dataloadSchedularAuditRepository;


    @Value("${elinks.people.lastUpdated}")
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
    public ResponseEntity<Object> updatePeople() {
        boolean isMorePagesAvailable = true;
        HttpStatus httpStatus = null;

        while (isMorePagesAvailable) {
            Response peopleApiResponse = getPeopleResposeFromElinks();
            httpStatus = HttpStatus.valueOf(peopleApiResponse.status());
            ResponseEntity<Object> responseEntity;

            if (httpStatus.is2xxSuccessful()) {
                responseEntity = JsonFeignResponseUtil.toResponseEntity(peopleApiResponse, PeopleRequest.class);
                PeopleRequest elinkPeopleResponseRequest = (PeopleRequest) responseEntity.getBody();
                if (Optional.ofNullable(elinkPeopleResponseRequest).isPresent()
                        && Optional.ofNullable(elinkPeopleResponseRequest.getPagination()).isPresent()
                        && Optional.ofNullable(elinkPeopleResponseRequest.getResultsRequests()).isPresent()) {
                    isMorePagesAvailable = elinkPeopleResponseRequest.getPagination().getMorePages();
                    processPepleResponse(elinkPeopleResponseRequest);
                } else {
                    throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
                }
            } else {
                handleELinksErrorResponse(httpStatus);
            }
            pauseThread(Long.valueOf(threadPauseTime));
        }

        return ResponseEntity
                .status(httpStatus)
                .body(PEOPLE_DATA_LOAD_SUCCESS);


    }

    private Response getPeopleResposeFromElinks() {
        String updatedSince = getUpdateSince();
        try {
            return elinksFeignClient.getPeopleDetials(updatedSince, perPage, page,
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
            updatedSince = lastUpdated;
        } else {
            updatedSince = maxSchedulerEndTime.toString();
            updatedSince = updatedSince.substring(0, updatedSince.indexOf('T'));
        }
        return updatedSince;
    }

    private void processPepleResponse(PeopleRequest elinkPeopleResponseRequest) {
        try {
            List<ResultsRequest> resultsRequests = elinkPeopleResponseRequest.getResultsRequests();

            List<UserProfile> userProfiles = resultsRequests.stream().map(this::buildUserProfileDto)
                    .toList();
            profileRepository.saveAll(userProfiles);

            List<uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment> appointments = elinkPeopleResponseRequest
                    .getResultsRequests().stream()
                    .map(ResultsRequest::getAppointmentsRequests).flatMap(Collection::stream)
                    .map(this::buildAppointmentDto).toList();
            List<String> appointmentsPersonalCodesToDelete = appointments.stream()
                    .map(uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment::getPersonalCode).toList();
            appointementsRepository.deleteByPersonalCodeIn(appointmentsPersonalCodesToDelete);
            appointementsRepository.saveAll(appointments);

            List<uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation> authorisations =
                    elinkPeopleResponseRequest
                    .getResultsRequests().stream()
                    .map(ResultsRequest::getAuthorisationsRequests).flatMap(Collection::stream)
                    .map(this::buildAuthorisationsDto).toList();
            List<String> authorisationPersonalCodesToDelete = authorisations.stream()
                    .map(uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation::getPersonalCode).toList();
            authorisationsRepository.deleteByPersonalCodeIn(authorisationPersonalCodesToDelete);
            authorisationsRepository.saveAll(authorisations);
        } catch (Exception ex) {
            throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, DATA_UPDATE_ERROR, DATA_UPDATE_ERROR);
        }

    }

    private uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation buildAuthorisationsDto(AuthorisationsRequest
                                                                                                       authorisation) {
        return uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation.builder()
                .personalCode(authorisation.getPersonalCode())
                .jurisdiction(authorisation.getJurisdiction())
                .startDate(convertToLocalDateTime(authorisation.getStartDate()))
                .endDate(convertToLocalDateTime(authorisation.getEndDate()))
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .lowerLevel(authorisation.getLowerLevel())
                .objectId(authorisation.getObjectId())
                .ticketCode(authorisation.getTicketCode())
                .build();

    }


    private uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment
        buildAppointmentDto(AppointmentsRequest appointment) {
        Map<String,String> locationMappingDetails = getDetailsFromJudicilaLocationMapping(appointment
                .getBaseLocationId());
        return uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment.builder()
                .personalCode(appointment.getPersonalCode())
                .baseLocationId(appointment.getBaseLocationId())
                .regionId(appointment.getRegionId())
                .isPrincipleAppointment(appointment.getIsPrincipleAppointment())
                .startDate(convertToLocalDate(appointment.getStartDate()))
                .endDate(convertToLocalDate(appointment.getEndDate()))
                .createdDate(LocalDateTime.now())
                .lastLoadedDate(LocalDateTime.now())
                .epimmsId(locationMappingDetails.get(EPIMMS_ID))
                .serviceCode(locationMappingDetails.get(SERVICE_CODE))
                .objectId(appointment.getObjectId())
                .appointmentType(appointment.getAppointmentType())
                .workPattern(appointment.getWorkPattern())
                .build();
    }

    private Map<String, String> getDetailsFromJudicilaLocationMapping(String baseLocationId) {
        Map<String,String> locationMappingDetails = new HashMap<>();
        Optional<LocationMapping>  locationMapping = locationMapppingRepository.findById(baseLocationId);
        if (locationMapping.isPresent()) {
            locationMappingDetails.put(SERVICE_CODE, locationMapping.get().getServiceCode());
            locationMappingDetails.put(EPIMMS_ID, locationMapping.get().getEpimmsId());
        }

        return locationMappingDetails;
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
