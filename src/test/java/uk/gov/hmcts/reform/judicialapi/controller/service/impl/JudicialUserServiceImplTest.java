package uk.gov.hmcts.reform.judicialapi.controller.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import org.junit.Test;
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
import uk.gov.hmcts.reform.judicialapi.controller.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.judicialapi.controller.advice.UserProfileException;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.impl.JudicialUserServiceImpl;
import uk.gov.hmcts.reform.judicialapi.util.RequestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    ObjectMapper mapper = new ObjectMapper();

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
    public void testFetchUserProfileAllocationReturns200() throws JsonProcessingException {
        LrdOrgInfoServiceResponse lrdOrgInfoServiceResponse = new LrdOrgInfoServiceResponse();
        lrdOrgInfoServiceResponse.setServiceCode("BFA1");
        lrdOrgInfoServiceResponse.setCcdServiceName("cmc");
        String body = mapper.writeValueAsString(List.of(lrdOrgInfoServiceResponse));

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());

        UserProfile userProfile = buildUserProfile();

        PageRequest pageRequest = RequestUtils.validateAndBuildPaginationObject(0, 1,
                "perId", "ASC",
                20, "id", UserProfile.class);

        PageImpl<UserProfile> page = new PageImpl<>(Collections.singletonList(userProfile));
        when(userProfileRepository.fetchUserProfileByServiceNames(Set.of("BFA1"), pageRequest))
                .thenReturn(page);
        ResponseEntity<Object> responseEntity = judicialUserService
                .fetchUserProfileByServiceNames("cmc", pageRequest);

        assertEquals(200, responseEntity.getStatusCodeValue());

    }


    @Test(expected = UserProfileException.class)
    public void testFetchUserProfileAllocationWhenLrdResponseIsNon200() {

        PageRequest pageRequest = RequestUtils.validateAndBuildPaginationObject(0, 1,
                "perId", "ASC",
                20, "id", UserProfile.class);
        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body("body", defaultCharset()).status(400).build());

        judicialUserService
                .fetchUserProfileByServiceNames("cmc", pageRequest);

    }


    @Test(expected = ResourceNotFoundException.class)
    public void testFetchUserProfileAllocationWhenResponseIsEmpty() throws JsonProcessingException {

        LrdOrgInfoServiceResponse lrdOrgInfoServiceResponse = new LrdOrgInfoServiceResponse();
        lrdOrgInfoServiceResponse.setServiceCode("BFA1");
        lrdOrgInfoServiceResponse.setCcdServiceName("cmc");
        String body = mapper.writeValueAsString(List.of(lrdOrgInfoServiceResponse));

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());

        PageRequest pageRequest = RequestUtils.validateAndBuildPaginationObject(0, 1,
                "perId", "ASC",
                20, "id", UserProfile.class);

        PageImpl<UserProfile> page = new PageImpl<>(Collections.emptyList());
        when(userProfileRepository.fetchUserProfileByServiceNames(Set.of("BFA1"), pageRequest))
                .thenReturn(page);
        judicialUserService
                .fetchUserProfileByServiceNames("cmc", pageRequest);
    }


    @Test(expected = UserProfileException.class)
    public void testFetchUserProfileAllocationWhenLrdResponseIsEmpty() throws JsonProcessingException {

        String body = mapper.writeValueAsString(Collections.emptyList());

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping("cmc"))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());

        List<UserProfile> caseWorkerWorkAreas = new ArrayList<>();

        PageRequest pageRequest = RequestUtils.validateAndBuildPaginationObject(0, 1,
                "perId", "ASC",
                20, "id", UserProfile.class);

        judicialUserService
                .fetchUserProfileByServiceNames("cmc", pageRequest);
    }

    @Test(expected = UserProfileException.class)
    public void testFetchUserProfileAllocationWhenLrdResponseReturns400() throws JsonProcessingException {
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


        PageRequest pageRequest = RequestUtils.validateAndBuildPaginationObject(0, 1,
                "perId", "ASC",
                20, "id", UserProfile.class);

        judicialUserService
                .fetchUserProfileByServiceNames("cmc", pageRequest);
    }

    public UserProfile buildUserProfile() {

        Appointment appointment = new Appointment();
        appointment.setOfficeAppointmentId(1L);
        appointment.setIsPrincipleAppointment(true);
        appointment.setStartDate(LocalDate.now());
        appointment.setEndDate(LocalDate.now());
        appointment.setActiveFlag(true);
        appointment.setExtractedDate(LocalDateTime.now());
        appointment.setCreatedDate(LocalDateTime.now());
        appointment.setLastLoadedDate(LocalDateTime.now());

        Authorisation authorisation = new Authorisation();
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
        userProfile.setAppointment("Magistrate");
        userProfile.setKnownAs("TestEmp");
        userProfile.setSurname("Test");
        userProfile.setFullName("Test1");
        userProfile.setPostNominals("Test Test1");
        userProfile.setAppointmentType("temp");
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
