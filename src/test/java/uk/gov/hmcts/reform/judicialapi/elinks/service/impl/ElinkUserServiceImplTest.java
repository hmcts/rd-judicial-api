package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.judicialapi.controller.advice.UserProfileException;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.LocationMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.RegionType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ServiceCodeMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationMapppingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.RegionMappingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ServiceCodeMappingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserProfileRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RequestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.validator.ElinksRefreshUserValidator;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;

import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ElinkUserServiceImplTest {

    private static final LocalDate date = LocalDate.now();
    private static final LocalDateTime dateTime = LocalDateTime.now();

    @InjectMocks
    ElinkUserServiceImpl elinkUserService;

    @Spy
    ProfileRepository profileRepository;

    @Spy
    ServiceCodeMappingRepository serviceCodeMappingRepository;


    @Mock
    RegionMappingRepository regionMappingRepository;

    @Mock
    private LocationReferenceDataFeignClient locationReferenceDataFeignClient;

    @Spy
    LocationMapppingRepository locationMappingRepository;

    private ElinksRefreshUserValidator refreshUserValidatorMock;
    ObjectMapper mapper = new ObjectMapper();

    private List<String> searchServiceCode;

    @BeforeEach
    void setUp() {
        refreshUserValidatorMock = new ElinksRefreshUserValidator();
        elinkUserService.setElinksRefreshUserValidator(refreshUserValidatorMock);

        searchServiceCode = (List.of("bfa1","bba3"));
        elinkUserService.setSearchServiceCode(searchServiceCode);
    }


    @Test
    void shouldReturn200WhenUserFoundForTheSearchRequestProvidedForElinks() {
        var userSearchRequest = UserSearchRequest
                .builder()
                .serviceCode("BFA1")
                .location("12456")
                .searchString("Test")
                .build();
        var userProfile = createUserSearchResponse();
        var userProfile1 = createUserSearchResponse();
        var serviceCodeMapping = ServiceCodeMapping
                .builder()
                .ticketCode("testTicketCode")
                .build();

        when(serviceCodeMappingRepository.findByServiceCodeIgnoreCase(userSearchRequest.getServiceCode()))
                .thenReturn(List.of(serviceCodeMapping));
        when(profileRepository.findBySearchForString(userSearchRequest.getSearchString().toLowerCase(),
                userSearchRequest.getServiceCode(),userSearchRequest.getLocation(),List.of("testTicketCode"),
                searchServiceCode))
                .thenReturn(List.of(userProfile,userProfile1));

        var responseEntity =
            elinkUserService.retrieveElinkUsers(userSearchRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(profileRepository, times(1)).findBySearchForString(any(),any(),
                any(), anyList(),anyList());
    }

    @Test
    void shouldReturn200WhenUserFoundForSscsSearchRequestProvided() {
        var userSearchRequest = UserSearchRequest
                .builder()
                .serviceCode("BBA3")
                .location("12456")
                .searchString("Test")
                .build();
        var userProfile = createUserSearchResponse();
        var userProfile1 = createUserSearchResponse();
        var serviceCodeMapping = ServiceCodeMapping
                .builder()
                .ticketCode("testTicketCode")
                .build();

        when(serviceCodeMappingRepository.findByServiceCodeIgnoreCase(userSearchRequest.getServiceCode()))
                .thenReturn(List.of(serviceCodeMapping));
        when(profileRepository.findBySearchForString(userSearchRequest.getSearchString().toLowerCase(),
                userSearchRequest.getServiceCode(),userSearchRequest.getLocation(),List.of("testTicketCode"),
                searchServiceCode))
                .thenReturn(List.of(userProfile,userProfile1));

        var responseEntity =
            elinkUserService.retrieveElinkUsers(userSearchRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(profileRepository, times(1)).findBySearchForString(any(),any(),
                any(), anyList(),anyList());
    }


    @Test
    void shouldReturn200WithEmptyResponseWhenUserNotFoundForTheSearchRequestProvided() {

        var userSearchRequest = UserSearchRequest
                .builder()
                .location("12456")
                .searchString("Test")
                .build();

        when(profileRepository.findBySearchForString(any(), any(), any(), any(),any()))
                .thenReturn(Collections.emptyList());

        var responseEntity =
            elinkUserService.retrieveElinkUsers(userSearchRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Collections.EMPTY_LIST, responseEntity.getBody());
        verify(profileRepository, times(1)).findBySearchForString(any(),any(),
                any(), anyList(),anyList());
    }

    public static UserSearchResponseWrapper createUserSearchResponse() {
        UserSearchResponseWrapper userSearchResponse = new UserSearchResponseWrapper();
        userSearchResponse.setPersonalCode("personalCode");
        userSearchResponse.setKnownAs("knownAs");
        userSearchResponse.setSurname("surname");
        userSearchResponse.setFullName("name");
        userSearchResponse.setTitle("postNominals");
        userSearchResponse.setEmailId("emailId");
        userSearchResponse.setIdamId("sidamId");
        userSearchResponse.setInitials("I");
        userSearchResponse.setTitle("Mr");

        return userSearchResponse;
    }

    @NotNull
    private PageRequest getElinksPageRequest() {
        return RequestUtils.validateAndBuildPaginationObject(1, 0,
                "ASC", "objectId",
                20, "id", UserProfile.class);
    }

    @Test
    void test_elinksRefreshUserProfile_Two_Input_01() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, Arrays.asList("test", "test"),null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });

    }

    @Test
    void test_elinksRefreshUserProfile_Two_Input_02() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                Arrays.asList("test", "test"), null,null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_elinksRefreshUserProfile_Two_Input_03() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("",
                Arrays.asList("test", "test"), Arrays.asList("test", "test"),null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_elinksRefreshUserProfile_Two_Input_04() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("",
                Arrays.asList("test", "test"), null,Arrays.asList("test", "test"));
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_elinksRefreshUserProfile_Multiple_Input() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                Arrays.asList("test", "test"), Arrays.asList("test", "test"),null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_elinksRefreshUserProfile_No_Input() throws JsonProcessingException {
        checkAssertion("");
    }

    @Test
    void test_elinksRefreshUserProfile_WhenCcdServiceNameContainComma() throws JsonProcessingException {
        checkAssertion("abc,def");
    }

    @Test
    void test_elinksRefreshUserProfile_WhenCcdServiceNameContainAll() throws JsonProcessingException {
        checkAssertion(" all ");
    }

    private void checkAssertion(String ccdServiceNames) {
        var refreshRoleRequest = new RefreshRoleRequest(ccdServiceNames,
                null, null,null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }


    @Test
    void test_elinksRefreshUserProfile_BasedOnSidamIds_200() throws JsonProcessingException {
        var userProfile = buildUserProfile();

        var pageRequest = getElinksPageRequest();
        var page = new PageImpl<>(Collections.singletonList(userProfile));
        var serviceCodeMappingOne = ServiceCodeMapping
                .builder()
                .ticketCode("300")
                .serviceCode("BBA3")
                .build();
        var serviceCodeMappingTwo = ServiceCodeMapping
                .builder()
                .ticketCode("373")
                .serviceCode("BFA1")
                .build();

        when(serviceCodeMappingRepository.findAllServiceCodeMapping())
                .thenReturn(List.of(serviceCodeMappingOne,serviceCodeMappingTwo));
        when(profileRepository.fetchUserProfileBySidamIds(List.of("test", "test"), pageRequest))
                .thenReturn(page);
        var refreshRoleRequest = new uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest("",
                null, Arrays.asList("test", "test"),null);
        var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");

        assertEquals(200, responseEntity.getStatusCodeValue());
    }


    @DisplayName("Refresh ElinksUserprofile based on IAC objectId")
    @Test
    void test_elinksRefreshUserProfile_BasedOnObjectIds_200() {
        var userProfile = buildUserProfileIac();
        var pageRequest = getElinksPageRequest();
        var page = new PageImpl<>(List.of(userProfile));

        var serviceCodeMappingOne = uk.gov.hmcts.reform.judicialapi.elinks.domain.ServiceCodeMapping
                .builder()
                .ticketCode("300")
                .serviceCode("BBA3")
                .build();
        var serviceCodeMappingTwo = uk.gov.hmcts.reform.judicialapi.elinks.domain.ServiceCodeMapping
                .builder()
                .ticketCode("373")
                .serviceCode("BFA1")
                .build();

        when(serviceCodeMappingRepository.findAllServiceCodeMapping())
                .thenReturn(List.of(serviceCodeMappingOne,serviceCodeMappingTwo));
        when(profileRepository.fetchUserProfileByObjectIds(List.of("test", "test"), pageRequest))
                .thenReturn(page);
        var refreshRoleRequest = new uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest("",
                Arrays.asList("test", "test"), null,null);
        var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");

        assertEquals(200, responseEntity.getStatusCodeValue());
        ArrayList profiles =  (ArrayList)responseEntity.getBody();
        assertEquals(1, profiles.size());

    }


    @DisplayName("Refresh ElinksUserprofile based on NonIAC objectId")
    @Test
    void test_elinksRefreshUserProfile_BasedOnObjectIds_NonIac200() {
        var userProfile = buildUserProfileNonIac();
        var pageRequest = getElinksPageRequest();
        var page = new PageImpl<>(List.of(userProfile));

        var serviceCodeMappingOne = uk.gov.hmcts.reform.judicialapi.elinks.domain.ServiceCodeMapping
                .builder()
                .ticketCode("366")
                .serviceCode("BBA3")
                .build();


        when(serviceCodeMappingRepository.findAllServiceCodeMapping()).thenReturn(List.of(serviceCodeMappingOne));
        when(profileRepository.fetchUserProfileByObjectIds(List.of("test", "test"), pageRequest))
                .thenReturn(page);
        var refreshRoleRequest = new uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest("",
                Arrays.asList("test", "test"), null,null);

        var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");
        assertEquals(200, responseEntity.getStatusCodeValue());
        ArrayList profiles =  (ArrayList)responseEntity.getBody();
        assertEquals(1, profiles.size());
        UserProfileRefreshResponse profile = (UserProfileRefreshResponse)profiles.get(0);
        assertEquals(2, profile.getAppointments().size());
        assertEquals(2, profile.getAuthorisations().size());
    }

    @Test
    void test_elinksRefreshUserProfile_BasedOnPersonalCodes_Error() throws JsonProcessingException {
        var userProfile = buildUserProfile();

        var pageRequest = getElinksPageRequest();
        var page = new PageImpl<>(Collections.singletonList(userProfile));

        var refreshRoleRequest = new uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest("",
                Arrays.asList("test", "test"), null,null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void test_elinksRefreshUserProfile_BasedOnPersonalCodes_200() {
        var userProfile = buildUserProfileIac();

        var pageRequest = getElinksPageRequest();
        var page = new PageImpl<>(Collections.singletonList(userProfile));
        var serviceCodeMapping = uk.gov.hmcts.reform.judicialapi.elinks.domain.ServiceCodeMapping
                .builder()
                .ticketCode("373")
                .serviceCode("BFA1")
                .build();

        when(serviceCodeMappingRepository.findAllServiceCodeMapping()).thenReturn(List.of(serviceCodeMapping));

        when(profileRepository.fetchUserProfileByPersonalCodes(List.of("Emp", "Emp"), pageRequest))
                .thenReturn(page);
        var refreshRoleRequest = new uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest("",
                null, null, Arrays.asList("Emp", "Emp", null));
        var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");
        List<uk.gov.hmcts.reform.judicialapi.elinks.response.UserProfileRefreshResponse>
                userProfileRefreshResponses = (List<uk.gov.hmcts.reform.judicialapi.elinks
                .response.UserProfileRefreshResponse>) responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(userProfileRefreshResponses.get(0).getAppointments().get(0).getStartDate());
        assertNull(userProfileRefreshResponses.get(0).getAppointments().get(0).getEndDate());
        assertNotNull(userProfileRefreshResponses.get(0).getAuthorisations().get(0).getStartDate());
        assertNull(userProfileRefreshResponses.get(0).getAuthorisations().get(0).getEndDate());
        assertNotNull(userProfileRefreshResponses.get(0).getAppointments().get(0).getCftRegionID());
        assertNotNull(userProfileRefreshResponses.get(0).getAppointments().get(0).getCftRegion());
        assertNotNull(userProfileRefreshResponses.get(0).getAuthorisations().get(0).getServiceCodes().get(0));
        assertNotNull(userProfileRefreshResponses.get(0).getAppointments().get(0).getEpimmsId());
        assertNull(userProfileRefreshResponses.get(0).getRoles().get(0).getEndDate());
        assertNull(userProfileRefreshResponses.get(0).getRoles().get(0).getStartDate());
        assertNull(userProfileRefreshResponses.get(0).getRetirementDate());

    }

    @Test
    void test_elinksRefreshUserProfile_BasedOnPersonalCodes_400() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("",
                null, null, Arrays.asList("Emp", "Emp", null));

        Assertions.assertThrows(InvalidRequestException.class, () -> {
            var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, -1,
                    0, "ASC", "objectId");

        });
    }



    @Test
    void test_elinksRefreshUserProfile_BasedOnPersonalCodes_404() throws JsonProcessingException {
        var userProfile = buildUserProfile();

        var pageRequest = getElinksPageRequest();
        var page = new PageImpl<>(Collections.singletonList(userProfile));
        when(profileRepository.fetchUserProfileByPersonalCodes(List.of("Emp", "Emp"), pageRequest))
                .thenReturn(null);
        var refreshRoleRequest = new RefreshRoleRequest("",
                null, null, Arrays.asList("Emp", "Emp"));
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }


    @Test
    void test_elinksRefreshUserProfile_BasedOnCcdServiceNames_200() throws JsonProcessingException {
        var lrdOrgInfoServiceResponse = new LrdOrgInfoServiceResponse();
        lrdOrgInfoServiceResponse.setServiceCode("BFA1");
        lrdOrgInfoServiceResponse.setCcdServiceName("cmc");
        var body = mapper.writeValueAsString(List.of(lrdOrgInfoServiceResponse));

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());

        var userProfile = buildUserProfile();

        var pageRequest = getElinksPageRequest();

        var page = new PageImpl<>(Collections.singletonList(userProfile));

        when(serviceCodeMappingRepository.fetchTicketCodeFromServiceCode(Set.of("BFA1"))).thenReturn(List.of("386"));
        when(profileRepository.fetchUserProfileByServiceNames(Set.of("BFA1"), List.of("386"), pageRequest))
                .thenReturn(page);
        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null,null);
        var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                0, "ASC", "objectId");

        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    void test_elinksRefreshUserProfile_BasedOnCcdServiceNames_when_LrdResponse_IsNon_200() {

        var pageRequest = getElinksPageRequest();
        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body("body", defaultCharset()).status(400).build());

        var refreshRoleRequest = new uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest("cmc",
                null, null,null);
        Assertions.assertThrows(UserProfileException.class, () -> {
            var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });

    }

    @Test
    void test_elinksRefreshUserProfile_BasedOnCcdServiceNames_when_Response_Empty() throws JsonProcessingException {

        var lrdOrgInfoServiceResponse = new LrdOrgInfoServiceResponse();
        lrdOrgInfoServiceResponse.setServiceCode("BFA1");
        lrdOrgInfoServiceResponse.setCcdServiceName("cmc");
        var body = mapper.writeValueAsString(List.of(lrdOrgInfoServiceResponse));

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());

        var pageRequest = getElinksPageRequest();

        var page = new PageImpl<uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile>(Collections.emptyList());
        when(profileRepository.fetchUserProfileByServiceNames(Set.of("BFA1"), List.of("386"), pageRequest))
                .thenReturn(page);
        when(serviceCodeMappingRepository.fetchTicketCodeFromServiceCode(Set.of("BFA1"))).thenReturn(List.of("386"));
        var refreshRoleRequest = new uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest("cmc",
                null, null,null);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_elinksRefreshUserProfile_BasedOnCcdServiceNames_when_LrdResponseIsEmpty()
            throws JsonProcessingException {

        var body = mapper.writeValueAsString(Collections.emptyList());

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());
        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null,null);

        Assertions.assertThrows(UserProfileException.class, () -> {
            var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_elinksRefreshUserProfile_BasedOnCcdServiceNames_when_LrdResponseReturns400()
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
        var pageRequest = getElinksPageRequest();
        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, null,null);

        Assertions.assertThrows(UserProfileException.class, () -> {
            var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_elinksRefreshUserProfile_BasedOn_All_400() throws JsonProcessingException {
        var refreshRoleRequest =
                new RefreshRoleRequest("", null, null,null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            var responseEntity = elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }



    UserProfile buildUserProfile() {

        var baseLocationType = new BaseLocation();
        baseLocationType.setBaseLocationId("1");
        baseLocationType.setParentId("National");

        var regionType = new RegionType();
        regionType.setRegionId("1");
        regionType.setRegionDescCy("National");
        regionType.setRegionDescEn("National");

        var appointment = new Appointment();
        appointment.setEpimmsId("1234");
        appointment.setOfficeAppointmentId(1L);
        appointment.setIsPrincipleAppointment(true);
        appointment.setStartDate(LocalDate.now());
        appointment.setEndDate(LocalDate.now());
        appointment.setCreatedDate(LocalDateTime.now());
        appointment.setLastLoadedDate(LocalDateTime.now());
        appointment.setBaseLocationType(baseLocationType);
        appointment.setRegionType(regionType);
        appointment.setRegionId("1");
        appointment.setPersonalCode("");
        appointment.setBaseLocationId("1");
        appointment.setAppointmentMapping("String");
        appointment.setAppointmentType("test");
        appointment.setType("test");
        appointment.setAppointmentId("1");
        appointment.setRoleNameId("test");
        appointment.setContractTypeId("test");
        appointment.setLocation("test");
        appointment.setJoBaseLocationId("test");
        appointment.setLocationMappings(List.of(LocationMapping.builder().serviceCode("BBA3")
                .judicialBaseLocationId("1").epimmsId("1").build()));


        var appointmentTwo = new Appointment();
        appointmentTwo.setEpimmsId(null);
        appointmentTwo.setOfficeAppointmentId(1L);
        appointmentTwo.setIsPrincipleAppointment(true);
        appointmentTwo.setStartDate(LocalDate.now());
        appointmentTwo.setEndDate(LocalDate.now().minusDays(10));
        appointmentTwo.setCreatedDate(LocalDateTime.now());
        appointmentTwo.setLastLoadedDate(LocalDateTime.now());
        appointmentTwo.setBaseLocationType(baseLocationType);
        appointmentTwo.setRegionType(regionType);
        appointmentTwo.setRegionId("2");
        appointmentTwo.setPersonalCode("");
        appointmentTwo.setBaseLocationId("1");
        appointmentTwo.setAppointmentMapping("String");
        appointmentTwo.setAppointmentType("test");
        appointmentTwo.setType("test");
        appointmentTwo.setAppointmentId("2");
        appointmentTwo.setRoleNameId("test");
        appointmentTwo.setContractTypeId("test");
        appointmentTwo.setLocation("test");
        appointmentTwo.setJoBaseLocationId("test");
        appointmentTwo.setLocationMappings(List.of(LocationMapping.builder().serviceCode("BFA1")
                .judicialBaseLocationId("2").epimmsId("2").build()));

        var appointmentThree = new Appointment();
        appointmentThree.setEpimmsId(null);
        appointmentThree.setOfficeAppointmentId(1L);
        appointmentThree.setIsPrincipleAppointment(true);
        appointmentThree.setStartDate(LocalDate.now());
        appointmentThree.setEndDate(LocalDate.now().plusDays(10));
        appointmentThree.setCreatedDate(LocalDateTime.now());
        appointmentThree.setLastLoadedDate(LocalDateTime.now());
        appointmentThree.setBaseLocationType(baseLocationType);
        appointmentThree.setRegionType(regionType);
        appointmentThree.setRegionId("2");
        appointmentThree.setPersonalCode("");
        appointmentThree.setBaseLocationId("1");
        appointmentThree.setAppointmentMapping("String");
        appointmentThree.setAppointmentType("test");
        appointmentThree.setType("test");
        appointmentThree.setAppointmentId("3");
        appointmentThree.setRoleNameId("test");
        appointmentThree.setContractTypeId("test");
        appointmentThree.setLocation("test");
        appointmentThree.setJoBaseLocationId("test");
        appointmentThree.setLocationMappings(List.of(LocationMapping.builder().serviceCode("BFA1")
                .judicialBaseLocationId("3").epimmsId("3").build()));

        var appointmentFour = new Appointment();
        appointmentFour.setEpimmsId("10");
        appointmentFour.setOfficeAppointmentId(1L);
        appointmentFour.setIsPrincipleAppointment(true);
        appointmentFour.setStartDate(LocalDate.now());
        appointmentFour.setEndDate(LocalDate.now());
        appointmentFour.setCreatedDate(LocalDateTime.now());
        appointmentFour.setLastLoadedDate(LocalDateTime.now());
        appointmentFour.setBaseLocationType(baseLocationType);
        appointmentFour.setRegionType(regionType);
        appointmentFour.setRegionId("3");
        appointmentFour.setPersonalCode("");
        appointmentFour.setBaseLocationId("4");
        appointmentFour.setAppointmentMapping("String");
        appointmentFour.setAppointmentType("test");
        appointmentFour.setType("test");
        appointmentFour.setAppointmentId("1");
        appointmentFour.setRoleNameId("test");
        appointmentFour.setContractTypeId("test");
        appointmentFour.setLocation("test");
        appointmentFour.setJoBaseLocationId("test");
        appointmentFour.setLocationMappings(List.of(LocationMapping.builder().serviceCode("BBA3")
                .judicialBaseLocationId("4").epimmsId("4").build()));

        var authorisation = new Authorisation();
        authorisation.setOfficeAuthId(1L);
        authorisation.setJurisdiction("Languages");
        authorisation.setStartDate(null);
        authorisation.setEndDate(null);
        authorisation.setCreatedDate(LocalDateTime.now());
        authorisation.setLastUpdated(LocalDateTime.now());
        authorisation.setLowerLevel("Welsh");
        authorisation.setPersonalCode("");
        authorisation.setTicketCode(null);
        authorisation.setAppointmentId("1");
        authorisation.setAuthorisationId("1");
        authorisation.setJurisdictionId("test");

        var authorisationOne = new Authorisation();
        authorisationOne.setOfficeAuthId(1L);
        authorisationOne.setJurisdiction("Languages");
        authorisationOne.setStartDate(LocalDate.now());
        authorisationOne.setEndDate(LocalDate.now().plusDays(15));
        authorisationOne.setCreatedDate(LocalDateTime.now());
        authorisationOne.setLastUpdated(LocalDateTime.now());
        authorisationOne.setLowerLevel("Welsh");
        authorisationOne.setPersonalCode("");
        authorisationOne.setTicketCode("373");
        authorisationOne.setAppointmentId("2");
        authorisationOne.setAuthorisationId("2");
        authorisationOne.setJurisdictionId("test");

        var authorisationTwo = new Authorisation();
        authorisationTwo.setOfficeAuthId(1L);
        authorisationTwo.setJurisdiction("Languages");
        authorisationTwo.setStartDate(LocalDate.now());
        authorisationTwo.setEndDate(LocalDate.now().minusDays(5));
        authorisationTwo.setCreatedDate(LocalDateTime.now());
        authorisationTwo.setLastUpdated(LocalDateTime.now());
        authorisationTwo.setLowerLevel("Welsh");
        authorisationTwo.setPersonalCode("");
        authorisationTwo.setTicketCode("100");
        authorisationTwo.setAppointmentId("3");
        authorisationTwo.setAuthorisationId("3");
        authorisationTwo.setJurisdictionId("test");

        var judicialRoleType = new JudicialRoleType();
        judicialRoleType.setRoleId(1);
        judicialRoleType.setTitle("Test1");

        var judicialRoleType1 = new JudicialRoleType();
        judicialRoleType1.setRoleId(2);
        judicialRoleType1.setTitle("Test2");
        judicialRoleType1.setEndDate(LocalDateTime.now().minusDays(3));

        var judicialRoleType2 = new JudicialRoleType();
        judicialRoleType2.setRoleId(3);
        judicialRoleType2.setTitle("Test3");
        judicialRoleType2.setEndDate(LocalDateTime.now().plusDays(3));

        var userProfile = new UserProfile();
        userProfile.setPersonalCode("Emp");
        userProfile.setKnownAs("TestEmp");
        userProfile.setSurname("Test");
        userProfile.setFullName("Test1");
        userProfile.setPostNominals("Test Test1");
        userProfile.setEjudiciaryEmailId("abc@gmail.com");
        userProfile.setLastWorkingDate(LocalDate.now());
        userProfile.setActiveFlag(true);
        userProfile.setCreatedDate(LocalDateTime.now());
        userProfile.setLastLoadedDate(LocalDateTime.now());
        userProfile.setObjectId("");
        userProfile.setSidamId("4c0ff6a3-8fd6-803b-301a-29d9dacccca8");

        userProfile.setAppointments(List.of(appointment,appointmentTwo,appointmentThree,appointmentFour));
        userProfile.setAuthorisations(List.of(authorisation,authorisationOne,authorisationTwo));
        userProfile.setJudicialRoleTypes(List.of(judicialRoleType,judicialRoleType1,judicialRoleType2));

        return userProfile;
    }

    //Valid IAC record
    UserProfile buildUserProfileIac() {

        var baseLocationType = new BaseLocation();
        baseLocationType.setBaseLocationId("2");

        var regionType = new RegionType();
        regionType.setRegionId("1");
        regionType.setRegionDescCy("Nationals");
        regionType.setRegionDescEn("Nationals");

        var appointment = new Appointment();
        appointment.setEpimmsId(" ");
        appointment.setOfficeAppointmentId(1L);
        appointment.setIsPrincipleAppointment(true);
        appointment.setStartDate(LocalDate.now());
        appointment.setEndDate(null);
        appointment.setCreatedDate(LocalDateTime.now());
        appointment.setLastLoadedDate(LocalDateTime.now());
        appointment.setBaseLocationType(baseLocationType);
        appointment.setRegionType(regionType);
        appointment.setRegionId("1");
        appointment.setPersonalCode("");
        appointment.setBaseLocationId("1");
        appointment.setAppointmentMapping("String");
        appointment.setAppointmentType("test");
        appointment.setType("test");
        appointment.setAppointmentId("1");
        appointment.setRoleNameId("test");
        appointment.setContractTypeId("test");
        appointment.setLocation("test");
        appointment.setJoBaseLocationId("test");
        appointment.setLocationMappings(List.of(LocationMapping.builder().serviceCode("BBA3")
                .judicialBaseLocationId("1").epimmsId("1").build()));



        var authorisation = new Authorisation();
        authorisation.setOfficeAuthId(1L);
        authorisation.setJurisdiction("Languages");
        authorisation.setStartDate(LocalDate.now());
        authorisation.setEndDate(null);
        authorisation.setCreatedDate(LocalDateTime.now());
        authorisation.setLastUpdated(LocalDateTime.now());
        authorisation.setLowerLevel("Welsh");
        authorisation.setPersonalCode("100");
        authorisation.setTicketCode("373");
        authorisation.setAppointmentId("1");
        authorisation.setAuthorisationId("1");
        authorisation.setJurisdictionId("test");

        var judicialRoleType = new JudicialRoleType();
        judicialRoleType.setRoleId(1);
        judicialRoleType.setTitle("Test1");

        var judicialRoleType1 = new JudicialRoleType();
        judicialRoleType1.setRoleId(2);
        judicialRoleType1.setTitle("Test2");
        judicialRoleType1.setEndDate(LocalDateTime.now().minusDays(3));

        var judicialRoleType2 = new JudicialRoleType();
        judicialRoleType2.setRoleId(3);
        judicialRoleType2.setTitle("Test3");
        judicialRoleType2.setEndDate(LocalDateTime.now().plusDays(3));

        var userProfile = new UserProfile();
        userProfile.setPersonalCode("Emp");
        userProfile.setKnownAs("TestEmp");
        userProfile.setSurname("Test");
        userProfile.setFullName("Test1");
        userProfile.setPostNominals("Test Test1");
        userProfile.setEjudiciaryEmailId("abc@gmail.com");
        userProfile.setLastWorkingDate(LocalDate.now());
        userProfile.setActiveFlag(false);
        userProfile.setCreatedDate(LocalDateTime.now());
        userProfile.setLastLoadedDate(LocalDateTime.now());
        userProfile.setObjectId("asd12345-0987asdas-asdas8asdas");
        userProfile.setSidamId("4c0ff6a3-8fd6-803b-301a-29d9dacccca8");

        authorisation.setUserProfile(userProfile);
        userProfile.setAppointments(List.of(appointment));
        userProfile.setAuthorisations(List.of(authorisation));
        userProfile.setJudicialRoleTypes(List.of(judicialRoleType,judicialRoleType1,judicialRoleType2));

        return userProfile;

    }

    //user Profile IAC records
    UserProfile buildUserProfileNonIac() {

        var baseLocationType = new BaseLocation();
        baseLocationType.setBaseLocationId("2");

        var regionType = new RegionType();
        regionType.setRegionId("1");
        regionType.setRegionDescCy("Nationals");
        regionType.setRegionDescEn("Nationals");

        var appointmentOne = new Appointment();
        appointmentOne.setEpimmsId(" ");
        appointmentOne.setOfficeAppointmentId(1L);
        appointmentOne.setIsPrincipleAppointment(true);
        appointmentOne.setStartDate(LocalDate.now());
        appointmentOne.setEndDate(null);
        appointmentOne.setCreatedDate(LocalDateTime.now());
        appointmentOne.setLastLoadedDate(LocalDateTime.now());
        appointmentOne.setBaseLocationType(baseLocationType);
        appointmentOne.setRegionType(regionType);
        appointmentOne.setRegionId("2");
        appointmentOne.setPersonalCode("");
        appointmentOne.setBaseLocationId("1");
        appointmentOne.setAppointmentMapping("String");
        appointmentOne.setAppointmentType("test");
        appointmentOne.setType("test");
        appointmentOne.setAppointmentId("1");
        appointmentOne.setRoleNameId("test");
        appointmentOne.setContractTypeId("test");
        appointmentOne.setLocation("test");
        appointmentOne.setJoBaseLocationId("test");
        appointmentOne.setLocationMappings(List.of(LocationMapping.builder().serviceCode("BBA3")
                .judicialBaseLocationId("1").epimmsId("1").build()));


        var appointmentTwo = new Appointment();
        appointmentTwo.setEpimmsId(" ");
        appointmentTwo.setOfficeAppointmentId(1L);
        appointmentTwo.setIsPrincipleAppointment(true);
        appointmentTwo.setStartDate(LocalDate.now());
        appointmentTwo.setEndDate(LocalDate.now().minusDays(1));
        appointmentTwo.setCreatedDate(LocalDateTime.now());
        appointmentTwo.setLastLoadedDate(LocalDateTime.now());
        appointmentTwo.setBaseLocationType(baseLocationType);
        appointmentTwo.setRegionType(regionType);
        appointmentTwo.setRegionId("2");
        appointmentTwo.setPersonalCode("");
        appointmentTwo.setBaseLocationId("1");
        appointmentTwo.setAppointmentMapping("String");
        appointmentTwo.setAppointmentType("test");
        appointmentTwo.setType("test");
        appointmentTwo.setAppointmentId("2");
        appointmentTwo.setRoleNameId("test");
        appointmentTwo.setContractTypeId("test");
        appointmentTwo.setLocation("test");
        appointmentTwo.setJoBaseLocationId("test");
        appointmentTwo.setLocationMappings(List.of(LocationMapping.builder().serviceCode("BFA1")
                .judicialBaseLocationId("2").epimmsId("2").build()));



        var authorisationOne = new Authorisation();
        authorisationOne.setOfficeAuthId(1L);
        authorisationOne.setJurisdiction("Languages");
        authorisationOne.setStartDate(LocalDate.now());
        authorisationOne.setEndDate(null);
        authorisationOne.setCreatedDate(LocalDateTime.now());
        authorisationOne.setLastUpdated(LocalDateTime.now());
        authorisationOne.setLowerLevel("Welsh");
        authorisationOne.setPersonalCode("100");
        authorisationOne.setTicketCode("366");
        authorisationOne.setAppointmentId("2");
        authorisationOne.setAuthorisationId("2");
        authorisationOne.setJurisdictionId("test");


        var authorisationTwo = new Authorisation();
        authorisationTwo.setOfficeAuthId(1L);
        authorisationTwo.setJurisdiction("Languages");
        authorisationTwo.setStartDate(LocalDate.now());
        authorisationTwo.setEndDate(LocalDate.now().minusDays(1));
        authorisationTwo.setCreatedDate(LocalDateTime.now());
        authorisationTwo.setLastUpdated(LocalDateTime.now());
        authorisationTwo.setLowerLevel("Welsh");
        authorisationTwo.setPersonalCode("100");
        authorisationTwo.setTicketCode(" ");
        authorisationTwo.setAppointmentId("3");
        authorisationTwo.setAuthorisationId("3");
        authorisationTwo.setJurisdictionId("test");

        var judicialRoleType = new JudicialRoleType();
        judicialRoleType.setRoleId(1);
        judicialRoleType.setTitle("Test1");

        var judicialRoleType1 = new JudicialRoleType();
        judicialRoleType1.setRoleId(2);
        judicialRoleType1.setTitle("Test2");
        judicialRoleType1.setEndDate(LocalDateTime.now().minusDays(3));

        var judicialRoleType2 = new JudicialRoleType();
        judicialRoleType2.setRoleId(3);
        judicialRoleType2.setTitle("Test3");
        judicialRoleType2.setEndDate(LocalDateTime.now().plusDays(3));

        var userProfile = new UserProfile();
        userProfile.setPersonalCode("Emp");
        userProfile.setKnownAs("TestEmp");
        userProfile.setSurname("Test");
        userProfile.setFullName("Test1");
        userProfile.setPostNominals("Test Test1");
        userProfile.setEjudiciaryEmailId("abcd@gmail.com");
        userProfile.setLastWorkingDate(LocalDate.now());
        userProfile.setActiveFlag(false);
        userProfile.setCreatedDate(LocalDateTime.now());
        userProfile.setLastLoadedDate(LocalDateTime.now());
        userProfile.setObjectId("asd12345-0987asdas-asdas8asdas");
        userProfile.setSidamId("4c0ff6a3-8fd6-803b-301a-29d9dacccca8");

        authorisationOne.setUserProfile(userProfile);
        userProfile.setAppointments(List.of(appointmentOne,appointmentTwo));
        userProfile.setAuthorisations(List.of(authorisationOne,authorisationTwo));
        userProfile.setJudicialRoleTypes(List.of(judicialRoleType,judicialRoleType1,judicialRoleType2));

        return userProfile;

    }


}
