package uk.gov.hmcts.reform.judicialapi.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static uk.gov.hmcts.reform.judicialapi.util.SpringBootIntegrationTest.getObjectMapper;

public class KeyGenUtil {

    private static final String KEY_ID = "23456789";
    private static RSAKey rsaJwk;

    private KeyGenUtil() {
    }

    public static RSAKey getRsaJwk() throws JOSEException {
        if (isNull(rsaJwk)) {
            rsaJwk = new RSAKeyGenerator(2048)
                    .keyID(KEY_ID)
                    .generate();
        }
        return rsaJwk;
    }

    public static String getDynamicJwksResponse() {
        try {
            Map<String, Object> jwks = Map.of(
                    "keys", List.of(getRsaJwk().toPublicJWK().toJSONObject())
            );
            return getObjectMapper().writeValueAsString(jwks);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
