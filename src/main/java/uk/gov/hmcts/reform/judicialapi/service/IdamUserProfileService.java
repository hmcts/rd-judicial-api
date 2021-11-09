package uk.gov.hmcts.reform.judicialapi.service;

import org.springframework.http.ResponseEntity;

public interface IdamUserProfileService {
    public ResponseEntity<Object> createIdamUserProfiles();
}
