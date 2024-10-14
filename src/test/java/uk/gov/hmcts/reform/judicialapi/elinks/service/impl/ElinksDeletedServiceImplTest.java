package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.PaginationRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.response.DeletedResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.response.ElinksDeleteApiResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinksResponsesRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkDeletedWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleDeleteService;
import uk.gov.hmcts.reform.judicialapi.elinks.util.CommonUtil;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksResponsesHelper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.AUDIT_DATA_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDSUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;

@ExtendWith(MockitoExtension.class)
class ElinksDeletedServiceImplTest {

    @Spy
    ElinksFeignClient elinksFeignClient;

    @Spy
    private ProfileRepository profileRepository;

    @Spy
    private DataloadSchedularAuditRepository dataloadSchedularAuditRepository;

    @Spy
    private ElinkDataIngestionSchedularAudit elinkDataIngestionSchedularAudit;

    @Spy
    private ElinksPeopleDeleteService elinksPeopleDeleteService;

    @InjectMocks
    private ELinksServiceImpl elinksServiceImpl;

    private DeletedResponse result1;

    private DeletedResponse result2;

    private PaginationRequest pagination;

    private ElinksDeleteApiResponse elinksApiResponseFirstHit;

    private ElinksDeleteApiResponse elinksApiResponseSecondHit;

    JdbcTemplate jdbcTemplate =  mock(JdbcTemplate.class);

    @Mock
    ElinksResponsesHelper elinksResponsesHelper;

    @Spy
    ElinksResponsesRepository elinksResponsesRepository;

    @Spy
    CommonUtil commonUtil;

    @BeforeEach
    void setUP() {

        ReflectionTestUtils.setField(elinksServiceImpl, "threadPauseTime",
                "2000");
        ReflectionTestUtils.setField(elinksServiceImpl, "lastUpdated",
                "Thu Jan 01 00:00:00 GMT 2015");
        ReflectionTestUtils.setField(elinksServiceImpl, "page",
                "1");
        ReflectionTestUtils.setField(elinksServiceImpl, "isCustomizeUpdatedSince",
            false);


        pagination = PaginationRequest.builder()
                .results(1)
                .pages(1).currentPage(1).resultsPerPage(3).morePages(true).build();


        result1 = DeletedResponse.builder().personalCode("1234").deletedOn("2022-12-20")
                .deleted("true").build();

        result2 = DeletedResponse.builder().personalCode("1234").deletedOn("2022-12-20")
                .deleted("true").build();

        List<DeletedResponse> results = Arrays.asList(result1,result2);

        elinksApiResponseFirstHit = ElinksDeleteApiResponse
            .builder().deletedResponse(results).pagination(pagination).build();


        PaginationRequest paginationFalse = PaginationRequest.builder()
                .results(1)
                .pages(1).currentPage(1).resultsPerPage(3).morePages(false).build();
        elinksApiResponseSecondHit = ElinksDeleteApiResponse
            .builder().deletedResponse(results).pagination(paginationFalse)
                .build();
    }

    @Test
    void loadDeletedWhenAuditEntryPresentSuccess() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(elinksApiResponseFirstHit);
        String body2 = mapper.writeValueAsString(elinksApiResponseSecondHit);

        LocalDateTime dateTime = LocalDateTime.now();

        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenReturn(dateTime);
        Response r1 = Response.builder().request(mock(Request.class)).body(body, defaultCharset()).status(200).build();
        Response r2 = Response.builder().request(mock(Request.class)).body(body2, defaultCharset()).status(200).build();
        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenReturn(r1).thenReturn(r2);

        when(elinksResponsesHelper.saveElinksResponse(eq(DELETEDAPI),eq(r1))).thenReturn(r1);
        when(elinksResponsesHelper.saveElinksResponse(eq(DELETEDAPI),eq(r2))).thenReturn(r2);
        ResponseEntity<ElinkDeletedWrapperResponse> response = elinksServiceImpl.retrieveDeleted();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertThat(response.getBody().getMessage()).isEqualTo(DELETEDSUCCESS);

        verify(elinksFeignClient, times(2)).getDeletedDetails(any(), any(), any());
        org.assertj.core.api.Assertions.assertThat(elinksFeignClient.getDeletedDetails(any(),any(),any())).isNotNull();

