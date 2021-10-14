package uk.gov.hmcts.reform.judicialapi.controller.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.controller.advice.UserProfileException;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.domain.BaseLocationType;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.impl.JudicialUserServiceImpl;
import uk.gov.hmcts.reform.judicialapi.util.RequestUtils;
import uk.gov.hmcts.reform.judicialapi.validator.RefreshUserValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createUserProfile;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;

@RunWith(MockitoJUnitRunner.class)
public class JudicialUserServiceImplTest {

    @InjectMocks
    JudicialUserServiceImpl judicialUserService;

    @Mock
    UserProfileRepository userProfileRepository;

    @Mock
    private LocationReferenceDataFeignClient locationReferenceDataFeignClient;

    private RefreshUserValidator refreshUserValidatorMock;
    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
        refreshUserValidatorMock = new RefreshUserValidator();
        judicialUserService.setRefreshUserValidator(refreshUserValidatorMock);
    }

    @Test
    public void shouldFetchJudicialUsers() {
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
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userProfileRepository, times(1)).findBySidamIdIn(any(),any());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldFetchJudicialUsersFailure() {
        List<String> sidamIds = new ArrayList<>();
        sidamIds.add("sidamId1");
        sidamIds.add("sidamId2");
        List<UserProfile> userProfiles = Collections.emptyList();
        Pageable pageable = createPageableObject(0, 10, 10);
        PageImpl<UserProfile> page = new PageImpl<>(userProfiles);

        when(userProfileRepository.findBySidamIdIn(sidamIds,pageable)).thenReturn(page);
        judicialUserService.fetchJudicialUsers(10,0, sidamIds);
    }

    @Test
    public void shouldReturn200WhenUserFoundForTheSearchRequestProvided() {
        var userSearchRequest = UserSearchRequest
                .builder()
                .serviceCode("BFA1")
                .location("12456")
                .searchString("Test")
                .build();
        var userProfile = createUserProfile();


        when(userProfileRepository.findBySearchString(any(), any(), any()))
                .thenReturn(List.of(userProfile));

        var responseEntity =
                judicialUserService.retrieveUserProfile(userSearchRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userProfileRepository, times(1)).findBySearchString(any(),any(), any());
    }

    @Test
    public void shouldReturn404WhenUserNotFoundForTheSearchRequestProvided() {

        var userSearchRequest = UserSearchRequest
                .builder()
                .serviceCode("BFA1")
                .location("12456")
                .searchString("Test")
                .build();

        when(userProfileRepository.findBySearchString(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                judicialUserService.retrieveUserProfile(userSearchRequest));

    }

    @Test(expected = InvalidRequestException.class)
    public void test_refreshUserProfile_Two_Input_01() throws JsonProcessingException {

        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, Arrays.asList("test", "test"));
        judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");
    }

    @Test(expected = InvalidRequestException.class)
    public void test_refreshUserProfile_Two_Input_02() throws JsonProcessingException {

        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("cmc",
                Arrays.asList("test", "test"), null);
        judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");
    }

    @Test(expected = InvalidRequestException.class)
    public void test_refreshUserProfile_Two_Input_03() throws JsonProcessingException {

        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("",
                Arrays.asList("test", "test"), Arrays.asList("test", "test"));
        judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");
    }

    @Test(expected = InvalidRequestException.class)
    public void test_refreshUserProfile_Multiple_Input() throws JsonProcessingException {

        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("cmc",
                Arrays.asList("test", "test"), Arrays.asList("test", "test"));
        judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");
    }

    @Test
    public void test_refreshUserProfile_BasedOnSidamIds_200() throws JsonProcessingException {
        UserProfile userProfile = buildUserProfile();

        PageRequest pageRequest = getPageRequest();
        PageImpl<UserProfile> page = new PageImpl<>(Collections.singletonList(userProfile));
        when(userProfileRepository.fetchUserProfileBySidamIds(List.of("test", "test"), pageRequest))
                .thenReturn(page);
        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("",
                null, Arrays.asList("test", "test"));
        ResponseEntity<Object> responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");

        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void test_refreshUserProfile_BasedOnObjectIds_200() throws JsonProcessingException {
        UserProfile userProfile = buildUserProfile();

        PageRequest pageRequest = getPageRequest();
        PageImpl<UserProfile> page = new PageImpl<>(Collections.singletonList(userProfile));
        when(userProfileRepository.fetchUserProfileByObjectIds(List.of("test", "test"), pageRequest))
                .thenReturn(page);
        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("",
                Arrays.asList("test", "test"), null);
        ResponseEntity<Object> responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");

        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void test_refreshUserProfile_BasedOnCcdServiceNames_200() throws JsonProcessingException {
        LrdOrgInfoServiceResponse lrdOrgInfoServiceResponse = new LrdOrgInfoServiceResponse();
        lrdOrgInfoServiceResponse.setServiceCode("BFA1");
        lrdOrgInfoServiceResponse.setCcdServiceName("cmc");
        String body = mapper.writeValueAsString(List.of(lrdOrgInfoServiceResponse));

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());

        UserProfile userProfile = buildUserProfile();

        PageRequest pageRequest = getPageRequest();

        PageImpl<UserProfile> page = new PageImpl<>(Collections.singletonList(userProfile));
        when(userProfileRepository.fetchUserProfileByServiceNames(Set.of("BFA1"), pageRequest))
                .thenReturn(page);
        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null);
        ResponseEntity<Object> responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");

        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test(expected = UserProfileException.class)
    public void test_refreshUserProfile_BasedOnCcdServiceNames_when_LrdResponse_IsNon_200() {

        PageRequest pageRequest = getPageRequest();
        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body("body", defaultCharset()).status(400).build());

        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null);
        ResponseEntity<Object> responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");

    }

    @Test(expected = ResourceNotFoundException.class)
    public void test_refreshUserProfile_BasedOnCcdServiceNames_when_Response_Empty() throws JsonProcessingException {

        LrdOrgInfoServiceResponse lrdOrgInfoServiceResponse = new LrdOrgInfoServiceResponse();
        lrdOrgInfoServiceResponse.setServiceCode("BFA1");
        lrdOrgInfoServiceResponse.setCcdServiceName("cmc");
        String body = mapper.writeValueAsString(List.of(lrdOrgInfoServiceResponse));

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());

        PageRequest pageRequest = getPageRequest();

        PageImpl<UserProfile> page = new PageImpl<>(Collections.emptyList());
        when(userProfileRepository.fetchUserProfileByServiceNames(Set.of("BFA1"), pageRequest))
                .thenReturn(page);
        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null);
        ResponseEntity<Object> responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");
    }

    @Test(expected = UserProfileException.class)
    public void test_refreshUserProfile_BasedOnCcdServiceNames_when_LrdResponseIsEmpty()
            throws JsonProcessingException {

        String body = mapper.writeValueAsString(Collections.emptyList());

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());
        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null);
        ResponseEntity<Object> responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");
    }

    @Test(expected = UserProfileException.class)
    public void test_refreshUserProfile_BasedOnCcdServiceNames_when_LrdResponseReturns400()
            throws JsonProcessingException {
        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .errorCode(400)
                .errorDescription("testErrorDesc")
                .errorMessage("testErrorMsg")
                .build()
                ;
        String body = mapper.writeValueAsString(errorResponse);

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(400).build());
        PageRequest pageRequest = getPageRequest();
        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null);
        ResponseEntity<Object> responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");
    }

    @Test
    public void test_refreshUserProfile_BasedOn_All_200() throws JsonProcessingException {
        UserProfile userProfile = buildUserProfile();

        PageRequest pageRequest = getPageRequest();
        PageImpl<UserProfile> page = new PageImpl<>(Collections.singletonList(userProfile));
        when(userProfileRepository.fetchUserProfileByAll(pageRequest))
                .thenReturn(page);
        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("",  null, null);
        ResponseEntity<Object> responseEntity = judicialUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");

        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @NotNull
    private PageRequest getPageRequest() {
        return RequestUtils.validateAndBuildPaginationObject(1, 0,
                "ASC", "objectId",
                20, "id", UserProfile.class);
    }

    public UserProfile buildUserProfile() {

        BaseLocationType baseLocationType = new BaseLocationType();
        baseLocationType.setBaseLocationId("123");

        Appointment appointment = new Appointment();
        appointment.setOfficeAppointmentId(1L);
        appointment.setIsPrincipleAppointment(true);
        appointment.setStartDate(LocalDate.now());
        appointment.setEndDate(LocalDate.now());
        appointment.setActiveFlag(true);
        appointment.setExtractedDate(LocalDateTime.now());
        appointment.setCreatedDate(LocalDateTime.now());
        appointment.setLastLoadedDate(LocalDateTime.now());
        appointment.setBaseLocationType(baseLocationType);

        Authorisation authorisation = new Authorisation();
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
        authorisation.setServiceCode("BFA1");

        UserProfile userProfile = new UserProfile();
        userProfile.setPerId("1");
        userProfile.setPersonalCode("Emp");
        //userProfile.setAppointment("Magistrate");
        userProfile.setKnownAs("TestEmp");
        userProfile.setSurname("Test");
        userProfile.setFullName("Test1");
        userProfile.setPostNominals("Test Test1");
        //userProfile.setAppointmentType("temp");
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

        userProfile.setAppointments(singletonList(appointment));
        userProfile.setAuthorisations(singletonList(authorisation));

        return userProfile;
    }


}
