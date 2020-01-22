package uk.gov.hmcts.reform.judicialapi.service.impl;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialUserProfileEntityResponse;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;
import uk.gov.hmcts.reform.judicialapi.persistence.JudicialUserProfileRepository;

public class JudicialUserProfileServiceImplUnitTest {

    @InjectMocks
    private JudicialUserProfileServiceImpl sut;

    private JudicialUserProfileRepository judicialUserProfileRepositoryMock;
    private JudicialUserProfile judicialUserProfileMock;


    @Before
    public void setUp() {

        judicialUserProfileRepositoryMock = mock(JudicialUserProfileRepository.class);
        judicialUserProfileMock = mock(JudicialUserProfile.class);
        MockitoAnnotations.initMocks(this);

    }


    @Test
    public void testRetrievejudicialuserprofilebyEmailid() throws Exception {
        JudicialUserProfile userProfile = mock(JudicialUserProfile.class);

        String email = randomAlphabetic(10) + "@usersearch.test".toLowerCase();
        String elinkId = "ElinkId";
        String fullName = "fullName";
        String surName = "Surname";

        when(userProfile.getEmailId()).thenReturn(email);


        when(judicialUserProfileRepositoryMock.findUserProfileByEmailAddress(email)).thenReturn(judicialUserProfileMock);
        when(judicialUserProfileMock.getEmailId()).thenReturn(email);
        when(judicialUserProfileMock.getElinksId()).thenReturn(elinkId);
        when(judicialUserProfileMock.getFullName()).thenReturn(fullName);
        when(judicialUserProfileMock.getSurname()).thenReturn(surName);


        JudicialUserProfileEntityResponse judicialUserProfileEntityResponse = sut.retrieveUserProfile(email);

        assertThat(judicialUserProfileEntityResponse).isNotNull();

        verify(judicialUserProfileRepositoryMock, times(1)).findUserProfileByEmailAddress(any(String.class));

    }

    @Ignore
    @Test(expected = ResourceNotFoundException.class)
    public void retrieveJudicialUserProfileThrowsResourceNotFoundIfEmpty() {
        sut.judicialUserProfileRepository.findUserProfileByEmailAddress("test@net.co.uk");
    }
}
