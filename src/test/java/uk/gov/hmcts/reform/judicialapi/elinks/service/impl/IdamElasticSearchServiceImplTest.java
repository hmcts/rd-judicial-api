package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.feign.IdamFeignClient;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkIdamWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamOpenIdTokenResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataExceptionHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.SendEmail;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.SIDAM_IDS_UPDATED;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class IdamElasticSearchServiceImplTest {

    private final IdamFeignClient idamClientMock = spy(IdamFeignClient.class);
    private final IdamTokenConfigProperties tokenConfigProperties = new IdamTokenConfigProperties();
    private final IdamOpenIdTokenResponse openIdTokenResponseMock = mock(IdamOpenIdTokenResponse.class);
    @InjectMocks
    private IdamElasticSearchServiceImpl idamElasticSearchServiceImpl;
    JdbcTemplate jdbcTemplate =  mock(JdbcTemplate.class);

    @Mock
    ElinkDataExceptionHelper elinkDataExceptionHelper;

    @Spy
    private ProfileRepository userProfileRepository;

    @Mock
    private ElinkDataExceptionRepository elinkDataExceptionRepository;

    @Mock
    SendEmail sendEmail;


    @Mock
    ElinkDataIngestionSchedularAudit elinkDataIngestionSchedularAudit;

    public static final String CLIENT_AUTHORIZATION =
            "eyjfddsfsdfsdfdj03903.dffkljfke932rjf032j02f3--fskfljdskls-fdkldskll";


    @BeforeEach
    void setUP() {
        byte[] base64UserDetails = Base64.getDecoder().decode("ZHVtbXl2YWx1ZUBobWN0cy5uZXQ6SE1DVFMxMjM0");
        byte[] clientAuth = Base64.getDecoder().decode("cmQteHl6LWFwaTp4eXo");
        tokenConfigProperties.setClientId("234342332");
        tokenConfigProperties.setClientAuthorization(new String(clientAuth));
        tokenConfigProperties.setAuthorization(new String(base64UserDetails));
        tokenConfigProperties.setRedirectUri("http://idam-api.aat.platform.hmcts.net");
        tokenConfigProperties.setUrl("http://127.0.0.1:5000");

        idamElasticSearchServiceImpl.props = tokenConfigProperties;
        idamElasticSearchServiceImpl.recordsPerPage = 1;
        idamElasticSearchServiceImpl.page = "1";
        idamElasticSearchServiceImpl.idamSearchQuery = "(roles:judiciary) AND lastModified:>now-%sh";
        idamElasticSearchServiceImpl.idamFindQuery = "ssoid:";
    }

    @Test
    void getBearerToken() {
        when(openIdTokenResponseMock.getAccessToken()).thenReturn(CLIENT_AUTHORIZATION);
        when(idamClientMock.getOpenIdToken(any())).thenReturn(openIdTokenResponseMock);
        String actualToken = idamElasticSearchServiceImpl.getIdamBearerToken(LocalDateTime.now());
        assertThat(actualToken).isEqualTo(CLIENT_AUTHORIZATION);
        verify(openIdTokenResponseMock, times(1)).getAccessToken();
        verify(idamClientMock, times(1)).getOpenIdToken(any());
    }

    @Test
    void getBearerTokenWithException() {
        when(openIdTokenResponseMock.getAccessToken()).thenReturn(CLIENT_AUTHORIZATION);
        assertThrows(ElinksException.class, () -> idamElasticSearchServiceImpl.getIdamBearerToken(LocalDateTime.now()));
        verify(elinkDataIngestionSchedularAudit,times(1))
            .auditSchedulerStatus(any(),any(),any(),any(),any());
    }

    @Test
    void testSyncFeed() throws JsonProcessingException {
        when(openIdTokenResponseMock.getAccessToken()).thenReturn(CLIENT_AUTHORIZATION);
        when(idamClientMock.getOpenIdToken(any())).thenReturn(openIdTokenResponseMock);

        Set<IdamResponse> users = new HashSet<>();
        users.add(createUser("some@some.com"));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(users);

        Map<String, Collection<String>> map = new HashMap<>();
        Collection<String> list = new ArrayList<>();
        list.add("5");
        map.put("X-Total-Count", list);

        Response response = Response.builder().request(Request.create(Request.HttpMethod.GET, "", new HashMap<>(),
                        Request.Body.empty(), null)).headers(map).body(body, Charset.defaultCharset())
                .status(200).build();
        when(idamClientMock.getUserFeed(anyString(), any())).thenReturn(response);
        when(userProfileRepository.fetchObjectId()).thenReturn(List.of("2234"));

        ResponseEntity<Object> useResponses = idamElasticSearchServiceImpl.getIdamElasticSearchSyncFeed();
        assertThat(response).isNotNull();
        Set<IdamResponse>  idamResponse = (HashSet<IdamResponse>) useResponses.getBody();
        idamResponse.forEach(useResponse -> {
            assertThat(useResponse.getEmail()).isEqualTo("some@some.com");
        });
        verify(idamClientMock, times(1)).getUserFeed(anyString(), any());
        verify(elinkDataIngestionSchedularAudit,times(2))
            .auditSchedulerStatus(any(),any(),any(),any(),any());
    }

    @Test
    void testSidamUpdate() throws JsonProcessingException {
        when(openIdTokenResponseMock.getAccessToken()).thenReturn(CLIENT_AUTHORIZATION);
        when(idamClientMock.getOpenIdToken(any())).thenReturn(openIdTokenResponseMock);

        Set<IdamResponse> users = new HashSet<>();
        IdamResponse idamResponseOne = createUser("some@some.com");
        idamResponseOne.setSsoId("objectId1");
        users.add(idamResponseOne);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(users);

        Map<String, Collection<String>> map = new HashMap<>();
        Collection<String> list = new ArrayList<>();
        list.add("5");
        map.put("X-Total-Count", list);

        Response response = Response.builder().request(Request.create(Request.HttpMethod.GET, "", new HashMap<>(),
                Request.Body.empty(), null)).headers(map).body(body, Charset.defaultCharset())
            .status(200).build();
        when(idamClientMock.getUserFeed(anyString(), any())).thenReturn(response);
        when(userProfileRepository.fetchObjectIdFromCurrentDate()).thenReturn(createUserProfile());
        when(userProfileRepository.fetchObjectId()).thenReturn(List.of("objectId1"));

        ResponseEntity<Object> useResponses = idamElasticSearchServiceImpl.getIdamDetails();
        assertThat(response).isNotNull();
        ElinkIdamWrapperResponse  elinkIdamWrapperResponse = (ElinkIdamWrapperResponse) useResponses.getBody();
        assertEquals(elinkIdamWrapperResponse.getMessage(),SIDAM_IDS_UPDATED);
        verify(idamClientMock, times(2)).getUserFeed(anyString(), any());
        verify(elinkDataIngestionSchedularAudit,times(2))
            .auditSchedulerStatus(any(),any(),any(),any(),any());
    }

    private List<UserProfile> createUserProfile() {
        UserProfile userProfileOne = UserProfile.builder()
            .personalCode("12222").objectId("objectId1").emailId("email@justice").build();
        UserProfile userProfileTwo = UserProfile.builder()
            .personalCode("12223").objectId("objectId12").emailId("email@justice").build();
        return List.of(userProfileOne,userProfileTwo);
    }

    @Test
    void testSyncFeedResponseError() throws JsonProcessingException {
        when(openIdTokenResponseMock.getAccessToken()).thenReturn(CLIENT_AUTHORIZATION);
        when(idamClientMock.getOpenIdToken(any())).thenReturn(openIdTokenResponseMock);

        List<IdamResponse> users = new ArrayList<>();
        users.add(createUser("some@some.com"));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(users);

        Response response = Response.builder().request(Request.create(Request.HttpMethod.GET, "", new HashMap<>(),
                        Request.Body.empty(), null)).body(body, Charset.defaultCharset())
                .status(500).build();
        when(idamClientMock.getUserFeed(anyString(), any())).thenReturn(response);
        assertThrows(ElinksException.class,() -> idamElasticSearchServiceImpl.getIdamElasticSearchSyncFeed());
        verify(elinkDataIngestionSchedularAudit,times(2))
            .auditSchedulerStatus(any(),any(),any(),any(),any());
    }

    @Test
    void testIdamSearchResponseError() throws JsonProcessingException {
        when(openIdTokenResponseMock.getAccessToken()).thenReturn(CLIENT_AUTHORIZATION);
        when(idamClientMock.getOpenIdToken(any())).thenReturn(openIdTokenResponseMock);

        List<IdamResponse> users = new ArrayList<>();
        users.add(createUser("some@some.com"));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(users);

        Response response = Response.builder().request(Request.create(Request.HttpMethod.GET, "", new HashMap<>(),
                Request.Body.empty(), null)).body(body, Charset.defaultCharset())
            .status(500).build();
        when(idamClientMock.getUserFeed(anyString(), any())).thenReturn(response);
        when(userProfileRepository.fetchObjectIdFromCurrentDate()).thenReturn(createUserProfile());
        assertThrows(ElinksException.class,() -> idamElasticSearchServiceImpl.getIdamDetails());
        verify(elinkDataIngestionSchedularAudit,times(2))
            .auditSchedulerStatus(any(),any(),any(),any(),any());
    }


    private IdamResponse createUser(String email) {
        IdamResponse profile = new IdamResponse();
        profile.setActive(true);
        profile.setEmail(email);
        profile.setForename("some");
        profile.setId(UUID.randomUUID().toString());
        profile.setActive(true);
        profile.setSsoId("1234");
        return profile;
    }

    @Test
    @SneakyThrows
    void testLogResponse() {
        List<IdamResponse> users = new ArrayList<>();
        users.add(createUser("some@some.com"));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(users);
        Map<String, Collection<String>> map = new HashMap<>();
        Collection<String> list = new ArrayList<>();
        list.add("5");
        map.put("X-Total-Count", list);
        Response response = spy(Response.builder().request(Request.create(Request.HttpMethod.GET, "", new HashMap<>(),
                        Request.Body.empty(), null)).headers(map).body(body, Charset.defaultCharset())
                .status(200).build());
        invokeMethod(idamElasticSearchServiceImpl, "logIdamResponses", response);
        verify(response, times(2)).status();
    }

    @Test
    @SneakyThrows
    void testLogEmptyResponse() {
        Response nullResponse = spy(Response.builder().request(Request.create(Request.HttpMethod.GET, "",
                new HashMap<>(),
                Request.Body.create((byte[]) null), null)).build());
        invokeMethod(idamElasticSearchServiceImpl, "logIdamResponses", nullResponse);
        assertNull(nullResponse.body());
    }

    @Test
    @SneakyThrows
    void testErrorStatus() {
        List<IdamResponse> users = new ArrayList<>();
        users.add(createUser("some@some.com"));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(users);
        Map<String, Collection<String>> map = new HashMap<>();
        Collection<String> list = new ArrayList<>();
        list.add("5");
        map.put("X-Total-Count", list);
        Response response = spy(Response.builder().request(Request.create(Request.HttpMethod.GET, "",
                        new HashMap<>(),
                        Request.Body.empty(), null)).headers(map).body(body, Charset.defaultCharset())
                .status(500).build());
        invokeMethod(idamElasticSearchServiceImpl, "logIdamResponses", response);
        verify(response, times(3)).status();
    }

    @Test
    void testElasticSearchQuery() {

        List<Timestamp> resultSet = new ArrayList<>(Collections.singleton(java.sql.Timestamp.valueOf(
                LocalDateTime.now().minusDays(1))));
        when(jdbcTemplate.query(anyString(),any(RowMapper.class))).thenReturn(resultSet);
        Long hours = invokeMethod(idamElasticSearchServiceImpl, "idamElasticSearchQueryHours");
        Assert.assertEquals(Long.valueOf(25), hours);
    }

    @Test
    void testElasticSearchQueryMaxIsNull() {
        when(jdbcTemplate.query(anyString(),any(RowMapper.class))).thenReturn(null);
        Long computeHours = invokeMethod(idamElasticSearchServiceImpl, "idamElasticSearchQueryHours");
        Assert.assertEquals(Long.valueOf(72),computeHours);
    }
}
