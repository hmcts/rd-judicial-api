package uk.gov.hmcts.reform.judicialapi.elinks.service;

import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.ResultsRequest;

public interface ELinksPeopleDeleteService {

    void deleteAuth(ResultsRequest resultsRequest);

    void deletePeople(String personalCode);

}
