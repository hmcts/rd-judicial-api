package uk.gov.hmcts.reform.judicialapi.service.impl;

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
import uk.gov.hmcts.reform.judicialapi.domain.RegionMapping;
import uk.gov.hmcts.reform.judicialapi.domain.ServiceCodeMapping;
import uk.gov.hmcts.reform.judicialapi.repository.ServiceCodeMappingRepository;
import uk.gov.hmcts.reform.judicialapi.repository.RegionMappingRepository;
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

    @Autowired
    private RegionMappingRepository regionMappingRepository;

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
        var pageRequest = RequestUtils.validateAndBuildPaginationObject(pageSize, pageNumber,
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
        var userProfilePage = userProfileRepository.fetchUserProfileByObjectIds(
                objectIds, pageRequest);


        if (userProfilePage == null || userProfilePage.isEmpty()) {
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
        var userProfilePage = userProfileRepository.fetchUserProfileBySidamIds(
                sidamIds, pageRequest);
        if (userProfilePage == null || userProfilePage.isEmpty()) {
            log.error("{}:: No data found in JRD for the sidamIds {}",
                    loggingComponentName, sidamIds);
            throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
        }
        return getRefreshRoleResponseEntity(userProfilePage, sidamIds, "sidamIds");
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> refreshUserProfileBasedOnAll(PageRequest pageRequest) {
        log.info("starting refreshUserProfile BasedOn All");

        var userProfilePage = userProfileRepository.fetchUserProfileByAll(pageRequest);

        if (userProfilePage == null || userProfilePage.isEmpty()) {
            log.error("{}:: No data found in JRD {}", loggingComponentName);
            throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
        }

        return getRefreshRoleResponseEntity(userProfilePage, "", "All");
    }

    private ResponseEntity<Object> getRefreshRoleResponseEntity(Page<UserProfile> userProfilePage,
                                                                Object collection, String collectionName) {
        var userProfileList = new ArrayList<UserProfileRefreshResponse>();

        var serviceCodeMappings = serviceCodeMappingRepository.findAllServiceCodeMapping();
        log.info("serviceCodeMappings size = {}", serviceCodeMappings.size());

        var regionMappings = regionMappingRepository.findAllRegionMappingData();
        log.info("regionMappings size = {}", regionMappings.size());

        userProfilePage.forEach(userProfile -> userProfileList.add(
                buildUserProfileRefreshResponseDto(userProfile,serviceCodeMappings,regionMappings)));

        Map<String, List<UserProfileRefreshResponse>> groupedUserProfiles = userProfileList
                .stream()
                .collect(Collectors.groupingBy(UserProfileRefreshResponse::getEmailId));

        var refreshResponse = new ArrayList<UserProfileRefreshResponse>();

        groupedUserProfiles.forEach((k, v) -> refreshResponse.add(UserProfileRefreshResponse.builder()
                .surname(v.get(0).getSurname())
                .fullName(v.get(0).getFullName())
                .emailId(v.get(0).getEmailId())
                .sidamId(v.get(0).getSidamId())
                .objectId(v.get(0).getObjectId())
                .knownAs(v.get(0).getKnownAs())
                .postNominals(v.get(0).getPostNominals())
                .appointments(v.stream()
                        .flatMap(i -> i.getAppointments().stream())
                        .collect(Collectors.toList()))
                .authorisations(v.stream()
                        .flatMap(i -> i.getAuthorisations().stream())
                        .collect(Collectors.toList()))
                .build()));

        log.info("userProfileList size = {}", userProfileList.size());

        log.info("{}:: Successfully fetched the User Profile details to refresh role assignment "
                + "for " + collectionName + " {}", loggingComponentName, collection);
        return ResponseEntity
                .ok()
                .header("total_records", String.valueOf(userProfilePage.getTotalElements()))
                .body(refreshResponse);

    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> refreshUserProfileBasedOnCcdServiceNames(String ccdServiceNames,
                                                                            PageRequest pageRequest) {
        log.info("starting refreshUserProfile BasedOn CcdServiceNames");
        var lrdOrgInfoServiceResponse =
                locationReferenceDataFeignClient.getLocationRefServiceMapping(ccdServiceNames);
        var httpStatus = HttpStatus.valueOf(lrdOrgInfoServiceResponse.status());

        if (httpStatus.is2xxSuccessful()) {
            ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toResponseEntityWithListBody(
                    lrdOrgInfoServiceResponse, LrdOrgInfoServiceResponse.class);

            var listLrdServiceMapping =
                    (List<LrdOrgInfoServiceResponse>) responseEntity.getBody();

            if (listLrdServiceMapping != null && !listLrdServiceMapping.isEmpty()) {

                var ccdServiceNameToCodeMapping =
                        listLrdServiceMapping
                                .stream()
                                .filter(r -> StringUtils.isNotBlank(r.getServiceCode())
                                        && StringUtils.isNotBlank(r.getCcdServiceName()))
                                .collect(Collectors.toMap(LrdOrgInfoServiceResponse::getServiceCode,
                                        LrdOrgInfoServiceResponse::getCcdServiceName));
                log.info("ccdServiceNameToCodeMapping keySet {}", ccdServiceNameToCodeMapping.keySet());

                var ticketCode = fetchTicketCodeFromServiceCode(ccdServiceNameToCodeMapping.keySet());
                log.info("ticketCode {}", ticketCode);

                var userProfilePage = userProfileRepository.fetchUserProfileByServiceNames(
                        ccdServiceNameToCodeMapping.keySet(), ticketCode, pageRequest);

                if (userProfilePage == null || userProfilePage.isEmpty()) {
                    log.error("{}:: No data found in JRD for the ccdServiceNames {}",
                            loggingComponentName, ccdServiceNames);
                    throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
                }

                return getRefreshRoleResponseEntity(userProfilePage, ccdServiceNames, "ccdServiceNames");
            }
        }

        log.error("{}:: Error in getting the data from LRD for the ccdServiceNames {} :: Status code {}",
                loggingComponentName, ccdServiceNames, httpStatus);
        var responseEntity = JsonFeignResponseUtil.toResponseEntity(lrdOrgInfoServiceResponse,
                ErrorResponse.class);
        var responseBody = responseEntity.getBody();

        if (nonNull(responseBody) && responseBody instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) responseBody;
            throw new UserProfileException(httpStatus, errorResponse.getErrorMessage(),
                    errorResponse.getErrorDescription());
        } else {
            throw new UserProfileException(httpStatus, RefDataConstants.LRD_ERROR, RefDataConstants.LRD_ERROR);
        }
    }

    private UserProfileRefreshResponse buildUserProfileRefreshResponseDto(
            UserProfile profile, List<ServiceCodeMapping> serviceCodeMappings, List<RegionMapping> regionMappings) {
        return UserProfileRefreshResponse.builder()
                .sidamId(profile.getSidamId())
                .objectId(profile.getObjectId())
                .knownAs(profile.getKnownAs())
                .surname(profile.getSurname())
                .fullName(profile.getFullName())
                .postNominals(profile.getPostNominals())
                .emailId(profile.getEjudiciaryEmailId())
                .appointments(getAppointmentRefreshResponseList(profile,regionMappings))
                .authorisations(getAuthorisationRefreshResponseList(profile,serviceCodeMappings))
                .build();
    }

    private List<AppointmentRefreshResponse> getAppointmentRefreshResponseList(
            UserProfile profile, List<RegionMapping> regionMappings) {

        var appointmentList = new ArrayList<AppointmentRefreshResponse>();

        profile.getAppointments().stream()
                .forEach(appointment -> appointmentList.add(
                        buildAppointmentRefreshResponseDto(appointment, profile, regionMappings)));
        return appointmentList;
    }

    private AppointmentRefreshResponse buildAppointmentRefreshResponseDto(
            Appointment appt, UserProfile profile, List<RegionMapping> regionMappings) {

        RegionMapping regionMapping = regionMappings.stream()
                .filter(rm -> rm.getJrdRegionId().equalsIgnoreCase(appt.getRegionId()))
                .findFirst()
                .orElse(null);

        return AppointmentRefreshResponse.builder()
                .baseLocationId(appt.getBaseLocationId())
                .epimmsId(appt.getEpimmsId())
                .courtName(appt.getBaseLocationType().getCourtName())
                .cftRegionID(null != regionMapping ? regionMapping.getRegionId() : null)
                .cftRegion(null != regionMapping ? regionMapping.getRegion() : null)
                .locationId(appt.getRegionId())
                .location(null != regionMapping ? regionMapping.getJrdRegion() : null)
                .isPrincipalAppointment(String.valueOf(appt.getIsPrincipleAppointment()))
                .appointment(appt.getAppointment())
                .appointmentType(appt.getAppointmentType())
                .serviceCode(appt.getServiceCode())
                .roles(getRoleIdList(profile.getJudicialRoleTypes()))
                .startDate(null != appt.getStartDate() ? String.valueOf(appt.getStartDate()) : null)
                .endDate(null != appt.getEndDate() ? String.valueOf(appt.getEndDate()) : null)
                .build();
    }

    private List<AuthorisationRefreshResponse> getAuthorisationRefreshResponseList(
            UserProfile profile, List<ServiceCodeMapping> serviceCodeMappings) {
        var authorisationList = new ArrayList<AuthorisationRefreshResponse>();

        profile.getAuthorisations().stream()
                .forEach(authorisation -> authorisationList.add(
                        buildAuthorisationRefreshResponseDto(authorisation, serviceCodeMappings)));

        return authorisationList;
    }

    private AuthorisationRefreshResponse buildAuthorisationRefreshResponseDto(
            Authorisation auth, List<ServiceCodeMapping> serviceCodeMappings) {

        String serviceCode = serviceCodeMappings.stream()
                .filter(s -> s.getTicketCode().equalsIgnoreCase(auth.getTicketCode()))
                .map(ServiceCodeMapping::getServiceCode)
                .collect(Collectors.joining(","));

        return AuthorisationRefreshResponse.builder()
                .jurisdiction(auth.getJurisdiction())
                .ticketDescription(auth.getLowerLevel())
                .ticketCode(auth.getTicketCode())
                .serviceCode(serviceCode)
                .startDate(null != auth.getStartDate() ? String.valueOf(auth.getStartDate()) : null)
                .endDate(null != auth.getEndDate() ? String.valueOf(auth.getEndDate()) : null)
                .build();
    }

    private List<String> fetchTicketCodeFromServiceCode(Set<String> serviceCode) {
        return serviceCodeMappingRepository.fetchTicketCodeFromServiceCode(serviceCode);
    }

    private List<String> getRoleIdList(List<JudicialRoleType> judicialRoleTypes) {
        return judicialRoleTypes.stream().map(JudicialRoleType::getTitle).collect(Collectors.toList());
    }

}
