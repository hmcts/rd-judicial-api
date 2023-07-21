package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.response.AppointmentRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.AuthorisationRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.RegionMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ServiceCodeMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.RegionMappingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserProfileRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinkUserService;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ServiceCodeMappingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RequestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.validator.RefreshUserValidator;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.LOCATION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.REGION;

@Slf4j
@Service
@Setter
public class ElinkUserServiceImpl implements ElinkUserService {

    @Autowired
    private ProfileRepository userProfileRepository;

    @Autowired
    private ServiceCodeMappingRepository serviceCodeMappingRepository;

    @Autowired
    private RegionMappingRepository regionMappingRepository;

    @Value("${search.serviceCode}")
    private List<String> searchServiceCode;

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @Value("${refresh.pageSize}")
    private int refreshDefaultPageSize;

    @Value("${refresh.sortColumn}")
    private String refreshDefaultSortColumn;



    @Autowired
    private RefreshUserValidator refreshUserValidator;

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
          //  return refreshUserProfileBasedOnCcdServiceNames(refreshRoleRequest.getCcdServiceNames(), pageRequest);
        } else if (refreshUserValidator.isListNotEmptyOrNotNull(refreshRoleRequest.getSidamIds())) {
          //  return refreshUserProfileBasedOnSidamIds(
                 //   refreshUserValidator.removeEmptyOrNullFromList(refreshRoleRequest.getSidamIds()), pageRequest);
        } else if (refreshUserValidator.isListNotEmptyOrNotNull(refreshRoleRequest.getObjectIds())) {
            return refreshUserProfileBasedOnObjectIds(
                    refreshUserValidator.removeEmptyOrNullFromList(refreshRoleRequest.getObjectIds()), pageRequest);
        } else if (refreshUserValidator.isListNotEmptyOrNotNull(refreshRoleRequest.getPersonalCodes())) {
         //   return refreshUserProfileBasedOnPersonalCodes(refreshUserValidator.removeEmptyOrNullFromList(
           ///         refreshRoleRequest.getPersonalCodes()), pageRequest);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


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

    private ResponseEntity<Object> getRefreshRoleResponseEntity(Page<UserProfile> userProfilePage,
                                                                Object collection, String collectionName) {
        log.info("{} : starting getRefresh Role Response Entity ", loggingComponentName);
        var userProfileList = new ArrayList<UserProfileRefreshResponse>();//change here ...

        var serviceCodeMappings = serviceCodeMappingRepository.findAllServiceCodeMapping();//check here
        log.info("serviceCodeMappings size = {}", serviceCodeMappings.size());

        var regionMappings = regionMappingRepository.findAllRegionMappingData(); // check here
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

    private UserProfileRefreshResponse buildUserProfileRefreshResponseDto(//change here
            UserProfile profile, List<ServiceCodeMapping> serviceCodeMappings, List<RegionMapping> regionMappings) {
        log.info("{} : starting build User Profile Refresh Response Dto ", loggingComponentName);
        return UserProfileRefreshResponse.builder()
                .sidamId(profile.getSidamId())
                .objectId(profile.getObjectId())//change all the fields verifying
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


    private List<AppointmentRefreshResponse> getAppointmentRefreshResponseList( // change in appointment refresh response
            UserProfile profile, List<RegionMapping> regionMappings) {
        log.info("{} : starting get Appointment Refresh Response List ", loggingComponentName);

        var appointmentList = new ArrayList<AppointmentRefreshResponse>();

        profile.getAppointments().stream()
                .forEach(appointment -> appointmentList.add(
                        buildAppointmentRefreshResponseDto(appointment, profile, regionMappings)));
        return appointmentList;
    }

    private AppointmentRefreshResponse buildAppointmentRefreshResponseDto( //change here
            Appointment appt, UserProfile profile, List<RegionMapping> regionMappings) {
        log.info("{} : starting build Appointment Refresh Response Dto ", loggingComponentName);

        RegionMapping regionMapping = regionMappings.stream()
                .filter(rm -> rm.getJrdRegionId().equalsIgnoreCase(appt.getRegionId()))
                .findFirst()
                .orElse(null);

        RegionMapping regionCircuitMapping = regionMappings.stream()
                .filter(rm -> rm.getRegion().equalsIgnoreCase(appt.getBaseLocationType().getCircuit()))
                .findFirst()
                .orElse(null);


        return AppointmentRefreshResponse.builder()
                .baseLocationId(appt.getBaseLocationId())
                .epimmsId(appt.getEpimmsId())
                .courtName(appt.getBaseLocationType().getCourtName())
                .cftRegionID(getRegionId(appt.getEpimmsId(),regionMapping,regionCircuitMapping,REGION))
                .cftRegion(getRegion(appt.getEpimmsId(),regionMapping,regionCircuitMapping,REGION))
                .locationId(getRegionId(appt.getEpimmsId(),regionMapping,regionCircuitMapping,LOCATION))
                .location(getRegion(appt.getEpimmsId(),regionMapping,regionCircuitMapping,LOCATION))
                .isPrincipalAppointment(String.valueOf(appt.getIsPrincipleAppointment()))
                .appointment(appt.getAppointment())
                .appointmentType(appt.getAppointmentType())
                .roles(getRoleIdList(profile.getJudicialRoleTypes()))
                .startDate(null != appt.getStartDate() ? String.valueOf(appt.getStartDate()) : null)
                .endDate(null != appt.getEndDate() ? String.valueOf(appt.getEndDate()) : null)
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

    private String getRegionId(String epimmsId, RegionMapping regionMapping, RegionMapping regionCircuitMapping,
                               String type) {

        if ((epimmsId == null || epimmsId.isEmpty())) {
            if (LOCATION.equalsIgnoreCase(type)) {
                return null != regionMapping ? regionMapping.getJrdRegionId() : null;
            } else {
                return null != regionMapping ? regionMapping.getRegionId() : null;
            }
        } else {
            return null != regionCircuitMapping ? regionCircuitMapping.getRegionId() : null;
        }
    }

    private String getRegion(String epimmsId,RegionMapping regionMapping,RegionMapping regionCircuitMapping,
                             String type) {

        if ((epimmsId == null || epimmsId.isEmpty())) {
            if (LOCATION.equalsIgnoreCase(type)) {
                return null != regionMapping ? regionMapping.getJrdRegion() : null;
            } else {
                return null != regionMapping ? regionMapping.getRegion() : null;
            }
        } else {
            return null != regionCircuitMapping ? regionCircuitMapping.getRegion() : null;
        }
    }

    private List<String> getRoleIdList(List<JudicialRoleType> judicialRoleTypes) {
        log.info("{} : starting get RoleId List ", loggingComponentName);
        return judicialRoleTypes.stream()
                .filter(e -> e.getEndDate() == null || !e.getEndDate().toLocalDate().isBefore(LocalDate.now()))
                .map(JudicialRoleType::getTitle).toList();
    }


}
