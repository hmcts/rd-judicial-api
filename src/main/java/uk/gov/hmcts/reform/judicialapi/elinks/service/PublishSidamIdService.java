package uk.gov.hmcts.reform.judicialapi.elinks.service;

import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.JudicialDataLoadException;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;

import java.util.List;

public interface PublishSidamIdService {

    ResponseEntity<SchedulerJobStatusResponse> publishSidamIdToAsb() throws JudicialDataLoadException;

    ResponseEntity<SchedulerJobStatusResponse> publishSidamIdToAsb(List<String> sidamIds) throws JudicialDataLoadException;

}
