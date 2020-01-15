package uk.gov.hmcts.reform.judicialapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialRoleTypeResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialUserProfileResponse;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;
import uk.gov.hmcts.reform.judicialapi.service.JudicialRoleTypeService;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserProfileService;

@Slf4j
public class JudicialControllerUnitTest {

    @InjectMocks
    private JudicialController judicialController;

    private JudicialRoleTypeResponse judicialRoleTypeResponseMock;
    private JudicialRoleTypeService judicialRoleTypeServiceMock;
    private List<JudicialRoleTypeResponse> judicialRoleTypeResponseList;

    private JudicialUserProfileService judicialUserProfileServiceMock;
    private JudicialUserProfileResponse judicialUserProfileResponseMock;
    private ResponseEntity<?> responseEntityMock;

    @Before
    public void setUp() throws Exception {
        judicialRoleTypeResponseMock = mock(JudicialRoleTypeResponse.class);
        judicialRoleTypeServiceMock = mock(JudicialRoleTypeService.class);

        judicialRoleTypeResponseList = new ArrayList<>();
        judicialRoleTypeResponseList.add(judicialRoleTypeResponseMock);


        judicialUserProfileResponseMock = mock(JudicialUserProfileResponse.class);
        judicialUserProfileServiceMock = mock(JudicialUserProfileService.class);


        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testRetrieveJudicialRoleTypes() {

        final HttpStatus expectedHttpStatus = HttpStatus.OK;

        when(judicialRoleTypeServiceMock.retrieveJudicialRoles()).thenReturn(judicialRoleTypeResponseList);

        ResponseEntity<List<JudicialRoleTypeResponse>> actual = judicialController.getJudicialRoles();

        verify(judicialRoleTypeServiceMock, times(1)).retrieveJudicialRoles();

        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
    }

    @Test
    public void testRetrieveJudicialUserProfile() {

        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        JudicialUserProfile judicialUserProfile = new JudicialUserProfile("12345","12345","title","knownAs","surname","fullname","postNominals","contractType","workPattern","test@email.com",null,null,true,null,null,null);

        when(judicialUserProfileServiceMock.findJudicialUserProfileByEmailAddress("testing@email.com")).thenReturn(judicialUserProfile);

        ResponseEntity actual = judicialController.retrieveUserProfileByEmail("testing@email.com");

        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
    }


}
