package uk.gov.hmcts.reform.judicialapi.elinks.service;

import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.JudicialDataLoadException;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;

public interface PublishSidamIdService {

    ResponseEntity<SchedulerJobStatusResponse> publishSidamIdToAsb()throws JudicialDataLoadException;

}
