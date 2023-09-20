package uk.gov.hmcts.reform.judicialapi.elinks.service;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface IdamElasticSearchService {

    String getIdamBearerToken(LocalDateTime schedulerStartTime);

    ResponseEntity<Object> getIdamElasticSearchSyncFeed();
}
