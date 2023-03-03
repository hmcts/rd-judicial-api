package uk.gov.hmcts.reform.judicialapi.elinks.service;

import org.springframework.http.ResponseEntity;

public interface IdamElasticSearchService {

    String getIdamBearerToken();

    ResponseEntity<Object> getIdamElasticSearchSyncFeed();
}
