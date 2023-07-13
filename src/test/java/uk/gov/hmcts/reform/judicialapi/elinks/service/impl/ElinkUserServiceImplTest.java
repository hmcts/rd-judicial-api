package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.domain.ServiceCodeMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;
import uk.gov.hmcts.reform.judicialapi.repository.ServiceCodeMappingRepository;
import uk.gov.hmcts.reform.judicialapi.validator.RefreshUserValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private RefreshUserValidator refreshUserValidatorMock;
    ObjectMapper mapper = new ObjectMapper();

    private List<String> searchServiceCode;

    @BeforeEach
    void setUp() {
        refreshUserValidatorMock = new RefreshUserValidator();
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

}
