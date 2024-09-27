package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.RegionType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinksResponsesRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.BaseLocationResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleDeleteService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleLeaverService;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataExceptionHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksResponsesHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_DATA_STORE_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"AbbreviationAsWordInName","MemberName"})
class ELinksServiceImplTest {

    @Mock
    BaseLocationRepository baseLocationRepository;

    @Mock
    LocationRepository locationRepository;

    @Spy
    ElinksFeignClient elinksFeignClient;

    @Spy
    private ElinkDataIngestionSchedularAudit elinkDataIngestionSchedularAudit;

    @Mock
    ElinksResponsesHelper elinksResponsesHelper;


    @InjectMocks
    private ELinksServiceImpl eLinksServiceImpl;

    @Spy
    ElinksResponsesRepository elinksResponsesRepository;

    @Mock
    ElinkDataExceptionHelper elinkDataExceptionHelper;

    @Mock
    ElinkDataExceptionRepository elinkDataExceptionRepository;

    @Spy
    ProfileRepository profileRepository;

    @Spy
    AuthorisationsRepository authorisationRepository;

    @Spy
    AppointmentsRepository appointmentsRepository;

    @Spy
    JudicialRoleTypeRepository judicialRoleTypeRepository;

    @Spy
    ElinksPeopleLeaverService elinksPeopleLeaverService;

    @Spy
    ElinksPeopleDeleteService elinksPeopleDeleteService;


    @BeforeEach
    void setUP() {

        ReflectionTestUtils.setField(eLinksServiceImpl, "cleanElinksResponsesDays",
                30L);

        ReflectionTestUtils.setField(eLinksServiceImpl, "delJohProfiles", true);

        ReflectionTestUtils.setField(eLinksServiceImpl, "delJohProfilesYears", 7L);

    }

    @Test
    void elinksService_load_location_should_return_sucess_msg_with_status_200() throws JsonProcessingException {

        List<BaseLocationResponse> baseLocationResponses = getBaseLocationResponseData();


        ElinkBaseLocationResponse elinkLocationResponse = new ElinkBaseLocationResponse();
        elinkLocationResponse.setResults(baseLocationResponses);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);


