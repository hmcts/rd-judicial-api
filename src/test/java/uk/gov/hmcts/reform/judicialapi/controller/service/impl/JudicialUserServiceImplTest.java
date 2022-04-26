package uk.gov.hmcts.reform.judicialapi.controller.service.impl;


import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.domain.ServiceCodeMapping;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.repository.ServiceCodeMappingRepository;
import uk.gov.hmcts.reform.judicialapi.repository.RegionMappingRepository;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.impl.JudicialUserServiceImpl;
import uk.gov.hmcts.reform.judicialapi.controller.advice.UserProfileException;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.domain.BaseLocationType;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import org.springframework.data.domain.PageRequest;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.domain.RegionType;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;
import uk.gov.hmcts.reform.judicialapi.util.RequestUtils;
import uk.gov.hmcts.reform.judicialapi.validator.RefreshUserValidator;

import static java.nio.charset.Charset.defaultCharset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createUserProfile;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class JudicialUserServiceImplTest {

    @InjectMocks
    JudicialUserServiceImpl judicialUserService;

    @Mock
    UserProfileRepository userProfileRepository;

    @Mock
    ServiceCodeMappingRepository serviceCodeMappingRepository;

    @Mock
    RegionMappingRepository regionMappingRepository;

    @Mock
    private LocationReferenceDataFeignClient locationReferenceDataFeignClient;

    private RefreshUserValidator refreshUserValidatorMock;
    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        refreshUserValidatorMock = new RefreshUserValidator();
        judicialUserService.setRefreshUserValidator(refreshUserValidatorMock);
    }

    @Test
    void shouldFetchJudicialUsers() {
        List<String> sidamIds = new ArrayList<>();
        sidamIds.add("sidamId1");
        sidamIds.add("sidamId2");
        List<UserProfile> userProfiles = new ArrayList<>();
        UserProfile user = createUserProfile();
        userProfiles.add(user);
        Pageable pageable = createPageableObject(0, 10, 10);
        PageImpl<UserProfile> page = new PageImpl<>(userProfiles);

        when(userProfileRepository.findBySidamIdIn(sidamIds,pageable)).thenReturn(page);

        ResponseEntity<Object> responseEntity =
                judicialUserService.fetchJudicialUsers(10,0, sidamIds);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userProfileRepository, times(1)).findBySidamIdIn(any(),any());
    }

    @Test
    void shouldFetchJudicialUsersFailure() {
        List<String> sidamIds = new ArrayList<>();
        sidamIds.add("sidamId1");
        sidamIds.add("sidamId2");
        List<UserProfile> userProfiles = Collections.emptyList();
        Pageable pageable = createPageableObject(0, 10, 10);
        PageImpl<UserProfile> page = new PageImpl<>(userProfiles);

        when(userProfileRepository.findBySidamIdIn(sidamIds,pageable)).thenReturn(page);
        assertThrows(ResourceNotFoundException.class,
            () -> judicialUserService.fetchJudicialUsers(10,0, sidamIds));
    }

    @Test
    void shouldReturn200WhenUserFoundForTheSearchRequestProvided() {
        var userSearchRequest = UserSearchRequest
                .builder()
                .serviceCode("BFA1")
                .location("12456")
                .searchString("Test")
                .build();
        var userProfile = createUserProfile();

        var serviceCodeMapping = ServiceCodeMapping
                .builder()
                .ticketCode("testTicketCode")
                .build();

        when(serviceCodeMappingRepository.findByServiceCodeIgnoreCase(userSearchRequest.getServiceCode()))
                .thenReturn(List.of(serviceCodeMapping));
        when(userProfileRepository.findBySearchString(userSearchRequest.getSearchString().toLowerCase(),
                userSearchRequest.getServiceCode(),userSearchRequest.getLocation(),List.of("testTicketCode")))
                .thenReturn(List.of(userProfile));

        var responseEntity =
                judicialUserService.retrieveUserProfile(userSearchRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userProfileRepository, times(1)).findBySearchString(any(),any(),
                any(), anyList());
    }

    @Test
    void shouldReturn200WithEmptyResponseWhenUserNotFoundForTheSearchRequestProvided() {

        var userSearchRequest = UserSearchRequest
                .builder()
                .location("12456")
                .searchString("Test")
                .build();

        when(userProfileRepository.findBySearchString(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        var responseEntity =
                judicialUserService.retrieveUserProfile(userSearchRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Collections.EMPTY_LIST, responseEntity.getBody());
        verify(userProfileRepository, times(1)).findBySearchString(any(),any(),
                any(), anyList());
    }

    @Test
    void test_refreshUserProfile_Two_Input_01() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, Arrays.asList("test", "test"),null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });

    }

    @Test
    void test_refreshUserProfile_Two_Input_02() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                Arrays.asList("test", "test"), null,null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_Two_Input_03() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("",
                Arrays.asList("test", "test"), Arrays.asList("test", "test"),null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_Two_Input_04() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("",
                Arrays.asList("test", "test"), null,Arrays.asList("test", "test"));
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_Multiple_Input() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                Arrays.asList("test", "test"), Arrays.asList("test", "test"),null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_No_Input() throws JsonProcessingException {
        checkAssertion("");
    }

    @Test
    void test_refreshUserProfile_WhenCcdServiceNameContainComma() throws JsonProcessingException {
        checkAssertion("abc,def");
    }

    @Test
    void test_refreshUserProfile_WhenCcdServiceNameContainAll() throws JsonProcessingException {
        checkAssertion(" all ");
    }

    private void checkAssertion(String ccdServiceNames) {
        var refreshRoleRequest = new RefreshRoleRequest(ccdServiceNames,
                null, null,null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_BasedOnSidamIds_200() throws JsonProcessingException {
        var userProfile = buildUserProfile();

        var pageRequest = getPageRequest();
        var page = new PageImpl<>(Collections.singletonList(userProfile));
        when(userProfileRepository.fetchUserProfileBySidamIds(List.of("test", "test"), pageRequest))
                .thenReturn(page);
        var refreshRoleRequest = new RefreshRoleRequest("",
                null, Arrays.asList("test", "test"),null);
        var responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");

        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    void test_refreshUserProfile_BasedOnObjectIds_200() throws JsonProcessingException {
        var userProfile = buildUserProfile();

        var pageRequest = getPageRequest();
        var page = new PageImpl<>(Collections.singletonList(userProfile));
        when(userProfileRepository.fetchUserProfileByObjectIds(List.of("test", "test"), pageRequest))
                .thenReturn(page);
        var refreshRoleRequest = new RefreshRoleRequest("",
                Arrays.asList("test", "test"), null,null);
        var responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");

        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    void test_refreshUserProfile_BasedOnPersonalCodes_200() throws JsonProcessingException {
        var userProfile = buildUserProfile();

        var pageRequest = getPageRequest();
        var page = new PageImpl<>(Collections.singletonList(userProfile));
        when(userProfileRepository.fetchUserProfileByPersonalCodes(List.of("Emp", "Emp"), pageRequest))
                .thenReturn(page);
        var refreshRoleRequest = new RefreshRoleRequest("",
                null, null, Arrays.asList("Emp", "Emp", null));
        var responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");

        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    void test_refreshUserProfile_BasedOnPersonalCodes_400() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("",
                null, null, Arrays.asList("Emp", "Emp", null));

        Assertions.assertThrows(InvalidRequestException.class, () -> {
            var responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, -1,
                    0, "ASC", "objectId");

        });
    }

    @Test
    void test_refreshUserProfile_BasedOnPersonalCodes_404() throws JsonProcessingException {
        var userProfile = buildUserProfile();

        var pageRequest = getPageRequest();
        var page = new PageImpl<>(Collections.singletonList(userProfile));
        when(userProfileRepository.fetchUserProfileByPersonalCodes(List.of("Emp", "Emp"), pageRequest))
                .thenReturn(null);
        var refreshRoleRequest = new RefreshRoleRequest("",
                null, null, Arrays.asList("Emp", "Emp"));
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            var responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_BasedOnCcdServiceNames_200() throws JsonProcessingException {
        var lrdOrgInfoServiceResponse = new LrdOrgInfoServiceResponse();
        lrdOrgInfoServiceResponse.setServiceCode("BFA1");
        lrdOrgInfoServiceResponse.setCcdServiceName("cmc");
        var body = mapper.writeValueAsString(List.of(lrdOrgInfoServiceResponse));

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());

        var userProfile = buildUserProfile();

        var pageRequest = getPageRequest();

        var page = new PageImpl<>(Collections.singletonList(userProfile));

        when(serviceCodeMappingRepository.fetchTicketCodeFromServiceCode(Set.of("BFA1"))).thenReturn(List.of("386"));
        when(userProfileRepository.fetchUserProfileByServiceNames(Set.of("BFA1"), List.of("386"), pageRequest))
                .thenReturn(page);
        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null,null);
        var responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");

        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    void test_refreshUserProfile_BasedOnCcdServiceNames_when_LrdResponse_IsNon_200() {

        var pageRequest = getPageRequest();
        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body("body", defaultCharset()).status(400).build());

        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null,null);
        Assertions.assertThrows(UserProfileException.class, () -> {
            var responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });

    }

    @Test
    void test_refreshUserProfile_BasedOnCcdServiceNames_when_Response_Empty() throws JsonProcessingException {

        var lrdOrgInfoServiceResponse = new LrdOrgInfoServiceResponse();
        lrdOrgInfoServiceResponse.setServiceCode("BFA1");
        lrdOrgInfoServiceResponse.setCcdServiceName("cmc");
        var body = mapper.writeValueAsString(List.of(lrdOrgInfoServiceResponse));

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());

        var pageRequest = getPageRequest();

        var page = new PageImpl<UserProfile>(Collections.emptyList());
        when(userProfileRepository.fetchUserProfileByServiceNames(Set.of("BFA1"), List.of("386"), pageRequest))
                .thenReturn(page);
        when(serviceCodeMappingRepository.fetchTicketCodeFromServiceCode(Set.of("BFA1"))).thenReturn(List.of("386"));
        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null,null);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            var responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_BasedOnCcdServiceNames_when_LrdResponseIsEmpty()
            throws JsonProcessingException {

        var body = mapper.writeValueAsString(Collections.emptyList());

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());
        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null,null);

        Assertions.assertThrows(UserProfileException.class, () -> {
            var responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_BasedOnCcdServiceNames_when_LrdResponseReturns400()
            throws JsonProcessingException {
        var errorResponse = ErrorResponse
                .builder()
                .errorCode(400)
                .errorDescription("testErrorDesc")
                .errorMessage("testErrorMsg")
                .build();
        var body = mapper.writeValueAsString(errorResponse);

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(400).build());
        var pageRequest = getPageRequest();
        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null,null);

        Assertions.assertThrows(UserProfileException.class, () -> {
            var responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_BasedOn_All_400() throws JsonProcessingException {
        var refreshRoleRequest = new RefreshRoleRequest("", null, null,null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            var responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @NotNull
    private PageRequest getPageRequest() {
        return RequestUtils.validateAndBuildPaginationObject(1, 0,
                "ASC", "objectId",
                20, "id", UserProfile.class);
    }

    UserProfile buildUserProfile() {

        var baseLocationType = new BaseLocationType();
        baseLocationType.setBaseLocationId("123");

        var regionType = new RegionType();
        regionType.setRegionId("1");
        regionType.setRegionDescCy("National");
        regionType.setRegionDescEn("National");

        var appointment = new Appointment();
        appointment.setPerId("1");
        appointment.setOfficeAppointmentId(1L);
        appointment.setIsPrincipleAppointment(true);
        appointment.setStartDate(LocalDate.now());
        appointment.setEndDate(LocalDate.now());
        appointment.setActiveFlag(true);
        appointment.setExtractedDate(LocalDateTime.now());
        appointment.setCreatedDate(LocalDateTime.now());
        appointment.setLastLoadedDate(LocalDateTime.now());
        appointment.setBaseLocationType(baseLocationType);
        appointment.setRegionType(regionType);

        var authorisation = new Authorisation();
        authorisation.setPerId("1");
        authorisation.setOfficeAuthId(1L);
        authorisation.setJurisdiction("Languages");
        authorisation.setTicketId(29611L);
        authorisation.setStartDate(LocalDateTime.now());
        authorisation.setEndDate(LocalDateTime.now());
        authorisation.setCreatedDate(LocalDateTime.now());
        authorisation.setLastUpdated(LocalDateTime.now());
        authorisation.setLowerLevel("Welsh");
        authorisation.setPersonalCode("");

        var judicialRoleType = new JudicialRoleType();
        judicialRoleType.setRoleId("1");
        judicialRoleType.setPerId("1");
        judicialRoleType.setTitle("Test1");
        judicialRoleType.setLocation("west");

        var judicialRoleType1 = new JudicialRoleType();
        judicialRoleType1.setRoleId("2");
        judicialRoleType1.setPerId("1");
        judicialRoleType1.setTitle("Test2");
        judicialRoleType1.setLocation("east");
        judicialRoleType1.setEndDate(LocalDateTime.now().minusDays(3));

        var judicialRoleType2 = new JudicialRoleType();
        judicialRoleType2.setRoleId("3");
        judicialRoleType2.setPerId("1");
        judicialRoleType2.setTitle("Test3");
        judicialRoleType2.setLocation("north");
        judicialRoleType2.setEndDate(LocalDateTime.now().plusDays(3));

        var userProfile = new UserProfile();
        userProfile.setPerId("1");
        userProfile.setPersonalCode("Emp");
        userProfile.setKnownAs("TestEmp");
        userProfile.setSurname("Test");
        userProfile.setFullName("Test1");
        userProfile.setPostNominals("Test Test1");
        userProfile.setWorkPattern("temp");
        userProfile.setEjudiciaryEmailId("abc@gmail.com");
        userProfile.setJoiningDate(LocalDate.now());
        userProfile.setLastWorkingDate(LocalDate.now());
        userProfile.setActiveFlag(true);
        userProfile.setExtractedDate(LocalDateTime.now());
        userProfile.setCreatedDate(LocalDateTime.now());
        userProfile.setLastLoadedDate(LocalDateTime.now());
        userProfile.setObjectId("");
        userProfile.setSidamId("4c0ff6a3-8fd6-803b-301a-29d9dacccca8");

        authorisation.setUserProfile(userProfile);

        userProfile.setAppointments(List.of(appointment));
        userProfile.setAuthorisations(List.of(authorisation));
        userProfile.setJudicialRoleTypes(List.of(judicialRoleType,judicialRoleType1,judicialRoleType2));

        return userProfile;
    }

}
