package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testng.collections.Lists;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.AppointmentsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.AuthorisationsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.ResultsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleDeleteAuditService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"AbbreviationAsWordInName","MemberName"})
class ElinksPeopleDeleteServiceImplTest {

    @Spy
    private JudicialRoleTypeRepository judicialRoleTypeRepository;

    @Spy
    private AuthorisationsRepository authorisationsRepository;

    @Spy
    private AppointmentsRepository appointmentsRepository;

    @Spy
    private ProfileRepository profileRepository;

    @Spy
    private ElinksPeopleDeleteAuditService elinksPeopleDeleteAuditService;

    @InjectMocks
    private ElinksPeopleDeleteServiceImpl elinksPeopleDeleteServiceimpl;

    @Spy
    private PublishSidamIdService publishSidamIdService;

    private ResultsRequest result1;

    @BeforeEach
    void setUP() {


        AppointmentsRequest appointmentsRequest1 = AppointmentsRequest.builder()
            .baseLocationId("baselocId").circuit("circuit").location("location")
            .isPrincipleAppointment(true).startDate("1991-12-19").endDate("2022-12-20")
            .roleName("appointment").contractType("type").type("Courts").build();
        AppointmentsRequest appointmentsRequest2 = AppointmentsRequest.builder()
            .baseLocationId("baselocId").circuit("circuit").location("location")
            .isPrincipleAppointment(true).startDate("1991-12-19").endDate("2022-12-20")
            .roleName("appointment").contractType("type").type("Tribunals").build();
        List<AppointmentsRequest> appointmentsRequests = Arrays.asList(appointmentsRequest1,appointmentsRequest2);

        AuthorisationsRequest authorisation1 = AuthorisationsRequest.builder().jurisdiction("juristriction")
            .ticket("lowerlevel").startDate("1991-12-19")
            .endDate("2022-12-20").ticketCode("ticketId").build();
        AuthorisationsRequest authorisation2 = AuthorisationsRequest.builder().jurisdiction("juristriction")
            .ticket("lowerlevel").startDate("1991-12-19")
            .endDate("2022-12-20").ticketCode("ticketId").build();
        RoleRequest roleRequestOne = RoleRequest.builder().judiciaryRoleId("427").name("name")
            .startDate("1991-12-19").endDate("2024-12-20").build();
        RoleRequest roleRequestTwo = RoleRequest.builder().judiciaryRoleId("427").name("name")
            .startDate("1991-12-19")
            .endDate("2024-12-20").build();
        List<AuthorisationsRequest> authorisations = Arrays.asList(authorisation1,authorisation2);



        result1 = ResultsRequest.builder().personalCode("1234").knownAs("knownas").fullName("fullName")
            .surname("surname").postNominals("postNOmi").email("email").lastWorkingDate("2022-12-20")
            .objectId("objectId").initials("initials").appointmentsRequests(appointmentsRequests)
            .authorisationsRequests(authorisations).judiciaryRoles(List.of(roleRequestOne,roleRequestTwo)).build();
    }

    @Test
    void testdeleteAuth() {
        elinksPeopleDeleteServiceimpl.deleteAuth(result1);
        Mockito.verify(profileRepository, Mockito.times(1)).findByPersonalCodeIn(anyList());
        Mockito.verify(profileRepository, Mockito.times(1)).saveAll(anyList());
    }

    @Test
    void testdeletePeople() {
        elinksPeopleDeleteServiceimpl.deletePeople(result1.getPersonalCode());
        Mockito.verify(profileRepository, Mockito.times(1)).findByPersonalCodeIn(anyList());
        Mockito.verify(profileRepository, Mockito.times(1)).saveAll(anyList());
    }

    @Test
    void testClearDeletePeople() {
        List<UserProfile> userProfiles = new ArrayList<>();
        userProfiles.add(UserProfile.builder()
                .personalCode("1234")
                .surname("surname")
                .fullName("full name").build());
        Mockito.when(profileRepository.deleteByPersonalCodeIn(anyList())).thenReturn(userProfiles);

        elinksPeopleDeleteServiceimpl.clearDeletedPeople(Lists.newArrayList("1234"));
        Mockito.verify(authorisationsRepository,Mockito.times(1)).deleteByPersonalCodeIn(anyList());
        Mockito.verify(appointmentsRepository,Mockito.times(1)).deleteByPersonalCodeIn(anyList());
        Mockito.verify(judicialRoleTypeRepository,Mockito.times(1)).deleteByPersonalCodeIn(anyList());
        Mockito.verify(profileRepository,Mockito.times(1)).deleteByPersonalCodeIn(anyList());
    }

}
