package uk.gov.hmcts.reform.judicialapi.service;

import org.springframework.http.ResponseEntity;

import java.util.List;


public interface JudicialUserService {

    ResponseEntity<Object> fetchJudicialUsers(Integer size, Integer page, List<String> sidamIds);
}
