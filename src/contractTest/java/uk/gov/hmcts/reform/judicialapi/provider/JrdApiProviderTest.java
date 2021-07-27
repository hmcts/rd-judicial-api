package uk.gov.hmcts.reform.judicialapi.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.VersionSelector;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
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
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.domain.BaseLocationType;
import uk.gov.hmcts.reform.judicialapi.domain.RegionType;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.impl.JudicialUserServiceImpl;


import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Provider("referenceData_judicial")
@PactBroker(scheme = "${PACT_BROKER_SCHEME:http}",
        host = "${PACT_BROKER_URL:localhost}",
        port = "${PACT_BROKER_PORT:80}", consumerVersionSelectors = {
        @VersionSelector(tag = "master")})
@ContextConfiguration(classes = {JrdUsersController.class, JudicialUserServiceImpl.class})
@TestPropertySource(properties = {"defaultPageSize=10"})
@IgnoreNoPactsToVerify
public class JrdApiProviderTest {

    @Autowired
    JrdUsersController jrdUsersController;

    @MockBean
    UserProfileRepository userProfileRepository;

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
        BaseLocationType baseLocationType = new BaseLocationType();
        baseLocationType.setBaseLocationId("1");

        RegionType regionType = new RegionType();
        regionType.setRegionId("1");
        regionType.setRegionDescEn("default");

        Appointment appointment = new Appointment();
        appointment.setOfficeAppointmentId(12L);
        appointment.setBaseLocationType(baseLocationType);
        appointment.setRegionType(regionType);
        appointment.setIsPrincipleAppointment(Boolean.TRUE);

        Authorisation authorisation = new Authorisation();
        authorisation.setOfficeAuthId(1234L);
        authorisation.setJurisdiction("Languages");

        List<Authorisation> authorisations = Collections.singletonList(authorisation);
        List<Appointment> appointments = Collections.singletonList(appointment);

        UserProfile userProfile = new UserProfile();
        userProfile.setSidamId("44362987-4b00-f2e7-4ff8-761b87f16bf9");
        userProfile.setAppointments(appointments);
        userProfile.setAuthorisations(authorisations);

        List<UserProfile> userProfiles = Collections.singletonList(userProfile);

        Page<UserProfile> pagedUserProfiles = new PageImpl<>(userProfiles);
                
        when(userProfileRepository.findBySidamIdIn(anyList(),any())).thenReturn(pagedUserProfiles);
    }

}