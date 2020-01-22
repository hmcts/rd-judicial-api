package uk.gov.hmcts.reform.judicialapi.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialRoleTypeEntityResponse;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.persistence.JudicialRoleTypeRepository;

@Slf4j
public class JudicialRoleTypeServiceImplUnitTest {

    @InjectMocks
    private JudicialRoleTypeServiceImpl sut;

    private JudicialRoleTypeRepository judicialRoleTypeRepositoryMock;
    private JudicialRoleType judicialRoleType;
    private List<JudicialRoleType> judicialRoleTypeList;

    @Before
    public void setUp() {

        judicialRoleTypeRepositoryMock = mock(JudicialRoleTypeRepository.class);
        MockitoAnnotations.initMocks(this);

        judicialRoleType = new JudicialRoleType("1", "testEn", "testCy");

        judicialRoleTypeList = new ArrayList<>();
        judicialRoleTypeList.add(judicialRoleType);
    }

    @Test
    public void retrieveJudicialRolesTest() {

        when(judicialRoleTypeRepositoryMock.findAll()).thenReturn(judicialRoleTypeList);

        JudicialRoleTypeEntityResponse actual = sut.retrieveJudicialRoles();

        assertThat(actual).isNotNull();
        verify(judicialRoleTypeRepositoryMock, times(1)).findAll();
        assertThat(actual.getJudicialRoleTypes().get(0).getRoleId()).isEqualTo(judicialRoleType.getRoleId());
        assertThat(actual.getJudicialRoleTypes().get(0).getRoleDescEn()).isEqualTo(judicialRoleType.getRoleDescEn());
        assertThat(actual.getJudicialRoleTypes().get(0).getRoleDescCy()).isEqualTo(judicialRoleType.getRoleDescCy());

    }

    @Test(expected = ResourceNotFoundException.class)
    public void retrieveJudicialRolesThrowsResourceNotFoundIfEmpty() {
        sut.retrieveJudicialRoles();
    }
}
