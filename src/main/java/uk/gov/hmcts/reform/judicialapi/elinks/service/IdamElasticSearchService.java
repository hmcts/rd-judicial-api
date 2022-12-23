package uk.gov.hmcts.reform.judicialapi.elinks.service;

import uk.gov.hmcts.reform.judicialapi.elinks.exception.JudicialDataLoadException;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;

import java.util.Set;

public interface IdamElasticSearchService {

    String getIdamBearerToken() throws JudicialDataLoadException;

    Set<IdamResponse> getIdamElasticSearchSyncFeed() throws JudicialDataLoadException;
}
