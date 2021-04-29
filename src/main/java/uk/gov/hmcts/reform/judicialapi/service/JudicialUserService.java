package uk.gov.hmcts.reform.judicialapi.service;

import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;

import java.util.List;

public interface JudicialUserService {

    List<OrmResponse> fetchJudicialUsers(Integer size, Integer page);
}