        when(elinksFeignClient.getLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(HttpStatus.OK.value()).build());


        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(200).build());


        when(baseLocationRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        ResponseEntity<ElinkBaseLocationWrapperResponse> responseEntity = eLinksServiceImpl.retrieveLocation();

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseEntity.getBody().getMessage()).contains(BASE_LOCATION_DATA_LOAD_SUCCESS);

    }


    @Test
    void elinksService_load_location_should_return_elinksException_when_DataAccessException()
            throws JsonProcessingException {

        List<BaseLocationResponse> baseLocationResponses = getBaseLocationResponseData();


        ElinkBaseLocationResponse elinkLocationResponse = new ElinkBaseLocationResponse();
        elinkLocationResponse.setResults(baseLocationResponses);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);

        DataAccessException dataAccessException = mock(DataAccessException.class);

        when(elinksFeignClient.getLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(HttpStatus.OK.value()).build());

        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(200).build());

        when(baseLocationRepository.saveAll(anyList())).thenThrow(dataAccessException);
        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkBaseLocationWrapperResponse> responseEntity = eLinksServiceImpl.retrieveLocation();
        });

        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

        assertThat(thrown.getErrorMessage()).contains(ELINKS_DATA_STORE_ERROR);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_DATA_STORE_ERROR);


    }

    @Test
    void elinksService_load_location_should_return_elinksException_on_ZeroResult()
        throws JsonProcessingException {



        ElinkBaseLocationResponse elinkLocationResponse = new ElinkBaseLocationResponse();


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);

        when(elinksFeignClient.getLocationDetails()).thenReturn(Response.builder()
            .request(mock(Request.class)).body(body, defaultCharset()).status(HttpStatus.OK.value()).build());
        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(200).build());


        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkBaseLocationWrapperResponse> responseEntity = eLinksServiceImpl.retrieveLocation();
        });

        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());

        assertThat(thrown.getErrorMessage()).contains(ELINKS_ACCESS_ERROR);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ACCESS_ERROR);


    }

    @Test
    void elinksService_load_location_should_return_elinksException_when_FeignException()
            throws JsonProcessingException {

        List<BaseLocationResponse> baseLocationResponses = getBaseLocationResponseData();


        ElinkBaseLocationResponse elinkLocationResponse = new ElinkBaseLocationResponse();
        elinkLocationResponse.setResults(baseLocationResponses);

        FeignException feignExceptionMock = Mockito.mock(FeignException.class);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);

        when(elinksFeignClient.getLocationDetails()).thenThrow(feignExceptionMock);


        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkBaseLocationWrapperResponse> responseEntity = eLinksServiceImpl.retrieveLocation();
        });

        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());

        assertThat(thrown.getErrorMessage()).contains(ELINKS_ACCESS_ERROR);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ACCESS_ERROR);


    }

    @Test
    void elinksService_load_location_should_return_elinksException_when_http_BAD_REQUEST()
            throws JsonProcessingException {

        List<BaseLocationResponse> baseLocationResponses = getBaseLocationResponseData();


        ElinkBaseLocationResponse elinkLocationResponse = new ElinkBaseLocationResponse();
        elinkLocationResponse.setResults(baseLocationResponses);

        FeignException feignExceptionMock = Mockito.mock(FeignException.class);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);


        when(elinksFeignClient.getLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class))
                .body("", defaultCharset()).status(HttpStatus.BAD_REQUEST.value()).build());
        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class))
                .body("", defaultCharset()).status(HttpStatus.BAD_REQUEST.value()).build());

        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkBaseLocationWrapperResponse> responseEntity = eLinksServiceImpl.retrieveLocation();
        });

        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThat(thrown.getErrorMessage()).contains(ELINKS_ERROR_RESPONSE_BAD_REQUEST);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ERROR_RESPONSE_BAD_REQUEST);


    }


    @Test
    void elinksService_load_location_should_return_elinksException_when_http_UNAUTHORIZED()
            throws JsonProcessingException {

        List<BaseLocationResponse> baseLocationResponses = getBaseLocationResponseData();


        ElinkBaseLocationResponse elinkLocationResponse = new ElinkBaseLocationResponse();
        elinkLocationResponse.setResults(baseLocationResponses);

        FeignException feignExceptionMock = Mockito.mock(FeignException.class);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);


        when(elinksFeignClient.getLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class))
                .body("", defaultCharset()).status(HttpStatus.UNAUTHORIZED.value()).build());
        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class))
                .body("", defaultCharset()).status(HttpStatus.UNAUTHORIZED.value()).build());

        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkBaseLocationWrapperResponse> responseEntity = eLinksServiceImpl.retrieveLocation();
        });

        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        assertThat(thrown.getErrorMessage()).contains(ELINKS_ERROR_RESPONSE_UNAUTHORIZED);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ERROR_RESPONSE_UNAUTHORIZED);


    }

    @Test
    void elinksService_load_location_should_return_elinksException_when_http_FORBIDDEN()
            throws JsonProcessingException {

        List<BaseLocationResponse> baseLocationResponses = getBaseLocationResponseData();


        ElinkBaseLocationResponse elinkLocationResponse = new ElinkBaseLocationResponse();
        elinkLocationResponse.setResults(baseLocationResponses);

        FeignException feignExceptionMock = Mockito.mock(FeignException.class);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);


        when(elinksFeignClient.getLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset()).status(HttpStatus.FORBIDDEN.value()).build());
        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset()).status(HttpStatus.FORBIDDEN.value()).build());

        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkBaseLocationWrapperResponse> responseEntity = eLinksServiceImpl.retrieveLocation();
        });

        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());

        assertThat(thrown.getErrorMessage()).contains(ELINKS_ERROR_RESPONSE_FORBIDDEN);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ERROR_RESPONSE_FORBIDDEN);


    }

    @Test
    void elinksService_load_location_should_return_elinksException_when_http_NOT_FOUND()
            throws JsonProcessingException {

        List<BaseLocationResponse> baseLocationResponses = getBaseLocationResponseData();


        ElinkBaseLocationResponse elinkLocationResponse = new ElinkBaseLocationResponse();
        elinkLocationResponse.setResults(baseLocationResponses);

        FeignException feignExceptionMock = Mockito.mock(FeignException.class);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);


        when(elinksFeignClient.getLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset()).status(HttpStatus.NOT_FOUND.value()).build());


        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset()).status(HttpStatus.NOT_FOUND.value()).build());


        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkBaseLocationWrapperResponse> responseEntity = eLinksServiceImpl.retrieveLocation();
        });

        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.NOT_FOUND.value());

        assertThat(thrown.getErrorMessage()).contains(ELINKS_ERROR_RESPONSE_NOT_FOUND);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ERROR_RESPONSE_NOT_FOUND);


    }

    @Test
    void elinksService_load_location_should_return_elinksException_when_http_TOO_MANY_REQUESTS()
            throws JsonProcessingException {

        List<BaseLocationResponse> baseLocationResponses = getBaseLocationResponseData();


        ElinkBaseLocationResponse elinkLocationResponse = new ElinkBaseLocationResponse();
        elinkLocationResponse.setResults(baseLocationResponses);

        FeignException feignExceptionMock = Mockito.mock(FeignException.class);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);


        when(elinksFeignClient.getLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class))
                .body("", defaultCharset()).status(HttpStatus.TOO_MANY_REQUESTS.value()).build());


        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class))
                .body("", defaultCharset()).status(HttpStatus.TOO_MANY_REQUESTS.value()).build());

        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkBaseLocationWrapperResponse> responseEntity = eLinksServiceImpl.retrieveLocation();
        });

        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());

        assertThat(thrown.getErrorMessage()).contains(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS);


    }

    @Test
    void elinksService_cleanUpData_should_return_success_msg_with_status_200() throws JsonProcessingException {

        eLinksServiceImpl.cleanUpElinksResponses();
        Mockito.verify(elinksResponsesRepository,Mockito.times(1))
                .deleteByCreatedDateBefore(any());

    }

    @Test
    void elinksService_cleanUpData_should_return_failure() throws JsonProcessingException {

        DataAccessException dataAccessException = mock(DataAccessException.class);
        doThrow(dataAccessException).when(elinksResponsesRepository).deleteByCreatedDateBefore(any());
        eLinksServiceImpl.cleanUpElinksResponses();

        Mockito.verify(elinkDataExceptionHelper,Mockito.times(1))
                .auditException(any(),any(),any(),any(),any(),any(),any(),any(), any());

    }

    @Test
    void elinksService_deleteJohProfiles_should_return_success_with_zero_profiles() throws JsonProcessingException {

        eLinksServiceImpl.deleteJohProfiles(LocalDateTime.now());
        Mockito.verify(profileRepository,Mockito.times(1))
                .findByDeletedFlag(anyBoolean());

        Mockito.verify(profileRepository,Mockito.times(0))
                .deleteByDeletedOnBeforeAndDeletedFlag(any(),any());

        Mockito.verify(elinksPeopleDeleteService,Mockito.times(0))
                .clearDeletedPeople(any());

        Mockito.verify(elinkDataExceptionHelper,Mockito.times(0))
                .auditException(any(),any());

    }

    @Test
    void elinksService_deleteJohProfiles_should_return_failure_msg() throws JsonProcessingException {

        ReflectionTestUtils.setField(eLinksServiceImpl, "delJohProfiles",
                false);

        eLinksServiceImpl.deleteJohProfiles(LocalDateTime.now());
        Mockito.verify(profileRepository,Mockito.times(0))
                .findByDeletedOnBeforeAndDeletedFlag(any(),any());

        Mockito.verify(profileRepository,Mockito.times(0))
                .deleteByDeletedOnBeforeAndDeletedFlag(any(),any());

        Mockito.verify(authorisationRepository,Mockito.times(0))
                .deleteByPersonalCodeIn(any());

        Mockito.verify(appointmentsRepository,Mockito.times(0))
                .deleteByPersonalCodeIn(any());

        Mockito.verify(judicialRoleTypeRepository,Mockito.times(0))
                .deleteByPersonalCodeIn(any());

        Mockito.verify(elinkDataExceptionHelper,Mockito.times(0))
                .auditException(any(),any());

    }

    @Test
    void elinksService_deleteJohProfiles_should_return_success_with_few_profiles() throws JsonProcessingException {
        var userProfile = buildUserProfile();
        when(profileRepository.findByDeletedFlag(any()))
                .thenReturn(Collections.singletonList(userProfile));

        eLinksServiceImpl.deleteJohProfiles(LocalDateTime.now());

        Mockito.verify(profileRepository,Mockito.times(1))
                .findByDeletedFlag(anyBoolean());

        Mockito.verify(elinksPeopleDeleteService,Mockito.times(1))
                .clearDeletedPeople(any());

        Mockito.verify(elinkDataExceptionHelper,Mockito.times(1))
                .auditException(any(),any());
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
        userProfile.setEmailId("abc@gmail.com");
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


    private List<BaseLocationResponse> getBaseLocationResponseData() {


        BaseLocationResponse baseLocationResponse = new BaseLocationResponse();
        baseLocationResponse.setId("1");
        baseLocationResponse.setName("Aberconwy");
        baseLocationResponse.setTypeId("46");
        baseLocationResponse.setParentId("1722");
        baseLocationResponse.setTypeId("28");
        baseLocationResponse.setCreatedAt("2023-04-12T16:42:35Z");
        baseLocationResponse.setUpdatedAt("2023-04-12T16:42:35Z");

        return List.of(baseLocationResponse);

    }

}
