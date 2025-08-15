package uk.gov.hmcts.reform.judicialapi.controller;


import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.ROW_MAPPER;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.SqlContants.GET_DISTINCT_SIDAM_ID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.TestTopicPublishController;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"AbbreviationAsWordInName", "MemberName", "warnings"})
class TestTopicPublishControllerTest {

    @InjectMocks
    TestTopicPublishController eLinksController;

    @Mock
    JdbcTemplate jdbcTemplate;

    @Mock
    ElinkTopicPublisher elinkTopicPublisher;

    @Spy
    PublishSidamIdService publishSidamIdService;


    @Test
    void test_publish_to_topic() {

        List<String> sidamIds = IntStream.rangeClosed(1, 10)
            .mapToObj(i -> String.format("ID%03d", i))
            .toList();
        when(jdbcTemplate.query(GET_DISTINCT_SIDAM_ID, ROW_MAPPER)).thenReturn(sidamIds);
        when(jdbcTemplate.update(
            anyString(),
            any(Object[].class)
        )).thenReturn(1);
        ResponseEntity<SchedulerJobStatusResponse> actual = eLinksController.publishSidamIdToAsb();
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(elinkTopicPublisher, times(1)).sendMessage(any(), anyString());
        verify(jdbcTemplate, times(1)).update(anyString(), any(), anyInt());
        SchedulerJobStatusResponse res = SchedulerJobStatusResponse.builder().id("10")
            .jobStatus("success").sidamIdsCount(10).statusCode(HttpStatus.OK.value()).build();
        assertEquals("10",res.getId());
        assertEquals("success", res.getJobStatus());
        assertEquals(HttpStatus.OK.value(),res.getStatusCode());

    }

    @Test
    void test_publish_to_topic_post() {

        List<String> sidamIds = IntStream.rangeClosed(1, 10)
            .mapToObj(i -> String.format("ID%03d", i))
            .toList();
        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("cmc", null,
            sidamIds, null);

        ResponseEntity<SchedulerJobStatusResponse> actual = eLinksController
            .publishSidamIdToAsbIdsFromReqBody(refreshRoleRequest);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(elinkTopicPublisher, times(1)).sendMessage(any(), anyString());
        SchedulerJobStatusResponse res = SchedulerJobStatusResponse.builder().id("10")
            .jobStatus("success").sidamIdsCount(10).statusCode(HttpStatus.OK.value()).build();
        assertEquals("10",res.getId());
        assertEquals("success", res.getJobStatus());
        assertEquals(HttpStatus.OK.value(),res.getStatusCode());

    }


}