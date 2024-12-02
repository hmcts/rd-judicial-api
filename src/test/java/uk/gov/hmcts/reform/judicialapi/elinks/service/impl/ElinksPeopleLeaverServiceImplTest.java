package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.LeaversResultsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElinksPeopleLeaverServiceImplTest {

    @Mock
    JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ElinksPeopleLeaverServiceImpl elinksPeopleLeaverService;

    @Test
    void shouldInvokeBatchUpdate() {
        LeaversResultsRequest result1 = LeaversResultsRequest.builder().personalCode("1234").leftOn("2022-12-20")
                .objectId("objectId").leaver("true").perId("40291").build();

        List<LeaversResultsRequest> results = Lists.newArrayList(result1);

        elinksPeopleLeaverService.processLeavers(results);

        verify(jdbcTemplate, times(1)).batchUpdate(any(), any(), anyInt(), any());
    }

    @Test
    void shouldNotInvokeBatchUpdate() {
        List<LeaversResultsRequest> results = Lists.newArrayList();

        elinksPeopleLeaverService.processLeavers(results);

        verify(jdbcTemplate, times(0)).batchUpdate(any(), any(), anyInt(), any());
    }

    @Test
    void shouldThrowException() {
        LeaversResultsRequest result1 = LeaversResultsRequest.builder().personalCode("1234").leftOn("2022-12-20")
                .objectId("objectId").leaver("true").perId("40291").build();

        List<LeaversResultsRequest> results = Lists.newArrayList(result1);
        when(jdbcTemplate.batchUpdate(any(), any(), anyInt(), any()))
                .thenThrow(new DuplicateKeyException("Duplicate Key"));

        assertThrows(ElinksException.class, () -> elinksPeopleLeaverService.processLeavers(results));

        verify(jdbcTemplate, times(1)).batchUpdate(any(), any(), anyInt(), any());
    }

    // this test requires getTripleParameterizedPreparedStatementSetter() to be made default and not private
    // and works when run in intellij but fails when run from the commandline
    //@Test
    //void testGetTripleParameterizedPreparedStatementSetter() throws SQLException {
    //    ParameterizedPreparedStatementSetter<Triple<String, String, String>> setter =
    //            elinksPeopleLeaverService.getTripleParameterizedPreparedStatementSetter();
    //    PreparedStatement ps = mock(PreparedStatement.class);
    //    Triple<String, String, String> argument = Triple.of("personalCode", "true", "2022-12-20");

    //    setter.setValues(ps, argument);

    //    verify(ps, times(1)).setString(1, "2022-12-20");
    //    verify(ps, times(1)).setBoolean(2, false);
    //    verify(ps, times(1)).setString(3, "personalCode");
    //}
}