package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.judicialapi.controller.advice.UserProfileException;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ServiceCodeMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationMapppingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ServiceCodeMappingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.AppointmentRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.AuthorisationRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.JudicialRoleTypeRefresh;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserProfileRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinkUserService;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RequestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.validator.ElinksRefreshUserValidator;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.LRD_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.NO_DATA_FOUND;

@Slf4j
@Service
@Setter
public class ElinkUserServiceImpl implements ElinkUserService {

    @Autowired
    private ProfileRepository userProfileRepository;

    @Autowired
    private JudicialRoleTypeRepository judicialRoleTypeRepository;

    @Autowired
    private AuthorisationsRepository authorisationsRepository;

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    @Autowired
    private LocationMapppingRepository locationMapppingRepository;

    @Autowired
    @Qualifier("elinksServiceCodeMappingRepository")
    private ServiceCodeMappingRepository serviceCodeMappingRepository;

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @Value("${refresh.pageSize}")
    private int refreshDefaultPageSize;

    @Value("${refresh.sortColumn}")
    private String refreshDefaultSortColumn;

    @Value("${search.serviceCode}")
    private List<String> searchServiceCode;

    @Autowired
    private LocationReferenceDataFeignClient locationReferenceDataFeignClient;

    @Autowired
    private ElinksRefreshUserValidator elinksRefreshUserValidator;

    @Override
    public ResponseEntity<Object> retrieveElinkUsers(UserSearchRequest userSearchRequest) {
        var ticketCode = new ArrayList<String>();

        if (userSearchRequest.getServiceCode() != null) {
            var serviceCodeMappings = serviceCodeMappingRepository
                .findByServiceCodeIgnoreCase(userSearchRequest.getServiceCode());

            serviceCodeMappings
                .forEach(s -> ticketCode.add(s.getTicketCode()));
        }
        log.info("SearchServiceCode list = {}", searchServiceCode);
        var userSearchResponses = userProfileRepository
            .findBySearchForString(userSearchRequest.getSearchString().toLowerCase(),
                userSearchRequest.getServiceCode(), userSearchRequest.getLocation(), ticketCode,
                searchServiceCode);

        return ResponseEntity
            .status(200)
            .body(userSearchResponses);
    }

    @Override
    public ResponseEntity<Object> refreshUserProfile(RefreshRoleRequest refreshRoleRequest, Integer pageSize,
                                                     Integer pageNumber, String sortDirection, String sortColumn) {

        log.info("{} : starting refreshUserProfile ", loggingComponentName);
        elinksRefreshUserValidator.shouldContainOnlyOneInputParameter(refreshRoleRequest);
        var pageRequest = RequestUtils.validateAndBuildPaginationObject(pageSize, pageNumber,
                sortDirection, sortColumn, refreshDefaultPageSize, refreshDefaultSortColumn,
                UserProfile.class);

        return getRefreshUserProfileBasedOnParam(refreshRoleRequest, pageRequest);

    }

