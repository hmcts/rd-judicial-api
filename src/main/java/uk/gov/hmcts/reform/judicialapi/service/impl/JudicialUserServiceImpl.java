package uk.gov.hmcts.reform.judicialapi.service.impl;

import feign.Response;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfileWithServiceName;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;
import uk.gov.hmcts.reform.judicialapi.util.RefDataConstants;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;

@Slf4j
@Service
@Setter
public class JudicialUserServiceImpl implements JudicialUserService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private LocationReferenceDataFeignClient locationReferenceDataFeignClient;

    @Value("${defaultPageSize}")
    Integer defaultPageSize;

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

    /**
     * Returns the User Profile for Refresh Assignments.
     */
    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<Object> fetchUserProfileByServiceNames(String serviceNames, PageRequest pageRequest) {
        Response lrdOrgInfoServiceResponse =
                locationReferenceDataFeignClient.getLocationRefServiceMapping(serviceNames);
        HttpStatus httpStatus = HttpStatus.valueOf(lrdOrgInfoServiceResponse.status());
        if (httpStatus.is2xxSuccessful()) {
            ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toResponseEntityWithListBody(
                    lrdOrgInfoServiceResponse, LrdOrgInfoServiceResponse.class);
            List<LrdOrgInfoServiceResponse> listLrdServiceMapping =
                    (List<LrdOrgInfoServiceResponse>) responseEntity.getBody();
            if (!CollectionUtils.isEmpty(listLrdServiceMapping)) {

                Map<String, String> serviceNameToCodeMapping =
                        listLrdServiceMapping
                                .stream()
                                .filter(r -> StringUtils.isNotBlank(r.getServiceCode())
                                        && StringUtils.isNotBlank(r.getCcdServiceName()))
                                .collect(Collectors.toMap(LrdOrgInfoServiceResponse::getServiceCode,
                                        LrdOrgInfoServiceResponse::getCcdServiceName));

                Page<UserProfile> userProfilePage = userProfileRepository.fetchUserProfileByServiceNames(
                        serviceNameToCodeMapping.keySet(), pageRequest);

                if (userProfilePage.isEmpty()) {
                    log.error("{}:: No data found in JRD for the service name {}",
                            loggingComponentName, serviceNames);
                    throw new ResourceNotFoundException(RefDataConstants.NO_DATA_FOUND);
                }

                List<UserProfileWithServiceName> userProfileList = new ArrayList<>();
                Set<Authorisation> authorisations = new LinkedHashSet<>();

                userProfilePage.forEach(userProfile -> userProfile.getAuthorisations()
                        .stream()
                        .filter(auth -> serviceNameToCodeMapping.containsKey(auth.getServiceCode()))
                        .forEach(authorisations::add));

                authorisations.forEach(authorisation -> userProfileList.add(
                        UserProfileWithServiceName.builder()
                                .serviceName(serviceNameToCodeMapping.get(authorisation.getServiceCode()))
                                .userProfile(buildUserProfileDto(authorisation.getUserProfile()))
                                .build()));
                log.info("{}:: Successfully fetched the User Profile details to refresh role assignment "
                        + "for service names {}", loggingComponentName, serviceNames);
                return ResponseEntity
                        .ok()
                        .header("total_records", String.valueOf(userProfilePage.getTotalElements()))
                        .body(userProfileList);

            }
        }

        log.error("{}:: Error in getting the data from LRD for the service name {} :: Status code {}",
                loggingComponentName, serviceNames, httpStatus);
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


    private uk.gov.hmcts.reform.judicialapi.domain.UserProfile buildUserProfileDto(
            UserProfile profile) {
        return uk.gov.hmcts.reform.judicialapi.domain.UserProfile.builder()
                .perId(profile.getPerId())
                .personalCode(profile.getPersonalCode())
                .appointment(profile.getAppointment())
                .knownAs(profile.getKnownAs())
                .surname(profile.getSurname())
                .fullName(profile.getFullName())
                .postNominals(profile.getPostNominals())
                .appointmentType(profile.getAppointmentType())
                .workPattern(profile.getWorkPattern())
                .ejudiciaryEmailId(profile.getEjudiciaryEmailId())
                .joiningDate(profile.getJoiningDate())
                .lastWorkingDate(profile.getLastWorkingDate())
                .activeFlag(profile.getActiveFlag())
                .extractedDate(profile.getExtractedDate())
                .createdDate(profile.getCreatedDate())
                .lastLoadedDate(profile.getLastLoadedDate())
                .objectId(profile.getObjectId())
                .sidamId(profile.getSidamId())
                .build();
    }

}
