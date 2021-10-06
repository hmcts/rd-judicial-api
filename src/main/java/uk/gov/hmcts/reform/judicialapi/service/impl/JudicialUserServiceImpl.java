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
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.RefreshRoleResponse;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.domain.BaseLocationType;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;
import uk.gov.hmcts.reform.judicialapi.util.RefDataConstants;
import uk.gov.hmcts.reform.judicialapi.util.RequestUtils;
import uk.gov.hmcts.reform.judicialapi.validator.RefreshUserValidator;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserProfileRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.AppointmentRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.BaseLocationTypeRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.AuthorisationRefreshResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;

@Slf4j
@Service
@Setter
public class JudicialUserServiceImpl implements JudicialUserService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Value("${defaultPageSize}")
    Integer defaultPageSize;

    @Value("${refresh.pageSize}")
    private int configPageSize;

    @Value("${refresh.sortColumn}")
    private String configSortColumn;

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
    @SuppressWarnings("unchecked")
    public ResponseEntity<Object> refreshUserProfile(RefreshRoleRequest refreshRoleRequest, Integer pageSize,
                                                     Integer pageNumber, String sortDirection, String sortColumn) {

        refreshUserValidator.shouldContainOnlyOneInputParameter(refreshRoleRequest);
        PageRequest pageRequest = RequestUtils.validateAndBuildPaginationObject(pageSize, pageNumber,
                sortDirection, sortColumn, configPageSize, configSortColumn,
                UserProfile.class);

        if (refreshRoleRequest != null) {
            if (refreshUserValidator.isNotEmptyOrNull(refreshRoleRequest.getSidamIds())) {
                return refreshUserProfileBasedOnSidamIds(refreshRoleRequest.getSidamIds(), pageRequest);
            } else if (refreshUserValidator.isNotEmptyOrNull(refreshRoleRequest.getObjectIds())) {
                return refreshUserProfileBasedOnObjectIds(refreshRoleRequest.getObjectIds(), pageRequest);
            } else if (refreshUserValidator.isCcdServiceNamesNotEmptyOrNull(refreshRoleRequest.getCcdServiceNames())) {
                return refreshUserProfileBasedOnCcdServiceNames(refreshRoleRequest.getCcdServiceNames(), pageRequest);
            } else {
                return refreshUserProfileBasedOnAll(pageRequest);
            }
        } else {
            return refreshUserProfileBasedOnAll(pageRequest);
        }
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> refreshUserProfileBasedOnObjectIds(List<String> objectIds,
                                                                      PageRequest pageRequest) {
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
        Page<UserProfile> userProfilePage = userProfileRepository.fetchUserProfileByAll(pageRequest);
        if (userProfilePage.isEmpty()) {
            log.error("{}:: No data found in JRD {}", loggingComponentName);
            throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
        }
        return getRefreshRoleResponseEntity(userProfilePage, null, "");
    }

    private ResponseEntity<Object> getRefreshRoleResponseEntity(Page<UserProfile> userProfilePage,
                                                                Collection<?> collection, String collectionName) {
        List<RefreshRoleResponse> userProfileList = new ArrayList<>();

        userProfilePage.forEach(userProfile -> userProfileList.add(
                RefreshRoleResponse.builder()
                        .userProfileRefreshResponse(buildUserProfileRefreshResponseDto(
                                userProfile, new HashSet<>(userProfile.getAuthorisations())))
                        .build()));


        log.info("{}:: Successfully fetched the User Profile details to refresh role assignment "
                + "for" + collectionName + " {}", loggingComponentName, collection);
        return ResponseEntity
                .ok()
                .header("total_records", String.valueOf(userProfilePage.getTotalElements()))
                .body(userProfileList);

    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> refreshUserProfileBasedOnCcdServiceNames(String ccdServiceNames,
                                                                            PageRequest pageRequest) {
        Response lrdOrgInfoServiceResponse =
                locationReferenceDataFeignClient.getLocationRefServiceMapping(ccdServiceNames);
        HttpStatus httpStatus = HttpStatus.valueOf(lrdOrgInfoServiceResponse.status());

        if (httpStatus.is2xxSuccessful()) {
            ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toResponseEntityWithListBody(
                    lrdOrgInfoServiceResponse, LrdOrgInfoServiceResponse.class);

            List<LrdOrgInfoServiceResponse> listLrdServiceMapping =
                    (List<LrdOrgInfoServiceResponse>) responseEntity.getBody();

            if (!listLrdServiceMapping.isEmpty()) {

                Map<String, String> ccdServiceNameToCodeMapping =
                        listLrdServiceMapping
                                .stream()
                                .filter(r -> StringUtils.isNotBlank(r.getServiceCode())
                                        && StringUtils.isNotBlank(r.getCcdServiceName()))
                                .collect(Collectors.toMap(LrdOrgInfoServiceResponse::getServiceCode,
                                        LrdOrgInfoServiceResponse::getCcdServiceName));

                Page<UserProfile> userProfilePage = userProfileRepository.fetchUserProfileByServiceNames(
                        ccdServiceNameToCodeMapping.keySet(), pageRequest);

                if (userProfilePage.isEmpty()) {
                    log.error("{}:: No data found in JRD for the ccdServiceNames {}",
                            loggingComponentName, ccdServiceNames);
                    throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
                }

                List<RefreshRoleResponse> userProfileList = new ArrayList<>();
                Set<Authorisation> authorisations = new LinkedHashSet<>();

                userProfilePage.forEach(userProfile -> userProfile.getAuthorisations()
                        .stream()
                        .filter(auth -> ccdServiceNameToCodeMapping.containsKey(auth.getServiceCode()))
                        .forEach(authorisations::add));

                authorisations.forEach(authorisation -> userProfileList.add(
                        RefreshRoleResponse.builder()
                                .userProfileRefreshResponse(buildUserProfileRefreshResponseDto(
                                        authorisation.getUserProfile(), authorisations))
                                .build()));


                log.info("{}:: Successfully fetched the User Profile details to refresh role assignment "
                        + "for ccdServiceNames {}", loggingComponentName, ccdServiceNames);
                return ResponseEntity
                        .ok()
                        .header("total_records", String.valueOf(userProfilePage.getTotalElements()))
                        .body(userProfileList);

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

    private UserProfileRefreshResponse buildUserProfileRefreshResponseDto(UserProfile profile,
                                                                          Set<Authorisation> authorisations) {
        return UserProfileRefreshResponse.builder()
                .sidamId(profile.getSidamId())
                .objectId(profile.getObjectId())
                .knownAs(profile.getKnownAs())
                .surname(profile.getSurname())
                .fullName(profile.getFullName())
                .postNominals(profile.getPostNominals())
                .ejudiciaryEmailId(profile.getEjudiciaryEmailId())
                /*.personalCode(profile.getPersonalCode())
                .appointment(profile.getAppointment())
                .appointmentType(profile.getAppointmentType())
                .workPattern(profile.getWorkPattern())
                .joiningDate(profile.getJoiningDate())
                .lastWorkingDate(profile.getLastWorkingDate())
                .activeFlag(profile.getActiveFlag())
                .extractedDate(profile.getExtractedDate())
                .createdDate(profile.getCreatedDate())
                .lastLoadedDate(profile.getLastLoadedDate())*/
                .appointments(getAppointmentRefreshResponseList(profile.getAppointments()))
                .authorisations(getAuthorisationRefreshResponseList(authorisations, profile))
                .build();
    }

    private List<AppointmentRefreshResponse> getAppointmentRefreshResponseList(List<Appointment> appointments) {

        List<AppointmentRefreshResponse> appointmentList
                = new ArrayList<>();
        appointments.forEach(appt -> {
            appointmentList.add(buildAppointmentRefreshResponseDto(appt));
        });
        return appointmentList;
    }

    private AppointmentRefreshResponse buildAppointmentRefreshResponseDto(Appointment appt) {
        return AppointmentRefreshResponse.builder()
                .baseLocationType(buildBaseLocationRefreshResponseDTO(appt.getBaseLocationType()))
                .officeAppointmentId(appt.getOfficeAppointmentId())
                .isPrincipleAppointment(appt.getIsPrincipleAppointment())
                .startDate(appt.getStartDate())
                .createdDate(appt.getCreatedDate())
                .lastLoadedDate(appt.getLastLoadedDate())
                .endDate(appt.getEndDate())
                .activeFlag(appt.getActiveFlag())
                .personalCode(appt.getPersonalCode())
                .extractedDate(appt.getExtractedDate())
                .build();
    }

    private BaseLocationTypeRefreshResponse buildBaseLocationRefreshResponseDTO(BaseLocationType baseLocationType) {
        return BaseLocationTypeRefreshResponse.builder()
                .baseLocationId(baseLocationType.getBaseLocationId())
                .courtName(baseLocationType.getCourtName())
                .courtType(baseLocationType.getCourtType())
                .circuit(baseLocationType.getCircuit())
                .areaOfExpertise(baseLocationType.getAreaOfExpertise())
                .build();
    }

    private List<AuthorisationRefreshResponse> getAuthorisationRefreshResponseList(Set<Authorisation> authorisations,
                                                                                   UserProfile profile) {
        List<AuthorisationRefreshResponse> authorisationList = new ArrayList<>();

        authorisations.stream().filter(auth -> auth.getPerId().equals(profile.getPerId()))
                .forEach(authorisation -> authorisationList.add(buildAuthorisationRefreshResponseDto(authorisation)));

        return authorisationList;
    }

    private AuthorisationRefreshResponse buildAuthorisationRefreshResponseDto(Authorisation auth) {
        return AuthorisationRefreshResponse.builder()
                .officeAuthId(auth.getOfficeAuthId())
                .jurisdiction(auth.getJurisdiction())
                .lowerLevel(auth.getLowerLevel())
                .serviceCode(auth.getServiceCode())
                .startDate(auth.getStartDate())
                .endDate(auth.getEndDate())
                .createdDate(auth.getCreatedDate())
                .lastUpdated(auth.getLastUpdated())
                .ticketId(auth.getTicketId())
                .personalCode(auth.getPersonalCode())
                .build();
    }
}
