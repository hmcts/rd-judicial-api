package uk.gov.hmcts.reform.judicialapi.controller.service.impl;

import org.junit.jupiter.api.Test;
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
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.impl.JudicialUserServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createUserProfile;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;

@ExtendWith(MockitoExtension.class)
class JudicialUserServiceImplTest {

    @InjectMocks
    JudicialUserServiceImpl judicialUserService;

    @Mock
    UserProfileRepository userProfileRepository;

    @Mock
    ServiceCodeMappingRepository serviceCodeMappingRepository;

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
    void shouldReturn404WhenUserNotFoundForTheSearchRequestProvided() {

        var userSearchRequest = UserSearchRequest
                .builder()
                .location("12456")
                .searchString("Test")
                .build();

        when(userProfileRepository.findBySearchString(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                judicialUserService.retrieveUserProfile(userSearchRequest));
    }
}
