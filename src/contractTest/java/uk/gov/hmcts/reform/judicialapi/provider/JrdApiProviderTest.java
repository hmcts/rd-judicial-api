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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.judicialapi.controller.JrdUsersController;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.domain.BaseLocationType;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.domain.RegionType;
import uk.gov.hmcts.reform.judicialapi.domain.RegionMapping;
import uk.gov.hmcts.reform.judicialapi.domain.ServiceCodeMapping;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.repository.ServiceCodeMappingRepository;
import uk.gov.hmcts.reform.judicialapi.repository.RegionMappingRepository;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.impl.JudicialUserServiceImpl;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;
import uk.gov.hmcts.reform.judicialapi.validator.RefreshUserValidator;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.when;
import static java.nio.charset.Charset.defaultCharset;

@ExtendWith(SpringExtension.class)
@Provider("referenceData_judicial")
@PactBroker(scheme = "${PACT_BROKER_SCHEME:http}",
        host = "${PACT_BROKER_URL:localhost}",
        port = "${PACT_BROKER_PORT:80}", consumerVersionSelectors = {
        @VersionSelector(tag = "master")})
@ContextConfiguration(classes = {JrdUsersController.class, JudicialUserServiceImpl.class})
@TestPropertySource(properties = {"defaultPageSize=10", "refresh.pageSize=10", "refresh.sortColumn=objectId"})
@IgnoreNoPactsToVerify
public class JrdApiProviderTest {

    @Autowired
    JrdUsersController jrdUsersController;

    @MockBean
    UserProfileRepository userProfileRepository;

    @MockBean
    ServiceCodeMappingRepository serviceCodeMappingRepository;

    @MockBean
    RegionMappingRepository regionMappingRepository;

    @MockBean
    RefreshUserValidator refreshUserValidator;

    @MockBean
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
        testTarget.setControllers(jrdUsersController);
        if (nonNull(context)) {
            context.setTarget(testTarget);
        }

    }

    @State({"User profile details exist"})
    public void toReturnUserProfilesDetails() {
        Page<UserProfile> pagedUserProfiles = getPageUserProfiles();

        when(userProfileRepository.findBySidamIdIn(anyList(),any())).thenReturn(pagedUserProfiles);
    }


    @State({"User profile details exist for the search request provided"})
    public void toReturnUserProfilesDetailsForTheGivenSearchRequest() {
        var userProfile = new UserProfile();
        userProfile.setSidamId("44362987-4b00-f2e7-4ff8-761b87f16bf9");
        userProfile.setFullName("testFullName");
        userProfile.setKnownAs("testKnownAs");
        userProfile.setSurname("surname");
        userProfile.setEjudiciaryEmailId("test@test.com");
        userProfile.setPostNominals("Dr");

        var serviceCodeMapping = ServiceCodeMapping
                .builder()
                .ticketCode("testTicketCode")
                .build();

        var userProfiles = List.of(userProfile);

        when(serviceCodeMappingRepository.findByServiceCodeIgnoreCase(any())).thenReturn(List.of(serviceCodeMapping));
        when(userProfileRepository.findBySearchString(any(),any(),any(), anyList()))
                .thenReturn(userProfiles);
    }

    @State({"return judicial user profiles along with their active appointments and authorisations"})
    public void toReturnUserProfilesDetailsForRefreshUserProfile() throws JsonProcessingException {

        doNothing().when(refreshUserValidator).shouldContainOnlyOneInputParameter(any());
        when(refreshUserValidator.isStringNotEmptyOrNotNull(any())).thenReturn(Boolean.TRUE);
        when(refreshUserValidator.isListNotEmptyOrNotNull(any())).thenReturn(Boolean.FALSE);

        var mapper = new ObjectMapper();
        var lrdOrgInfoServiceResponse = new LrdOrgInfoServiceResponse();
        lrdOrgInfoServiceResponse.setServiceCode("BFA1");
        lrdOrgInfoServiceResponse.setCcdServiceName("CMC");
        String body = mapper.writeValueAsString(List.of(lrdOrgInfoServiceResponse));
        when(locationReferenceDataFeignClient.getLocationRefServiceMapping(any()))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());

        when(serviceCodeMappingRepository.fetchTicketCodeFromServiceCode(any())).thenReturn(List.of("373"));

        var serviceCodeMapping = new ServiceCodeMapping();
        serviceCodeMapping.setServiceId(1L);
        serviceCodeMapping.setTicketCode("368");
        serviceCodeMapping.setServiceCode("BBA3");
        serviceCodeMapping.setServiceDescription("Social Security and Child Support");

        when(serviceCodeMappingRepository.findAllServiceCodeMapping()).thenReturn(List.of(serviceCodeMapping));
        var regionMapping = new RegionMapping();
        regionMapping.setJrdRegionId("1");
        regionMapping.setRegionId("1");
        regionMapping.setRegion("National");
        regionMapping.setJrdRegion("National");
        when(regionMappingRepository.findAllRegionMappingData()).thenReturn(List.of(regionMapping));

        Page<UserProfile> pagedUserProfiles = getPageUserProfiles();
        when(userProfileRepository.fetchUserProfileByObjectIds(anyList(),any())).thenReturn(pagedUserProfiles);
        when(userProfileRepository.fetchUserProfileByServiceNames(anySet(),anyList(),any()))
                .thenReturn(pagedUserProfiles);
        when(userProfileRepository.fetchUserProfileBySidamIds(anyList(),any())).thenReturn(pagedUserProfiles);
        when(userProfileRepository.fetchUserProfileByAll(any())).thenReturn(pagedUserProfiles);

    }

    @NotNull
    private Page<UserProfile> getPageUserProfiles() {
        var baseLocationType = new BaseLocationType();
        baseLocationType.setBaseLocationId("1");
        baseLocationType.setBaseLocationId("Social Entitlement");
        baseLocationType.setCircuit("First Tier Tribunal");

        var regionType = new RegionType();
        regionType.setRegionId("1");
        regionType.setRegionDescEn("default");
        regionType.setRegionDescCy("default");

        var appointment = new Appointment();
        appointment.setOfficeAppointmentId(12L);
        appointment.setBaseLocationType(baseLocationType);
        appointment.setRegionType(regionType);
        appointment.setRegionId("1");
        appointment.setIsPrincipleAppointment(Boolean.TRUE);

        var authorisation = new Authorisation();
        authorisation.setOfficeAuthId(1234L);
        authorisation.setJurisdiction("Languages");

        var authorisations = Collections.singletonList(authorisation);
        var appointments = Collections.singletonList(appointment);

        var userProfile = new UserProfile();
        userProfile.setSidamId("44362987-4b00-f2e7-4ff8-761b87f16bf9");
        userProfile.setObjectId("fcb4f03c-4b3f-4c3c-bf3a-662b4557b470");
        userProfile.setAppointments(appointments);
        userProfile.setAuthorisations(authorisations);
        userProfile.setEjudiciaryEmailId("e@mail.com");

        var judicialRoleType = new JudicialRoleType();
        userProfile.setJudicialRoleTypes(Collections.singletonList(judicialRoleType));

        var userProfiles = Collections.singletonList(userProfile);

        return new PageImpl<>(userProfiles);
    }

}