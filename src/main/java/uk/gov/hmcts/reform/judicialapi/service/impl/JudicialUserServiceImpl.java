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
import uk.gov.hmcts.reform.judicialapi.controller.response.AppointmentRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.AuthorisationRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserProfileRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserSearchResponse;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.domain.RegionMapping;
import uk.gov.hmcts.reform.judicialapi.domain.ServiceCodeMapping;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;
import uk.gov.hmcts.reform.judicialapi.repository.RegionMappingRepository;
import uk.gov.hmcts.reform.judicialapi.repository.ServiceCodeMappingRepository;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;
import uk.gov.hmcts.reform.judicialapi.util.RefDataConstants;
import uk.gov.hmcts.reform.judicialapi.util.RequestUtils;
import uk.gov.hmcts.reform.judicialapi.validator.RefreshUserValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.distinctByKeys;

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

    @Value("${refresh.serviceCode}")
    private String refreshServiceCode;

    @Value("${refresh.ticketCode}")
    private String refreshTicketCode;

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
                .toList();

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

        var userSearchResponses = userProfiles
                .stream().filter(distinctByKeys(UserProfile::getPersonalCode))
                .map(UserSearchResponse::new)
                .toList();

        return ResponseEntity
                .status(200)
                .body(userSearchResponses);

    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<Object> refreshUserProfile(RefreshRoleRequest refreshRoleRequest, Integer pageSize,
                                                     Integer pageNumber, String sortDirection, String sortColumn) {

        log.info("{} : starting refreshUserProfile ", loggingComponentName);
        refreshUserValidator.shouldContainOnlyOneInputParameter(refreshRoleRequest);
        var pageRequest = RequestUtils.validateAndBuildPaginationObject(pageSize, pageNumber,
                sortDirection, sortColumn, refreshDefaultPageSize, refreshDefaultSortColumn,
                UserProfile.class);

        return getRefreshUserProfileBasedOnParam(refreshRoleRequest, pageRequest);

    }

    private ResponseEntity<Object> getRefreshUserProfileBasedOnParam(RefreshRoleRequest refreshRoleRequest,
                                                                     PageRequest pageRequest) {
        log.info("{} : starting getRefreshUserProfile Based On Param ", loggingComponentName);
        if (refreshUserValidator.isStringNotEmptyOrNotNull(refreshRoleRequest.getCcdServiceNames())) {
            return refreshUserProfileBasedOnCcdServiceNames(refreshRoleRequest.getCcdServiceNames(), pageRequest);
        } else if (refreshUserValidator.isListNotEmptyOrNotNull(refreshRoleRequest.getSidamIds())) {
            return refreshUserProfileBasedOnSidamIds(
                    refreshUserValidator.removeEmptyOrNullFromList(refreshRoleRequest.getSidamIds()), pageRequest);
        } else if (refreshUserValidator.isListNotEmptyOrNotNull(refreshRoleRequest.getObjectIds())) {
            return refreshUserProfileBasedOnObjectIds(
                    refreshUserValidator.removeEmptyOrNullFromList(refreshRoleRequest.getObjectIds()), pageRequest);
        } else if (refreshUserValidator.isListNotEmptyOrNotNull(refreshRoleRequest.getPersonalCodes())) {
            return refreshUserProfileBasedOnPersonalCodes(refreshUserValidator.removeEmptyOrNullFromList(
                    refreshRoleRequest.getPersonalCodes()), pageRequest);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> refreshUserProfileBasedOnObjectIds(List<String> objectIds,
                                                                      PageRequest pageRequest) {
        log.info("{} : starting refreshUserProfile BasedOn ObjectIds ", loggingComponentName);
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
        log.info("{} : starting refreshUserProfile BasedOn SidamIds ", loggingComponentName);
        var userProfilePage = userProfileRepository.fetchUserProfileBySidamIds(
                sidamIds, pageRequest);
        if (userProfilePage == null || userProfilePage.isEmpty()) {
            log.error("{}:: No data found in JRD for the sidamIds {}",
                    loggingComponentName, sidamIds);
            throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
        }
        return getRefreshRoleResponseEntity(userProfilePage, sidamIds, "sidamIds");
    }

    private ResponseEntity<Object> refreshUserProfileBasedOnPersonalCodes(List<String> personalCodes,
                                                                          PageRequest pageRequest) {
        log.info("{} : starting refreshUserProfile BasedOn personalCodes ", loggingComponentName);
        var userProfilePage = userProfileRepository.fetchUserProfileByPersonalCodes(
                personalCodes, pageRequest);
        if (userProfilePage == null || userProfilePage.isEmpty()) {
            log.error("{}:: No data found in JRD for the personalCodes {}",
                    loggingComponentName, personalCodes);
            throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
        }
        return getRefreshRoleResponseEntity(userProfilePage, personalCodes, "personalCodes");
    }

    private ResponseEntity<Object> getRefreshRoleResponseEntity(Page<UserProfile> userProfilePage,
                                                                Object collection, String collectionName) {
        log.info("{} : starting getRefresh Role Response Entity ", loggingComponentName);
        var userProfileList = new ArrayList<UserProfileRefreshResponse>();

        var serviceCodeMappings = serviceCodeMappingRepository.findAllServiceCodeMapping();
        log.info("serviceCodeMappings size = {}", serviceCodeMappings.size());

        var regionMappings = regionMappingRepository.findAllRegionMappingData();
        log.info("regionMappings size = {}", regionMappings.size());

        userProfilePage.forEach(userProfile -> {
            UserProfileRefreshResponse response =  buildUserProfileRefreshResponseDto(userProfile,
                    serviceCodeMappings, regionMappings);
            if (null != response) {
                userProfileList.add(response);
            }
        });

        if (userProfileList.isEmpty()) {
            throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
        }

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
                .personalCode(v.get(0).getPersonalCode())
                .appointments(v.stream()
                        .flatMap(i -> i.getAppointments().stream())
                        .toList())
                .authorisations(v.stream()
                        .flatMap(i -> i.getAuthorisations().stream())
                        .toList())
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
        log.info("{} : starting refreshUserProfile BasedOn CcdServiceNames ", loggingComponentName);
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

        if (nonNull(responseBody) && responseBody instanceof ErrorResponse errorResponse) {
            throw new UserProfileException(httpStatus, errorResponse.getErrorMessage(),
                    errorResponse.getErrorDescription());
        } else {
            throw new UserProfileException(httpStatus, RefDataConstants.LRD_ERROR, RefDataConstants.LRD_ERROR);
        }
    }

    private UserProfileRefreshResponse buildUserProfileRefreshResponseDto(
            UserProfile profile, List<ServiceCodeMapping> serviceCodeMappings, List<RegionMapping> regionMappings) {
        log.info("{} : starting build User Profile Refresh Response Dto ", loggingComponentName);

        //check the Non-IAC records validation
        if (Boolean.TRUE.equals(validateIacCodes(profile,true))) {
            return  userProfileResponse(profile,serviceCodeMappings,regionMappings);
        } else {
            if (Boolean.TRUE.equals(validateIacCodes(profile,false))) {
                return  userProfileResponse(profile,serviceCodeMappings,regionMappings);
            }
        }
        return null;
    }

    private UserProfileRefreshResponse userProfileResponse(UserProfile profile,
                                                           List<ServiceCodeMapping> serviceCodeMappings,
                                                           List<RegionMapping> regionMappings) {

        return UserProfileRefreshResponse.builder()
                .sidamId(profile.getSidamId())
                .objectId(profile.getObjectId())
                .knownAs(profile.getKnownAs())
                .surname(profile.getSurname())
                .fullName(profile.getFullName())
                .postNominals(profile.getPostNominals())
                .emailId(profile.getEjudiciaryEmailId())
                .personalCode(profile.getPersonalCode())
                .appointments(getAppointmentRefreshResponseList(profile, regionMappings))
                .authorisations(getAuthorisationRefreshResponseList(profile, serviceCodeMappings))
                .build();
    }

    private List<AppointmentRefreshResponse> getAppointmentRefreshResponseList(
            UserProfile profile, List<RegionMapping> regionMappings) {
        log.info("{} : starting get Appointment Refresh Response List ", loggingComponentName);

        var appointmentList = new ArrayList<AppointmentRefreshResponse>();
        profile.getAppointments()
                .forEach(appointment -> appointmentList.add(
                        buildAppointmentRefreshResponseDto(appointment, profile, regionMappings)));
        return appointmentList;
    }

    private AppointmentRefreshResponse buildAppointmentRefreshResponseDto(
            Appointment appt, UserProfile profile, List<RegionMapping> regionMappings) {
        log.info("{} : starting build Appointment Refresh Response Dto ", loggingComponentName);

        RegionMapping regionMapping = regionMappings.stream()
                .filter(rm -> rm.getJrdRegionId().equalsIgnoreCase(appt.getRegionId()))
                .findFirst()
                .orElse(new RegionMapping());

        RegionMapping regionCircuitMapping = regionMappings.stream()
                .filter(rm -> rm.getRegion().equalsIgnoreCase(appt.getBaseLocationType().getCircuit()))
                .findFirst()
                .orElse(new RegionMapping());


        return AppointmentRefreshResponse.builder()
                .baseLocationId(appt.getBaseLocationId())
                .epimmsId(appt.getEpimmsId())
                .courtName(appt.getBaseLocationType().getCourtName())
                .cftRegionID(StringUtils.isBlank(appt.getEpimmsId()) ? regionMapping.getRegionId() :
                            regionCircuitMapping.getRegionId())
                .cftRegion(StringUtils.isBlank(appt.getEpimmsId()) ?  regionMapping.getRegion() :
                             regionCircuitMapping.getRegion())
                .locationId(StringUtils.isBlank(appt.getEpimmsId()) ? regionMapping.getJrdRegionId() :
                        regionCircuitMapping.getRegionId())
                .location(StringUtils.isBlank(appt.getEpimmsId()) ?  regionMapping.getJrdRegion()  :
                            regionCircuitMapping.getRegion())
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
        log.info("{} : starting get Authorisation Refresh Response List ", loggingComponentName);

        var authorisationList = new ArrayList<AuthorisationRefreshResponse>();

        profile.getAuthorisations()
                .forEach(authorisation -> authorisationList.add(
                        buildAuthorisationRefreshResponseDto(authorisation, serviceCodeMappings)));

        return authorisationList;
    }

    private AuthorisationRefreshResponse buildAuthorisationRefreshResponseDto(
            Authorisation auth, List<ServiceCodeMapping> serviceCodeMappings) {
        log.info("{} : starting build Authorisation Refresh Response Dto ", loggingComponentName);

        List<String> serviceCode = serviceCodeMappings.stream()
                .filter(s -> s.getTicketCode().equalsIgnoreCase(auth.getTicketCode()))
                .map(ServiceCodeMapping::getServiceCode)
                .toList();

        return AuthorisationRefreshResponse.builder()
                .jurisdiction(auth.getJurisdiction())
                .ticketDescription(auth.getLowerLevel())
                .ticketCode(auth.getTicketCode())
                .serviceCodes(serviceCode)
                .startDate(null != auth.getStartDate() ? String.valueOf(auth.getStartDate()) : null)
                .endDate(null != auth.getEndDate() ? String.valueOf(auth.getEndDate()) : null)
                .build();
    }

    private List<String> fetchTicketCodeFromServiceCode(Set<String> serviceCode) {
        log.info("{} : starting fetch Ticket CodeFrom Service Code ", loggingComponentName);
        return serviceCodeMappingRepository.fetchTicketCodeFromServiceCode(serviceCode);
    }

    private List<String> getRoleIdList(List<JudicialRoleType> judicialRoleTypes) {
        log.info("{} : starting get RoleId List ", loggingComponentName);
        return judicialRoleTypes.stream()
                .filter(e -> e.getEndDate() == null || !e.getEndDate().toLocalDate().isBefore(LocalDate.now()))
                .map(JudicialRoleType::getTitle).toList();
    }

    private Boolean validateIacCodes(UserProfile profile,Boolean iacFlag) {

        List<String> serviceCode = List.of(refreshServiceCode);
        List<String> ticketCodes = List.of(refreshTicketCode);

        List<Appointment> appointment = profile.getAppointments().stream()
                                       .filter(app -> StringUtils.isNotBlank(app.getServiceCode())
                                               ? (serviceCode.contains(app.getServiceCode()) == iacFlag) :
                                                       !iacFlag)
                                    .filter(app -> null != app.getEndDate() ? (app.getEndDate().equals(LocalDate.now())
                                       || app.getEndDate().isAfter(LocalDate.now())) : Boolean.TRUE)
                                       .toList();



        List<Authorisation> authorisation =  profile.getAuthorisations().stream()
                .filter(auth -> StringUtils.isNotBlank(auth.getTicketCode())
                        ? (ticketCodes.contains(auth.getTicketCode()) == iacFlag) : !iacFlag)
                .filter(auth -> null != auth.getEndDate() ? (auth.getEndDate().equals(LocalDateTime.now())
                        || auth.getEndDate().isAfter(LocalDateTime.now())) : Boolean.TRUE)
                .toList();

        //IAC flag check
        return Boolean.TRUE.equals(iacFlag) ? (!appointment.isEmpty() || !authorisation.isEmpty()) :
               (!appointment.isEmpty() && !authorisation.isEmpty());
    }
}