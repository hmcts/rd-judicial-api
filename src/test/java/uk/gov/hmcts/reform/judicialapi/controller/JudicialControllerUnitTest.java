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
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialRoleTypeEntityResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialUserProfileResponse;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;
import uk.gov.hmcts.reform.judicialapi.service.JudicialRoleTypeService;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserProfileService;

@Slf4j
public class JudicialControllerUnitTest {

    @InjectMocks
    private JudicialController judicialController;

    private JudicialRoleType judicialRoleType1;
    private JudicialRoleType judicialRoleType2;
    private JudicialRoleTypeService judicialRoleTypeServiceMock;
    private JudicialRoleTypeEntityResponse judicialRoleTypeEntityResponse;
    private List<JudicialRoleType> judicialRoleTypeList;

    private JudicialUserProfileService judicialUserProfileServiceMock;
    private JudicialUserProfileResponse judicialUserProfileResponseMock;
    private ResponseEntity<?> responseEntityMock;

    @Before
    public void setUp() throws Exception {
        judicialRoleTypeServiceMock = mock(JudicialRoleTypeService.class);

        judicialRoleType1 = new JudicialRoleType("1", "testEn", "testCy");
        judicialRoleType2 = new JudicialRoleType("2", "testEn2", "testCy2");

        judicialRoleTypeList = new ArrayList<>();
        judicialRoleTypeList.add(judicialRoleType1);
        judicialRoleTypeList.add(judicialRoleType2);

        judicialRoleTypeEntityResponse = new JudicialRoleTypeEntityResponse(judicialRoleTypeList);


        judicialUserProfileResponseMock = mock(JudicialUserProfileResponse.class);
        judicialUserProfileServiceMock = mock(JudicialUserProfileService.class);


        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testRetrieveJudicialRoleTypes() {

        final HttpStatus expectedHttpStatus = HttpStatus.OK;

        when(judicialRoleTypeServiceMock.retrieveJudicialRoles()).thenReturn(judicialRoleTypeEntityResponse);

        ResponseEntity<JudicialRoleTypeEntityResponse> actual = judicialController.getJudicialRoles();

        verify(judicialRoleTypeServiceMock, times(1)).retrieveJudicialRoles();

        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
        assertThat(actual.getBody().getJudicialRoleTypes().toString())
                .contains(judicialRoleType1.getRoleId(), judicialRoleType1.getRoleDescEn(), judicialRoleType1.getRoleDescCy());
        assertThat(actual.getBody().getJudicialRoleTypes().toString())
                .contains(judicialRoleType2.getRoleId(), judicialRoleType2.getRoleDescEn(), judicialRoleType2.getRoleDescCy());
    }

    @Test
    public void testRetrieveJudicialUserProfile() {

        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        JudicialUserProfile judicialUserProfile = new JudicialUserProfile("12345","12345","title","knownAs","surname","fullname","postNominals","contractType","workPattern","test@email.com",null,null,true,null,null,null);

        when(judicialUserProfileServiceMock.findJudicialUserprofilebyemailaddress("testing@email.com")).thenReturn(judicialUserProfile);

        ResponseEntity actual = judicialController.retrieveUserProfileByEmail("testing@email.com");

        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
    }


}
