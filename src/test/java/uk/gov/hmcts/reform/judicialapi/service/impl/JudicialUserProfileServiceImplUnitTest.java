package uk.gov.hmcts.reform.judicialapi.service.impl;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialUserProfileResponse;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;
import uk.gov.hmcts.reform.judicialapi.persistence.JudicialUserProfileRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class JudicialUserProfileServiceImplUnitTest {


    @InjectMocks
    private JudicialUserProfileServiceImpl sut;

    private JudicialUserProfileRepository judicialUserProfileRepositoryMock;
    private JudicialUserProfile judicialUserProfileMock;
    private JudicialUserProfileResponse judicialUserProfileResponseMock;

    private List<JudicialUserProfile> judicialUserProfiles;
    private List<JudicialUserProfileResponse> judicialUserProfileResponses;

    @Before
    public void setUp() {

        judicialUserProfileRepositoryMock = mock(JudicialUserProfileRepository.class);
        judicialUserProfileMock = mock(JudicialUserProfile.class);
        judicialUserProfileResponseMock = mock(JudicialUserProfileResponse.class);
        MockitoAnnotations.initMocks(this);

        judicialUserProfiles = new ArrayList<>();
        judicialUserProfileResponses = new ArrayList<>();
    }

//    @Test
//    public void retrieveJudicialRolesTest() {
//
//        judicialUserProfiles.add(judicialUserProfileMock);
//        judicialUserProfileResponses.add(judicialUserProfileResponseMock);
//
//        when(judicialUserProfileRepositoryMock.findAll()).thenReturn(judicialUserProfiles);
//
//       // List<JudicialUserProfileResponse> actual = sut.findJudicialUserProfileByEmailAddress(any(String.class));
//
//       // assertThat(actual).isNotNull();
//
//        verify(judicialUserProfileRepositoryMock, times(1)).findAll();
//     }
}
