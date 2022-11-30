package uk.gov.hmcts.reform.judicialapi.configuration;

import uk.gov.hmcts.reform.judicialapi.feign.IdamClient;

import java.io.UnsupportedEncodingException;
import java.util.Set;

public interface JrdSidamTokenService {

    String getBearerToken();

    Set<IdamClient.User> getIdamElasticFeed() throws UnsupportedEncodingException;
}