        verify(elinkDataIngestionSchedularAudit,times(2))
            .auditSchedulerStatus(any(),any(),any(),any(),any());

    }

    @Test
    void loadDeletedWhenAuditEntryPresentSuccess_withCustomizeUpdatedSince() throws JsonProcessingException {

        ReflectionTestUtils.setField(elinksServiceImpl, "isCustomizeUpdatedSince",
            true);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(elinksApiResponseFirstHit);
        String body2 = mapper.writeValueAsString(elinksApiResponseSecondHit);

        LocalDateTime dateTime = LocalDateTime.now();

        Response r1 = Response.builder().request(mock(Request.class)).body(body, defaultCharset()).status(200).build();
        Response r2 = Response.builder().request(mock(Request.class)).body(body2, defaultCharset()).status(200).build();
        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenReturn(r1).thenReturn(r2);

        when(elinksResponsesHelper.saveElinksResponse(eq(DELETEDAPI),eq(r1))).thenReturn(r1);
        when(elinksResponsesHelper.saveElinksResponse(eq(DELETEDAPI),eq(r2))).thenReturn(r2);
        ResponseEntity<ElinkDeletedWrapperResponse> response = elinksServiceImpl.retrieveDeleted();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertThat(response.getBody().getMessage()).isEqualTo(DELETEDSUCCESS);

        verify(elinksFeignClient, times(2)).getDeletedDetails(any(), any(), any());
        org.assertj.core.api.Assertions.assertThat(elinksFeignClient.getDeletedDetails(any(),any(),any())).isNotNull();

        verify(elinkDataIngestionSchedularAudit,times(2))
            .auditSchedulerStatus(any(),any(),any(),any(),any());

    }

    @Test
    void loadDeletedNullFromElinksTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(elinksApiResponseFirstHit);
        String body2 = mapper.writeValueAsString(elinksApiResponseSecondHit);

        LocalDateTime dateTime = LocalDateTime.now();

        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenReturn(dateTime);

        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenReturn(null);

        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(200).build())
                .thenReturn(Response.builder().request(mock(Request.class))
                        .body(body2, defaultCharset()).status(200).build());

        ResponseEntity<ElinkDeletedWrapperResponse> response = elinksServiceImpl.retrieveDeleted();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertThat(response.getBody().getMessage()).isEqualTo(DELETEDSUCCESS);

        verify(elinksFeignClient, times(2)).getDeletedDetails(any(), any(), any());
        org.assertj.core.api.Assertions.assertThat(elinksFeignClient.getDeletedDetails(any(),any(),any())).isNull();
        verify(elinkDataIngestionSchedularAudit,times(2))
                .auditSchedulerStatus(any(),any(),any(),any(),any());

    }

    @Test
    void loadDeletedWhenAuditEntryNotPresentSuccess() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(elinksApiResponseFirstHit);
        String body2 = mapper.writeValueAsString(elinksApiResponseSecondHit);
        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenReturn(null);

        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(200).build())
                .thenReturn(Response.builder().request(mock(Request.class))
                        .body(body2, defaultCharset()).status(200).build());


        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(200).build())
                .thenReturn(Response.builder().request(mock(Request.class))
                        .body(body2, defaultCharset()).status(200).build());

        ResponseEntity<ElinkDeletedWrapperResponse> response = elinksServiceImpl.retrieveDeleted();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertThat(response.getBody().getMessage()).isEqualTo(DELETEDSUCCESS);


        verify(elinksFeignClient, times(2)).getDeletedDetails(any(), any(), any());

    }


    @Test
    void load_deleted_should_return_elinksException_when_DataAccessException_while_connecting_to_Audit_table() {

        DataAccessException dataAccessException = mock(DataAccessException.class);
        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenThrow(dataAccessException);
        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkDeletedWrapperResponse> responseEntity = elinksServiceImpl.retrieveDeleted();
        });
        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(thrown.getErrorMessage()).contains(AUDIT_DATA_ERROR);
        assertThat(thrown.getErrorDescription()).contains(AUDIT_DATA_ERROR);
    }

    @Test
    void load_deleted_should_return_elinksException_when_ElinksApi_Failure() {

        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenReturn(null);
        FeignException feignExceptionMock = Mockito.mock(FeignException.class);
        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenThrow(feignExceptionMock);

        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkDeletedWrapperResponse> responseEntity = elinksServiceImpl.retrieveDeleted();
        });
        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(thrown.getErrorMessage()).contains(ELINKS_ACCESS_ERROR);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ACCESS_ERROR);
    }

    @Test
    void load_deleted_should_return_elinksException_when_ElinksApi_Response_is_unknown_Format()
            throws JsonProcessingException {

        String body = "{\"test\":\"test\"}";
        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenReturn(null);

        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(200).build());


        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(200).build());

        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkDeletedWrapperResponse> responseEntity = elinksServiceImpl.retrieveDeleted();
        });
        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(thrown.getErrorMessage()).contains(ELINKS_ACCESS_ERROR);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ACCESS_ERROR);

    }

    @Test
    void load_deleted_should_return_elinksException_when_ElinksApi_Response_does_not_have_results()
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        elinksApiResponseFirstHit.setDeletedResponse(null);
        String body = mapper.writeValueAsString(elinksApiResponseFirstHit);

        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenReturn(LocalDateTime.now());

        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(200).build());


        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(200).build());

        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkDeletedWrapperResponse> responseEntity = elinksServiceImpl.retrieveDeleted();
        });
        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(thrown.getErrorMessage()).contains(ELINKS_ACCESS_ERROR);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ACCESS_ERROR);
        verify(elinkDataIngestionSchedularAudit,times(2))
            .auditSchedulerStatus(any(),any(),any(),any(),any());
    }

    @Test
    void load_deleted_should_return_elinksException_when_ElinksApi_Response_does_not_have_pagination()
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        elinksApiResponseFirstHit.setPagination(null);
        String body = mapper.writeValueAsString(elinksApiResponseFirstHit);

        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenReturn(LocalDateTime.now());

        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(200).build());

        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(200).build());

        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkDeletedWrapperResponse> responseEntity = elinksServiceImpl.retrieveDeleted();
        });
        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(thrown.getErrorMessage()).contains(ELINKS_ACCESS_ERROR);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ACCESS_ERROR);
    }

    @Test
    void load_deleted_should_return_elinksException_when_http_bad_request() {

        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenReturn(LocalDateTime.now());

        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset()).status(HttpStatus.BAD_REQUEST.value())
                .build());

        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset()).status(HttpStatus.BAD_REQUEST.value())
                .build());

        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkDeletedWrapperResponse> responseEntity = elinksServiceImpl.retrieveDeleted();
        });
        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(thrown.getErrorMessage()).contains(ELINKS_ERROR_RESPONSE_BAD_REQUEST);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ERROR_RESPONSE_BAD_REQUEST);
        verify(elinkDataIngestionSchedularAudit,times(2))
            .auditSchedulerStatus(any(),any(),any(),any(),any());
    }

    @Test
    void load_deleted_should_return_elinksException_when_http_unauthorised() {

        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenReturn(LocalDateTime.now());

        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset()).status(HttpStatus.UNAUTHORIZED.value())
                .build());

        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset()).status(HttpStatus.UNAUTHORIZED.value())
                .build());

        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkDeletedWrapperResponse> responseEntity = elinksServiceImpl.retrieveDeleted();
        });
        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(thrown.getErrorMessage()).contains(ELINKS_ERROR_RESPONSE_UNAUTHORIZED);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ERROR_RESPONSE_UNAUTHORIZED);

    }

    @Test
    void load_deleted_should_return_elinksException_when_http_forbidden() {

        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenReturn(LocalDateTime.now());

        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset()).status(HttpStatus.FORBIDDEN.value()).build());


        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset()).status(HttpStatus.FORBIDDEN.value()).build());
        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkDeletedWrapperResponse> responseEntity = elinksServiceImpl.retrieveDeleted();
        });
        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(thrown.getErrorMessage()).contains(ELINKS_ERROR_RESPONSE_FORBIDDEN);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ERROR_RESPONSE_FORBIDDEN);

    }

    @Test
    void load_deleted_should_return_elinksException_when_http_not_found() {

        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenReturn(LocalDateTime.now());

        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset()).status(HttpStatus.NOT_FOUND.value())
                .build());


        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset()).status(HttpStatus.NOT_FOUND.value())
                .build());

        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkDeletedWrapperResponse> responseEntity = elinksServiceImpl.retrieveDeleted();
        });
        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(thrown.getErrorMessage()).contains(ELINKS_ERROR_RESPONSE_NOT_FOUND);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ERROR_RESPONSE_NOT_FOUND);

    }

    @Test
    void load_deleted_should_return_elinksException_when_http_too_many_requests() {
        when(dataloadSchedularAuditRepository.findLatestDeletedSchedularEndTime()).thenReturn(LocalDateTime.now());

        when(elinksFeignClient.getDeletedDetails(any(), any(), any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset())
                .status(HttpStatus.TOO_MANY_REQUESTS.value()).build());


        when(elinksResponsesHelper.saveElinksResponse(any(),any())).thenReturn(Response.builder()
                .request(mock(Request.class)).body("", defaultCharset())
                .status(HttpStatus.TOO_MANY_REQUESTS.value()).build());

        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<ElinkDeletedWrapperResponse> responseEntity = elinksServiceImpl.retrieveDeleted();
        });
        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(thrown.getErrorMessage()).contains(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS);

    }
}