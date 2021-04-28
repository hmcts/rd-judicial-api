package uk.gov.hmcts.reform.judicialapi.service;

import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;

public interface JudicialUserService {

    OrmResponse fetchJudicialUsers(Integer size, Integer page);
}
