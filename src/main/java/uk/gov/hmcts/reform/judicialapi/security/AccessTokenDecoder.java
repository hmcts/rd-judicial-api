package uk.gov.hmcts.reform.judicialapi.security;

import java.util.Map;

public interface AccessTokenDecoder {

    Map<String, String> decode(
            String accessToken
    );
}
