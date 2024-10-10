package uk.gov.hmcts.reform.judicialapi.elinks.service;

import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.LeaversResultsRequest;

import java.util.List;

public interface ElinksPeopleLeaverService {

    void processLeavers(List<LeaversResultsRequest> leaversResultsRequests);
}