    private ResponseEntity<Object> getRefreshUserProfileBasedOnParam(RefreshRoleRequest refreshRoleRequest,
                                                                     PageRequest pageRequest) {
        log.info("{} : starting getRefreshUserProfile Based On Param ", loggingComponentName);
        if (elinksRefreshUserValidator.isStringNotEmptyOrNotNull(refreshRoleRequest.getCcdServiceNames())) {
            return refreshUserProfileBasedOnCcdServiceNames(refreshRoleRequest.getCcdServiceNames(), pageRequest);
        } else if (elinksRefreshUserValidator.isListNotEmptyOrNotNull(refreshRoleRequest.getSidamIds())) {
            return refreshUserProfileBasedOnSidamIds(
                    elinksRefreshUserValidator.removeEmptyOrNullFromList(refreshRoleRequest.getSidamIds()),
                    pageRequest);
        } else if (elinksRefreshUserValidator.isListNotEmptyOrNotNull(refreshRoleRequest.getObjectIds())) {
            return refreshUserProfileBasedOnObjectIds(
                    elinksRefreshUserValidator.removeEmptyOrNullFromList(refreshRoleRequest.getObjectIds()),
                    pageRequest);
        } else if (elinksRefreshUserValidator.isListNotEmptyOrNotNull(refreshRoleRequest.getPersonalCodes())) {
            return refreshUserProfileBasedOnPersonalCodes(elinksRefreshUserValidator.removeEmptyOrNullFromList(
                 refreshRoleRequest.getPersonalCodes()), pageRequest);
        }
        return new ResponseEntity<>(HttpStatus.OK);
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
                var userProfilePage = fetchUserProfiles(pageRequest, ccdServiceNameToCodeMapping, ticketCode);

                if (userProfilePage == null || userProfilePage.isEmpty()) {
                    log.error("{}:: No data found in JRD for the ccdServiceNames {}",
                            loggingComponentName, ccdServiceNames);
                    throw new ResourceNotFoundException(NO_DATA_FOUND);
                }

                return getRefreshRoleResponseEntity(userProfilePage, ccdServiceNames, "ccdServiceNames", pageRequest);
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
            throw new UserProfileException(httpStatus, LRD_ERROR, LRD_ERROR);
        }
    }

    private Page<UserProfile> fetchUserProfiles(PageRequest pageRequest,
                                                Map<String, String> ccdServiceNameToCodeMapping,
                                                List<String> ticketCode) {
        Page<UserProfile> userProfilePage;
        if (hasSpecialTribunalCic(ccdServiceNameToCodeMapping.keySet())) {
            userProfilePage = userProfileRepository.fetchUserProfileByTicketCodes(ticketCode, pageRequest);
        } else {
            userProfilePage = userProfileRepository.fetchUserProfileByServiceNames(
                    ccdServiceNameToCodeMapping.keySet(), ticketCode, pageRequest);
        }
        return userProfilePage;
    }

    private boolean hasSpecialTribunalCic(Set<String> serviceCodes) {
        return serviceCodes.stream().anyMatch(serviceCode -> "BBA2".equalsIgnoreCase(serviceCode));
    }


    private List<String> fetchTicketCodeFromServiceCode(Set<String> serviceCode) {
        log.info("{} : starting fetch Ticket CodeFrom Service Code ", loggingComponentName);
        return serviceCodeMappingRepository.fetchTicketCodeFromServiceCode(serviceCode);
    }


    private ResponseEntity<Object> refreshUserProfileBasedOnObjectIds(List<String> objectIds,
                                                                      PageRequest pageRequest) {
        log.info("{} : starting refreshUserProfile BasedOn ObjectIds ", loggingComponentName);
        var userProfilePage = userProfileRepository.fetchUserProfileByObjectIds(
                objectIds, pageRequest);

        if (userProfilePage == null || userProfilePage.isEmpty()) {
            log.error("{}:: No data found in JRD for the objectIds {}",
                    loggingComponentName, objectIds);
            throw new ResourceNotFoundException(NO_DATA_FOUND);
        }

        return getRefreshRoleResponseEntity(userProfilePage, objectIds, "objectIds", pageRequest);
    }

    private ResponseEntity<Object> refreshUserProfileBasedOnPersonalCodes(List<String> personalCodes,
                                                                          PageRequest pageRequest) {
        log.info("{} : starting refreshUserProfile BasedOn personalCodes ", loggingComponentName);
        var userProfilePage = userProfileRepository.fetchUserProfileByPersonalCodes(
                personalCodes, pageRequest);
        if (userProfilePage == null || userProfilePage.isEmpty()) {
            log.error("{}:: No data found in JRD for the personalCodes {}",
                    loggingComponentName, personalCodes);
            throw new ResourceNotFoundException(NO_DATA_FOUND);
        }
        return getRefreshRoleResponseEntity(userProfilePage, personalCodes, "personalCodes", pageRequest);
    }



    private ResponseEntity<Object> refreshUserProfileBasedOnSidamIds(List<String> sidamIds,
                                                                     PageRequest pageRequest) {
        log.info("{} : starting refreshUserProfile BasedOn SidamIds ", loggingComponentName);
        var userProfilePage = userProfileRepository.fetchUserProfileBySidamIds(
                sidamIds, pageRequest);
        if (userProfilePage == null || userProfilePage.isEmpty()) {
            log.error("{}:: No data found in JRD for the sidamIds {}",
                    loggingComponentName, sidamIds);
            throw new ResourceNotFoundException(NO_DATA_FOUND);
        }
        return getRefreshRoleResponseEntity(userProfilePage, sidamIds, "sidamIds", pageRequest);
    }

    private ResponseEntity<Object> getRefreshRoleResponseEntity(Page<UserProfile> userProfilePage, Object collection,
                                                                String collectionName,PageRequest pageRequest) {
        log.info("{} : starting getRefresh Role Response Entity ", loggingComponentName);
        var userProfileList = new ArrayList<UserProfileRefreshResponse>();

        var serviceCodeMappings = serviceCodeMappingRepository.findAllServiceCodeMapping();
        log.info("serviceCodeMappings size = {}", serviceCodeMappings.size());
        userProfilePage.forEach(userProfile -> userProfileList.add(
                buildUserProfileRefreshResponseDto(userProfile,serviceCodeMappings)));

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
                .title(v.get(0).getTitle())
                .initials(v.get(0).getInitials())
                .retirementDate(v.get(0).getRetirementDate())
                .activeFlag(v.get(0).getActiveFlag())
                .appointments(v.stream()
                        .flatMap(i -> i.getAppointments().stream())
                        .toList())
                .authorisations(v.stream()
                        .flatMap(i -> i.getAuthorisations().stream())
                        .toList())
                .roles(v.stream()
                                .flatMap(i -> i.getRoles().stream())
                                .toList())
                .build()));

        log.info("userProfileList size = {}", userProfileList.size());

        log.info("{}:: Successfully fetched the User Profile details to refresh role assignment "
                + "for " + collectionName + " {}", loggingComponentName, collection);

        sort(refreshResponse, pageRequest);
        return ResponseEntity
                .ok()
                .header("total_records", String.valueOf(userProfilePage.getTotalElements()))
                .body(refreshResponse);

    }

    @SuppressWarnings("unchecked")
    private void sort(List<UserProfileRefreshResponse> refreshResponses, PageRequest page) {
        Sort.Order order = page.getSort().get().findFirst().orElse(null);

        if (order != null) {
            Comparator<UserProfileRefreshResponse> comparator = Comparator.comparing(report -> {
                try {
                    return (Comparable) new PropertyDescriptor(order.getProperty(), report.getClass())
                            .getReadMethod().invoke(report);
                } catch (Exception e) {
                    log.error("not able to resolve the sorting options");
                    return null;
                }
            });
            if (Sort.Direction.DESC == order.getDirection()) {
                comparator = comparator.reversed();
            }
            refreshResponses.sort(comparator);
        }
    }

    private UserProfileRefreshResponse buildUserProfileRefreshResponseDto(//change here
            UserProfile profile, List<ServiceCodeMapping> serviceCodeMappings) {
        log.info("{} : starting build User Profile Refresh Response Dto ", loggingComponentName);
        return UserProfileRefreshResponse.builder()
                .sidamId(profile.getSidamId())
                .objectId(profile.getObjectId())//change all the fields verifying
                .knownAs(profile.getKnownAs())
                .surname(profile.getSurname())
                .fullName(profile.getFullName())
                .postNominals(profile.getPostNominals())
                .emailId(profile.getEmailId())
                .personalCode(profile.getPersonalCode())
                .title(profile.getTitle())
                .initials(profile.getInitials())
                .retirementDate(null != profile.getRetirementDate() ? String.valueOf(profile.getRetirementDate())
                        : null)
                .activeFlag(String.valueOf(profile.getActiveFlag()))
                .appointments(getAppointmentRefreshResponseList(profile))
                .authorisations(getAuthorisationRefreshResponseList(profile, serviceCodeMappings))
                .roles(getRolesRefreshResponseList(profile))
                .build();
    }

    private List<JudicialRoleTypeRefresh> getRolesRefreshResponseList(
            UserProfile profile) {
        log.info("{} : starting get Appointment Refresh Response List ", loggingComponentName);

        var rolesList = new ArrayList<JudicialRoleTypeRefresh>();

        profile.getJudicialRoleTypes().stream()
                .forEach(roleTypes -> rolesList.add(
                        buildRolesRefreshDto(roleTypes)));
        return rolesList;
    }

    private JudicialRoleTypeRefresh buildRolesRefreshDto(
            JudicialRoleType judicialRoleType) {
        log.info("{} : starting build Authorisation Refresh Response Dto ", loggingComponentName);

        return   JudicialRoleTypeRefresh.builder()
                .title(judicialRoleType.getTitle())
                .jurisdictionRoleId(judicialRoleType.getJurisdictionRoleNameId())
                .startDate(null != judicialRoleType.getStartDate() ? String.valueOf(judicialRoleType.getStartDate())
                        : null)
                .endDate(null != judicialRoleType.getEndDate() ? String.valueOf(judicialRoleType.getEndDate()) : null)
                .build();
    }

    private List<AppointmentRefreshResponse> getAppointmentRefreshResponseList(
            UserProfile profile) {
        log.info("{} : starting get Appointment Refresh Response List ", loggingComponentName);

        var appointmentList = new ArrayList<AppointmentRefreshResponse>();

        profile.getAppointments().stream()
                .forEach(appointment -> appointmentList.add(
                        buildAppointmentRefreshResponseDto(appointment)));
        return appointmentList;
    }

    private AppointmentRefreshResponse buildAppointmentRefreshResponseDto(
            Appointment appt) {
        log.info("{} : starting build Appointment Refresh Response Dto ", loggingComponentName);

        return AppointmentRefreshResponse.builder()
                .baseLocationId(appt.getBaseLocationId())
                .epimmsId(appt.getEpimmsId())
                .cftRegionID(appt.getRegionId())
                .cftRegion(appt.getRegionType().getRegionDescEn())
                .isPrincipalAppointment(String.valueOf(appt.getIsPrincipleAppointment()))
                .appointment(appt.getAppointmentMapping())
                .appointmentType(appt.getAppointmentType())
                .serviceCodes(
                    locationMapppingRepository.fetchServiceCodefromLocationId(appt.getBaseLocationId()))
                .startDate(null != appt.getStartDate() ? String.valueOf(appt.getStartDate()) : null)
                .endDate(null != appt.getEndDate() ? String.valueOf(appt.getEndDate()) : null)
                .appointmentId(appt.getAppointmentId())
                .roleNameId(appt.getRoleNameId())
                .type(appt.getType())
                .contractTypeId(appt.getContractTypeId())
                .build();
    }

    private List<AuthorisationRefreshResponse> getAuthorisationRefreshResponseList(
            UserProfile profile, List<ServiceCodeMapping> serviceCodeMappings) {
        log.info("{} : starting get Authorisation Refresh Response List ", loggingComponentName);

        var authorisationList = new ArrayList<AuthorisationRefreshResponse>();

        profile.getAuthorisations().stream()
                .forEach(authorisation -> authorisationList.add(
                        buildAuthorisationRefreshResponseDto(authorisation, serviceCodeMappings)));

        return authorisationList;
    }

    private AuthorisationRefreshResponse buildAuthorisationRefreshResponseDto(
            Authorisation auth, List<ServiceCodeMapping> serviceCodeMappings) {
        log.info("{} : starting build Authorisation Refresh Response Dto ", loggingComponentName);

        List<String> serviceCode = serviceCodeMappings.stream()
                .filter(s -> s.getTicketCode().equalsIgnoreCase(auth.getTicketCode())
                        && StringUtils.isNotBlank(s.getServiceCode()))
                .map(ServiceCodeMapping::getServiceCode).distinct()
                .toList();

        return AuthorisationRefreshResponse.builder()
                .jurisdiction(auth.getJurisdiction())
                .ticketDescription(auth.getLowerLevel())
                .ticketCode(auth.getTicketCode())
                .serviceCodes(serviceCode)
                .startDate(null != auth.getStartDate() ? String.valueOf(auth.getStartDate()) : null)
                .endDate(null != auth.getEndDate() ? String.valueOf(auth.getEndDate()) : null)
                .appointmentId(auth.getAppointmentId())
                .authorisationId(auth.getAuthorisationId())
                .jurisdictionId(auth.getJurisdictionId())
                .build();
    }
}
