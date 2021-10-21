package uk.gov.hmcts.reform.judicialapi.service.impl;

import feign.Response;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.judicialapi.controller.advice.UserProfileException;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserSearchResponse;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.repository.ServiceCodeMappingRepository;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;
import uk.gov.hmcts.reform.judicialapi.util.RefDataConstants;
import uk.gov.hmcts.reform.judicialapi.util.RequestUtils;
import uk.gov.hmcts.reform.judicialapi.validator.RefreshUserValidator;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserProfileRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.AppointmentRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.AuthorisationRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.judicialapi.util.JrdConstant.USER_DATA_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;

@Slf4j
@Service
@Setter
public class JudicialUserServiceImpl implements JudicialUserService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ServiceCodeMappingRepository serviceCodeMappingRepository;

    @Value("${defaultPageSize}")
    Integer defaultPageSize;

    @Value("${refresh.pageSize}")
    private int refreshDefaultPageSize;

    @Value("${refresh.sortColumn}")
    private String refreshDefaultSortColumn;

    @Autowired
    private RefreshUserValidator refreshUserValidator;

    @Autowired
    private LocationReferenceDataFeignClient locationReferenceDataFeignClient;

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @Override
    public ResponseEntity<Object> fetchJudicialUsers(Integer size, Integer page, List<String> sidamIds) {
        Pageable pageable = createPageableObject(page, size, defaultPageSize);
        Page<UserProfile> pagedUserProfiles = userProfileRepository.findBySidamIdIn(sidamIds, pageable);

        List<UserProfile> userProfiles = pagedUserProfiles.getContent();

        if (CollectionUtils.isEmpty(userProfiles)) {
            throw new ResourceNotFoundException("Data not found");
        }

        List<OrmResponse> ormResponses = userProfiles.stream()
                .map(OrmResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(200)
                .body(ormResponses);
    }

    @Override
    public ResponseEntity<Object> retrieveUserProfile(UserSearchRequest userSearchRequest) {
        var ticketCode = new ArrayList<String>();

        if (userSearchRequest.getServiceCode() != null) {
            var serviceCodeMappings = serviceCodeMappingRepository
                    .findByServiceCodeIgnoreCase(userSearchRequest.getServiceCode());

            serviceCodeMappings
                    .forEach(s -> ticketCode.add(s.getTicketCode()));
        }

        var userProfiles = userProfileRepository
                .findBySearchString(userSearchRequest.getSearchString().toLowerCase(),
                        userSearchRequest.getServiceCode(), userSearchRequest.getLocation(), ticketCode);

        if (CollectionUtils.isEmpty(userProfiles)) {
            throw new ResourceNotFoundException(USER_DATA_NOT_FOUND);
        }

        var userSearchResponses = userProfiles
                .stream()
                .map(UserSearchResponse::new)
                .collect(Collectors.toUnmodifiableList());

        return ResponseEntity
                .status(200)
                .body(userSearchResponses);

    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<Object> refreshUserProfile(RefreshRoleRequest refreshRoleRequest, Integer pageSize,
                                                     Integer pageNumber, String sortDirection, String sortColumn) {

        refreshUserValidator.shouldContainOnlyOneInputParameter(refreshRoleRequest);
        PageRequest pageRequest = RequestUtils.validateAndBuildPaginationObject(pageSize, pageNumber,
                sortDirection, sortColumn, refreshDefaultPageSize, refreshDefaultSortColumn,
                UserProfile.class);

        return (refreshRoleRequest != null) ? getRefreshUserProfileBasedOnParam(refreshRoleRequest, pageRequest)
                : refreshUserProfileBasedOnAll(pageRequest);

    }

    private ResponseEntity<Object> getRefreshUserProfileBasedOnParam(RefreshRoleRequest refreshRoleRequest,
                                                                     PageRequest pageRequest) {
        if (refreshUserValidator.isStringNotEmptyOrNotNull(refreshRoleRequest.getCcdServiceNames())) {
            return refreshUserProfileBasedOnCcdServiceNames(refreshRoleRequest.getCcdServiceNames(), pageRequest);
        } else if (refreshUserValidator.isListNotEmptyOrNotNull(refreshRoleRequest.getSidamIds())) {
            return refreshUserProfileBasedOnSidamIds(
                    refreshUserValidator.removeEmptyOrNullFromList(refreshRoleRequest.getSidamIds()), pageRequest);
        } else if (refreshUserValidator.isListNotEmptyOrNotNull(refreshRoleRequest.getObjectIds())) {
            return refreshUserProfileBasedOnObjectIds(
                    refreshUserValidator.removeEmptyOrNullFromList(refreshRoleRequest.getObjectIds()), pageRequest);
        } else {
            return refreshUserProfileBasedOnAll(pageRequest);
        }
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> refreshUserProfileBasedOnObjectIds(List<String> objectIds,
                                                                      PageRequest pageRequest) {
        log.info("starting refreshUserProfile BasedOn ObjectIds");
        Page<UserProfile> userProfilePage = userProfileRepository.fetchUserProfileByObjectIds(
                objectIds, pageRequest);

        if (userProfilePage.isEmpty()) {
            log.error("{}:: No data found in JRD for the objectIds {}",
                    loggingComponentName, objectIds);
            throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
        }

        return getRefreshRoleResponseEntity(userProfilePage, objectIds, "objectIds");
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> refreshUserProfileBasedOnSidamIds(List<String> sidamIds,
                                                                     PageRequest pageRequest) {
        log.info("starting refreshUserProfile BasedOn SidamIds");
        Page<UserProfile> userProfilePage = userProfileRepository.fetchUserProfileBySidamIds(
                sidamIds, pageRequest);
        if (userProfilePage.isEmpty()) {
            log.error("{}:: No data found in JRD for the sidamIds {}",
                    loggingComponentName, sidamIds);
            throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
        }
        return getRefreshRoleResponseEntity(userProfilePage, sidamIds, "sidamIds");
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> refreshUserProfileBasedOnAll(PageRequest pageRequest) {
        log.info("starting refreshUserProfile BasedOn All");

        Page<UserProfile> userProfilePage = userProfileRepository.fetchUserProfileByAll(pageRequest);

        if (userProfilePage.isEmpty()) {
            log.error("{}:: No data found in JRD {}", loggingComponentName);
            throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
        }

        return getRefreshRoleResponseEntity(userProfilePage, "", "All");
    }

    private ResponseEntity<Object> getRefreshRoleResponseEntity(Page<UserProfile> userProfilePage,
                                                                Object collection, String collectionName) {
        List<UserProfileRefreshResponse> userProfileList = new ArrayList<>();
        userProfilePage.forEach(userProfile -> userProfileList.add(buildUserProfileRefreshResponseDto(userProfile)));
        log.info("userProfileList size = {}", userProfileList.size());

        log.info("{}:: Successfully fetched the User Profile details to refresh role assignment "
                + "for " + collectionName + " {}", loggingComponentName, collection);
        return ResponseEntity
                .ok()
                .header("total_records", String.valueOf(userProfilePage.getTotalElements()))
                .body(userProfileList);

    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> refreshUserProfileBasedOnCcdServiceNames(String ccdServiceNames,
                                                                            PageRequest pageRequest) {
        log.info("starting refreshUserProfile BasedOn CcdServiceNames");
        Response lrdOrgInfoServiceResponse =
                locationReferenceDataFeignClient.getLocationRefServiceMapping(ccdServiceNames);
        HttpStatus httpStatus = HttpStatus.valueOf(lrdOrgInfoServiceResponse.status());

        if (httpStatus.is2xxSuccessful()) {
            ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toResponseEntityWithListBody(
                    lrdOrgInfoServiceResponse, LrdOrgInfoServiceResponse.class);

            List<LrdOrgInfoServiceResponse> listLrdServiceMapping =
                    (List<LrdOrgInfoServiceResponse>) responseEntity.getBody();

            if (listLrdServiceMapping != null && !listLrdServiceMapping.isEmpty()) {

                Map<String, String> ccdServiceNameToCodeMapping =
                        listLrdServiceMapping
                                .stream()
                                .filter(r -> StringUtils.isNotBlank(r.getServiceCode())
                                        && StringUtils.isNotBlank(r.getCcdServiceName()))
                                .collect(Collectors.toMap(LrdOrgInfoServiceResponse::getServiceCode,
                                        LrdOrgInfoServiceResponse::getCcdServiceName));

                log.info("ccdServiceNameToCodeMapping keySet {}", ccdServiceNameToCodeMapping.keySet());
                List<String> ticketCode = fetchTicketCodeFromServiceCode(ccdServiceNameToCodeMapping.keySet());

                Page<UserProfile> userProfilePage = userProfileRepository.fetchUserProfileByServiceNames(
                        ccdServiceNameToCodeMapping.keySet(), ticketCode, pageRequest);

                if (userProfilePage.isEmpty()) {
                    log.error("{}:: No data found in JRD for the ccdServiceNames {}",
                            loggingComponentName, ccdServiceNames);
                    throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
                }

                return getRefreshRoleResponseEntity(userProfilePage, ccdServiceNames, "ccdServiceNames");
            }
        }

        log.error("{}:: Error in getting the data from LRD for the ccdServiceNames {} :: Status code {}",
                loggingComponentName, ccdServiceNames, httpStatus);
        ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toResponseEntity(lrdOrgInfoServiceResponse,
                ErrorResponse.class);
        Object responseBody = responseEntity.getBody();
        if (nonNull(responseBody) && responseBody instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) responseBody;
            throw new UserProfileException(httpStatus, errorResponse.getErrorMessage(),
                    errorResponse.getErrorDescription());
        } else {
            throw new UserProfileException(httpStatus, RefDataConstants.LRD_ERROR, RefDataConstants.LRD_ERROR);
        }
    }

    private UserProfileRefreshResponse buildUserProfileRefreshResponseDto(UserProfile profile) {
        return UserProfileRefreshResponse.builder()
                .perId(profile.getPerId())
                .sidamId(profile.getSidamId())
                .objectId(profile.getObjectId())
                .knownAs(profile.getKnownAs())
                .surname(profile.getSurname())
                .fullName(profile.getFullName())
                .postNominals(profile.getPostNominals())
                .emailId(profile.getEjudiciaryEmailId())
                .appointments(getAppointmentRefreshResponseList(profile))
                .authorisations(getAuthorisationRefreshResponseList(profile))
                .build();
    }

    private List<AppointmentRefreshResponse> getAppointmentRefreshResponseList(UserProfile profile) {

        List<AppointmentRefreshResponse> appointmentList = new ArrayList<>();

        profile.getAppointments().stream()
                .forEach(appointment -> appointmentList.add(buildAppointmentRefreshResponseDto(appointment, profile)));
        return appointmentList;
    }

    private AppointmentRefreshResponse buildAppointmentRefreshResponseDto(Appointment appt,
                                                                          UserProfile profile) {
        return AppointmentRefreshResponse.builder()
                .perId(appt.getPerId())
                .officeAppointmentId(String.valueOf(appt.getOfficeAppointmentId()))
                .baseLocationId(appt.getBaseLocationType().getBaseLocationId())
                .epimmsId(appt.getEpimmsId())
                .courtName(appt.getBaseLocationType().getCourtName())
                .regionId(appt.getRegionType().getRegionId())
                .regionDescEn(appt.getRegionType().getRegionDescEn())
                .regionDescCy(appt.getRegionType().getRegionDescCy())
                .locationId(appt.getBaseLocationType().getBaseLocationId())
                .location(appt.getBaseLocationType().getCircuit())
                .isPrincipleAppointment(String.valueOf(appt.getIsPrincipleAppointment()))
                .appointment(appt.getAppointment())
                .appointmentType(appt.getAppointmentType())
                .serviceCode(appt.getServiceCode())
                .roles(getRoleIdList(profile.getJudicialRoleTypes()))
                .startDate(String.valueOf(appt.getStartDate()))
                .endDate(String.valueOf(appt.getEndDate()))
                .build();
    }

    private List<AuthorisationRefreshResponse> getAuthorisationRefreshResponseList(UserProfile profile) {
        List<AuthorisationRefreshResponse> authorisationList = new ArrayList<>();

        profile.getAuthorisations().stream()
                .forEach(authorisation -> authorisationList.add(buildAuthorisationRefreshResponseDto(authorisation)));

        return authorisationList;
    }

    private AuthorisationRefreshResponse buildAuthorisationRefreshResponseDto(Authorisation auth) {
        return AuthorisationRefreshResponse.builder()
                .perId(auth.getPerId())
                .officeAuthId(String.valueOf(auth.getOfficeAuthId()))
                .jurisdiction(auth.getJurisdiction())
                .ticketDescription(auth.getLowerLevel())
                .ticketCode(auth.getTicketCode())
                .startDate(String.valueOf(auth.getStartDate()))
                .endDate(String.valueOf(auth.getEndDate()))
                .build();
    }

    private List<String> fetchTicketCodeFromServiceCode(Set<String> serviceCode) {
        return userProfileRepository.fetchTicketCodeFromServiceCode(serviceCode);
    }

    private List<String> getRoleIdList(List<JudicialRoleType> judicialRoleTypes) {
        return judicialRoleTypes.stream().map(judicialRoleType ->
                judicialRoleType.getRoleId()).collect(Collectors.toList());
    }

}
