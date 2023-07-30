package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;
import uk.gov.hmcts.reform.judicialapi.repository.ServiceCodeMappingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.validator.ElinksRefreshUserValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class ElinkUserServiceImplTest {

    private static final LocalDate date = LocalDate.now();
    private static final LocalDateTime dateTime = LocalDateTime.now();

    @InjectMocks
    ElinkUserServiceImpl elinkUserService;

    @Mock
    ProfileRepository profileRepository;

    @Mock
    ServiceCodeMappingRepository serviceCodeMappingRepository;

    private ElinksRefreshUserValidator refreshUserValidatorMock;
    ObjectMapper mapper = new ObjectMapper();

    private List<String> searchServiceCode;

    @BeforeEach
    void setUp() {
         refreshUserValidatorMock = new ElinksRefreshUserValidator();
         elinkUserService.setElinksRefreshUserValidator(refreshUserValidatorMock);

        //searchServiceCode = (List.of("bfa1","bba3"));
         //elinkUserService.setSearchServiceCode(searchServiceCode);
    }


    /*  @Test
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
    }*/

    /* @Test
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
    }*/


    /*   @Test
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
    }*/

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

    @Test
    void test_refreshUserProfile_Two_Input_01() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                null, Arrays.asList("test", "test"),null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });

    }

    @Test
    void test_refreshUserProfile_Two_Input_02() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                Arrays.asList("test", "test"), null,null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_Two_Input_03() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("",
                Arrays.asList("test", "test"), Arrays.asList("test", "test"),null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_Two_Input_04() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("",
                Arrays.asList("test", "test"), null,Arrays.asList("test", "test"));
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_Multiple_Input() throws JsonProcessingException {

        var refreshRoleRequest = new RefreshRoleRequest("cmc",
                Arrays.asList("test", "test"), Arrays.asList("test", "test"),null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }

    @Test
    void test_refreshUserProfile_No_Input() throws JsonProcessingException {
        checkAssertion("");
    }

    private void checkAssertion(String ccdServiceNames) {
        var refreshRoleRequest = new RefreshRoleRequest(ccdServiceNames,
                null, null,null);
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            elinkUserService.refreshUserProfile(refreshRoleRequest, 1,
                    0, "ASC", "objectId");
        });
    }



}
