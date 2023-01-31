package uk.gov.hmcts.reform.judicialapi.elinks.service;

import uk.gov.hmcts.reform.judicialapi.elinks.exception.JudicialDataLoadException;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;

public interface PublishSidamIdService {

    SchedulerJobStatusResponse publishSidamIdToAsb()throws JudicialDataLoadException;

}
