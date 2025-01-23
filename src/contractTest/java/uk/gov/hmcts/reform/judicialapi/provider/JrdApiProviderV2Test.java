package uk.gov.hmcts.reform.judicialapi.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.VersionSelector;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.JrdElinkController;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JrdRegionMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.LocationMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.RegionType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ServiceCodeMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JrdRegionMappingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationMapppingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ServiceCodeMappingRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;
import uk.gov.hmcts.reform.judicialapi.elinks.service.impl.ElinkUserServiceImpl;
import uk.gov.hmcts.reform.judicialapi.elinks.validator.ElinksRefreshUserValidator;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Provider("referenceData_judicialv2")
@PactBroker(scheme = "${PACT_BROKER_SCHEME:http}",
        host = "${PACT_BROKER_URL:localhost}",
        port = "${PACT_BROKER_PORT:80}", consumerVersionSelectors = {
        @VersionSelector(tag = "master")})
@ContextConfiguration(classes = {JrdElinkController.class, ElinkUserServiceImpl.class})
@TestPropertySource(properties = {"defaultPageSize=10", "refresh.pageSize=10", "refresh.sortColumn=objectId"})
@IgnoreNoPactsToVerify
@SuppressWarnings("checkstyle:Indentation")
public class JrdApiProviderV2Test {

    @Autowired
    JrdElinkController jrdElinkController;

    @MockitoBean
    ProfileRepository userProfileRepository;

    @MockitoBean
    LocationMapppingRepository locationMapppingRepository;

    @MockitoBean
    JudicialRoleTypeRepository judicialRoleTypeRepository;

    @MockitoBean
    AuthorisationsRepository authorisationsRepository;

    @MockitoBean
    AppointmentsRepository appointmentsRepository;

    @MockitoBean
    ElinksRefreshUserValidator elinksRefreshUserValidator;

    @MockitoBean
    @Qualifier("elinksServiceCodeMappingRepository")
    ServiceCodeMappingRepository serviceCodeMappingRepository;

    @MockitoBean
    JrdRegionMappingRepository regionMappingRepository;


