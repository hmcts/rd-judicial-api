package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.LeaversResultsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleLeaverService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DATA_UPDATE_ERROR;

@Slf4j
@Service
public class ElinksPeopleLeaverServiceImpl implements ElinksPeopleLeaverService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final String QUERY = "UPDATE dbjudicialdata.judicial_user_profile SET last_working_date = Date(?)"
            + " , active_flag = ?, last_loaded_date= NOW() , last_updated= NOW() AT TIME ZONE 'utc' "
            + "WHERE personal_code = ?";

    @Override
    public void processLeavers(List<LeaversResultsRequest> leaversResultsRequests) {
        try {
            if (!leaversResultsRequests.isEmpty()) {
                List<Triple<String, String, String>> leaversId = new ArrayList<>();

                leaversResultsRequests.stream().filter(request -> nonNull(request.getPersonalCode())).forEach(s ->
                        leaversId.add(Triple.of(s.getPersonalCode(), s.getLeaver(), s.getLeftOn())));
                log.info("Insert Query batch Response from Leavers" + leaversId.size());
                jdbcTemplate.batchUpdate(
                        QUERY,
                        leaversId,
                        10,
                        (ps, argument) -> {
                            ps.setString(1, argument.getRight());
                            ps.setBoolean(2, !(Boolean.valueOf(argument.getMiddle())));
                            ps.setString(3, argument.getLeft());
                        });
            }
        } catch (Exception ex) {
            throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, DATA_UPDATE_ERROR, DATA_UPDATE_ERROR);
        }
    }
}
