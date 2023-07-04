package uk.gov.hmcts.reform.judicialapi.controller.oidc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.judicialapi.oidc.JwtGrantedAuthoritiesConverter;
import uk.gov.hmcts.reform.judicialapi.repository.IdamRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtGrantedAuthoritiesConverterTest {

    JwtGrantedAuthoritiesConverter converter;
    IdamRepository idamRepositoryMock;
    UserInfo userInfoMock;
    Jwt jwtMock;

    @BeforeEach
    void setUp() {
        idamRepositoryMock = mock(IdamRepository.class);
        userInfoMock = mock(UserInfo.class);
        jwtMock = mock(Jwt.class);
        converter = new JwtGrantedAuthoritiesConverter(idamRepositoryMock);
    }

    @Test
    void test_shouldReturnEmptyAuthorities() {
        final Collection<GrantedAuthority> authorities = converter.convert(jwtMock);

        assertNotNull(authorities);
        assertEquals(0, authorities.size());
        verify(idamRepositoryMock, times(0)).getUserInfo(anyString());
    }

    @Test
    void test_shouldReturnEmptyAuthoritiesWhenClaimNotAvailable() {
        when(jwtMock.hasClaim(anyString())).thenReturn(false);

        final Collection<GrantedAuthority> authorities = converter.convert(jwtMock);

        assertNotNull(authorities);
        assertEquals(0, authorities.size());
        verify(jwtMock, times(1)).hasClaim(anyString());
        verify(idamRepositoryMock, times(0)).getUserInfo(anyString());
    }

    @Test
    void test_shouldReturnEmptyAuthoritiesWhenClaimValueNotEquals() {
        when(jwtMock.hasClaim(anyString())).thenReturn(true);
        when(jwtMock.getClaim(anyString())).thenReturn("Test");

        final Collection<GrantedAuthority> authorities = converter.convert(jwtMock);

        assertNotNull(authorities);
        assertEquals(0, authorities.size());
        verify(jwtMock, times(1)).hasClaim(anyString());
        verify(jwtMock, times(1)).getClaim(anyString());
        verify(idamRepositoryMock, times(0)).getUserInfo(anyString());
    }

    @Test
    void test_shouldReturnEmptyAuthoritiesWhenIdamReturnsNoUsers() {
        List<String> roles = new ArrayList<>();

        when(jwtMock.hasClaim(anyString())).thenReturn(true);
        when(jwtMock.getClaim(anyString())).thenReturn("access_token");
        when(jwtMock.getTokenValue()).thenReturn("access_token");
        when(userInfoMock.getRoles()).thenReturn(roles);
        when(idamRepositoryMock.getUserInfo(anyString())).thenReturn(userInfoMock);

        final Collection<GrantedAuthority> authorities = converter.convert(jwtMock);

        assertNotNull(authorities);
        assertEquals(0, authorities.size());
        verify(jwtMock, times(1)).hasClaim(anyString());
        verify(jwtMock, times(1)).getClaim(anyString());
        verify(jwtMock, times(1)).getTokenValue();
        verify(userInfoMock, times(1)).getRoles();
        verify(idamRepositoryMock, times(1)).getUserInfo(anyString());

    }

    @Test
    void test_shouldReturnEmptyAuthoritiesWhenIdamReturnsUsers() {
        List<String> roles = List.of("lrd-admin");
        when(jwtMock.hasClaim(anyString())).thenReturn(true);
        when(jwtMock.getClaim(anyString())).thenReturn("access_token");
        when(jwtMock.getTokenValue()).thenReturn("access_token");
        when(userInfoMock.getRoles()).thenReturn(roles);
        when(idamRepositoryMock.getUserInfo(anyString())).thenReturn(userInfoMock);

        final Collection<GrantedAuthority> authorities = converter.convert(jwtMock);

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        verify(jwtMock, times(1)).hasClaim(anyString());
        verify(jwtMock, times(1)).getClaim(anyString());
        verify(jwtMock, times(1)).getTokenValue();
        verify(userInfoMock, times(1)).getRoles();
        verify(idamRepositoryMock, times(1)).getUserInfo(anyString());

        final UserInfo userInfo = converter.getUserInfo();

        assertNotNull(userInfo);
    }

}