    @MockitoBean
    LocationReferenceDataFeignClient locationReferenceDataFeignClient;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        if (context != null) {
            context.verifyInteraction();
        }
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        MockMvcTestTarget testTarget = new MockMvcTestTarget();
        testTarget.setControllers(jrdElinkController);
        if (nonNull(context)) {
            context.setTarget(testTarget);
        }

    }


    @State({"return judicial user profiles"})
    public void toReturnUserProfilesDetailsForTheGivenSearchRequestTypeAhead() {
        var userSearchResponseWrapper = new UserSearchResponseWrapper();
        userSearchResponseWrapper.setIdamId("44362987-4b00-f2e7-4ff8-761b87f16bf9");
        userSearchResponseWrapper.setFullName("testFullName");
        userSearchResponseWrapper.setKnownAs("testKnownAs");
        userSearchResponseWrapper.setSurname("surname");
        userSearchResponseWrapper.setEmailId("test@test.com");
        userSearchResponseWrapper.setTitle("Family Judge");
        userSearchResponseWrapper.setPersonalCode("1234");
        userSearchResponseWrapper.setPostNominals("Mr");
        userSearchResponseWrapper.setInitials("I N");

        var serviceCodeMapping = ServiceCodeMapping
                .builder()
                .ticketCode("testTicketCode")
                .build();

        var userProfiles = List.of(userSearchResponseWrapper);

        when(serviceCodeMappingRepository.findByServiceCodeIgnoreCase(any())).thenReturn(List.of(serviceCodeMapping));
        when(userProfileRepository.findBySearchForString(any(),any(),any(), anyList(), anyList()))
                .thenReturn(userProfiles);
    }


    @State({"return judicial user profiles v2 along with their active appointments and authorisations"})
    public void toReturnUserProfilesDetailsForRefreshUserProfile() throws JsonProcessingException {

        doNothing().when(elinksRefreshUserValidator).shouldContainOnlyOneInputParameter(any());
        when(elinksRefreshUserValidator.isStringNotEmptyOrNotNull(any())).thenReturn(Boolean.TRUE);
        when(elinksRefreshUserValidator.isListNotEmptyOrNotNull(any())).thenReturn(Boolean.FALSE);

        var mapper = new ObjectMapper();
        var lrdOrgInfoServiceResponse = new LrdOrgInfoServiceResponse();
        lrdOrgInfoServiceResponse.setServiceCode("BFA1");
        lrdOrgInfoServiceResponse.setCcdServiceName("CMC");
        String body = mapper.writeValueAsString(List.of(lrdOrgInfoServiceResponse));
        when(locationReferenceDataFeignClient.getLocationRefServiceMapping(any()))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, Charset.defaultCharset()).status(201).build());

        when(serviceCodeMappingRepository.fetchTicketCodeFromServiceCode(any())).thenReturn(List.of("373"));

        var serviceCodeMapping = new ServiceCodeMapping();
        serviceCodeMapping.setServiceId(1L);
        serviceCodeMapping.setTicketCode("368");
        serviceCodeMapping.setServiceCode("BBA3");
        serviceCodeMapping.setServiceDescription("Social Security and Child Support");

        when(serviceCodeMappingRepository.findAllServiceCodeMapping()).thenReturn(List.of(serviceCodeMapping));
        var regionMapping = new JrdRegionMapping();
        regionMapping.setJrdRegionId("1");
        regionMapping.setRegionId("1");
        regionMapping.setRegion("National");
        regionMapping.setJrdRegion("National");
        when(regionMappingRepository.findAll()).thenReturn(List.of(regionMapping));

        var locationMappings1 = new LocationMapping();
        locationMappings1.setEpimmsId("1");
        locationMappings1.setJudicialBaseLocationId("1");
        locationMappings1.setServiceCode("service code");

        List<LocationMapping> locationMappingList = new ArrayList<>();
        locationMappingList.add(locationMappings1);

        when(locationMapppingRepository.fetchServiceCodefromLocationId(any()))
            .thenReturn(List.of("service code","service code1"));
        Page<UserProfile> pagedUserProfiles = getPageUserProfiles();
        when(userProfileRepository.fetchUserProfileByObjectIds(anyList(),any())).thenReturn(pagedUserProfiles);
        when(userProfileRepository.fetchUserProfileByServiceNames(anySet(),anyList(),any()))
                .thenReturn(pagedUserProfiles);
        when(userProfileRepository.fetchUserProfileBySidamIds(anyList(),any())).thenReturn(pagedUserProfiles);
    }

    @NotNull
    private Page<UserProfile> getPageUserProfiles() {

        var baseLocationType = new BaseLocation();
        baseLocationType.setBaseLocationId("1");
        baseLocationType.setName("name");
        baseLocationType.setTypeId("typeId");
        baseLocationType.setParentId("parentId");
        baseLocationType.setJurisdictionId("jurisdictionId");
        baseLocationType.setStartDate(LocalDate.now());
        baseLocationType.setEndDate(LocalDate.now());
        baseLocationType.setCreatedAt(LocalDateTime.now());
        baseLocationType.setUpdatedAt(LocalDateTime.now());

        var regionType = new RegionType();
        regionType.setRegionId("1");
        regionType.setRegionDescEn("default");
        regionType.setRegionDescCy("default");

        var locationMappings1 = new LocationMapping();
        locationMappings1.setEpimmsId("1");
        locationMappings1.setJudicialBaseLocationId("1");
        locationMappings1.setServiceCode("service code");

        List<LocationMapping> locationMappingList = new ArrayList<>();
        locationMappingList.add(locationMappings1);

        var locationMappings2 = new LocationMapping();
        locationMappings2.setEpimmsId("2");
        locationMappings2.setJudicialBaseLocationId("2");
        locationMappings2.setServiceCode("service code1");
        locationMappingList.add(locationMappings2);

        var appointment = new Appointment();
        appointment.setOfficeAppointmentId(12L);
        appointment.setStartDate(LocalDate.now());
        appointment.setEndDate(LocalDate.now());
        appointment.setCreatedDate(LocalDateTime.now());
        appointment.setLastLoadedDate(LocalDateTime.now());
        appointment.setRegionType(regionType);
        appointment.setRegionId("1");
        appointment.setIsPrincipleAppointment(Boolean.TRUE);
        appointment.setPersonalCode("testPersonalCode");
        appointment.setEpimmsId("testEpimmsId");
        appointment.setAppointmentType("testAppType");
        appointment.setAppointmentMapping("testAppMapping");
        appointment.setType("testType");
        appointment.setAppointmentId("testAppointmentID");
        appointment.setRoleNameId("testRoleName");
        appointment.setContractTypeId("testContractTypeId");
        appointment.setLocation("testLocation");
        appointment.setJoBaseLocationId("testjobaselocationid");
        appointment.setBaseLocationId("testBaseLocID");
        appointment.setBaseLocationType(baseLocationType);

        var authorisation = new Authorisation();
        authorisation.setOfficeAuthId(1234L);
        authorisation.setJurisdiction("Languages");
        authorisation.setStartDate(LocalDate.now());
        authorisation.setEndDate(LocalDate.now().plusDays(1));
        authorisation.setCreatedDate(LocalDateTime.now());
        authorisation.setLastUpdated(LocalDateTime.now());
        authorisation.setLowerLevel("lower level");
        authorisation.setPersonalCode("Personal code");
        authorisation.setTicketCode("Ticket code");
        authorisation.setAppointmentId("testAppointmentID");
        authorisation.setAuthorisationId("testAuthorisationID");
        authorisation.setJurisdictionId("testJurisdictionID");

        var authorisations = Collections.singletonList(authorisation);
        var appointments = Collections.singletonList(appointment);

        var userProfile = new UserProfile();
        userProfile.setSidamId("44362987-4b00-f2e7-4ff8-761b87f16bf9");
        userProfile.setObjectId("fcb4f03c-4b3f-4c3c-bf3a-662b4557b470");
        userProfile.setAppointments(appointments);
        userProfile.setAuthorisations(authorisations);
        userProfile.setEmailId("e@mail.com");
        userProfile.setPersonalCode("testPersonalCode");
        userProfile.setKnownAs("testKnownAs");
        userProfile.setSurname("testSurname");
        userProfile.setFullName("testFullName");
        userProfile.setPostNominals("testPostNominals");
        userProfile.setLastWorkingDate(LocalDate.now());
        userProfile.setCreatedDate(LocalDateTime.now());
        userProfile.setLastLoadedDate(LocalDateTime.now());
        userProfile.setActiveFlag(Boolean.TRUE);
        userProfile.setPersonalCode("1234");
        userProfile.setInitials("I.N");
        userProfile.setTitle("testTitle");
        userProfile.setRetirementDate(LocalDate.now());
        userProfile.setDeletedFlag(Boolean.FALSE);
        userProfile.setDeletedOn(LocalDateTime.now());

        var judicialRoleType = new JudicialRoleType();
        judicialRoleType.setRoleId(1L);
        judicialRoleType.setPersonalCode("testPersonalCode");
        judicialRoleType.setTitle("testTitle");
        judicialRoleType.setStartDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        judicialRoleType.setEndDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        judicialRoleType.setJurisdictionRoleNameId("testJurisdictionRoleNameId");
        userProfile.setJudicialRoleTypes(Collections.singletonList(judicialRoleType));

        var userProfiles = Collections.singletonList(userProfile);

        return new PageImpl<>(userProfiles);
    }

}