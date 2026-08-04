package uk.gov.hmcts.reform.judicialapi.elinks.controller;


import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;
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


    @Test
    void test_publish_to_topic() {

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
