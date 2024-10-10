package uk.gov.hmcts.reform.judicialapi.elinks.service;

import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.ResultsRequest;

import java.util.List;

public interface ElinksPeopleDeleteService {

    void deleteAuth(ResultsRequest resultsRequest);

    void deletePeople(String personalCode);

    void deletePeople(List<String> personalCodes);

    void clearDeletedPeople(List<String> personalCodes);

}
