package uk.gov.hmcts.reform.judicialapi.util;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.cfg.ConstructorDetector;
import tools.jackson.databind.json.JsonMapper;
import feign.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.hmcts.reform.judicialapi.controller.advice.UserProfileException;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.JudicialDataLoadException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.ERROR_IN_PARSING_THE_FEIGN_RESPONSE;

@SuppressWarnings("unchecked")
public class JsonFeignResponseUtil {

    private static final JsonMapper json = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .constructorDetector(ConstructorDetector.DEFAULT.withAllowImplicitWithDefaultConstructor(false))
            .build();

    private JsonFeignResponseUtil() {

    }

    public static ResponseEntity<Object> toResponseEntityWithListBody(Response response, Object clazz) {
        List<Object> payload = mapObjectToList(response, clazz);

        return ResponseEntity.status(response.status())
                .headers(headers -> convertHeaders(response.headers())
                        .forEach(headers::addAll))
                .body(payload);
    }

    public static List<Object> mapObjectToList(Response response, Object clazz) {
        try {
            JavaType type = json.getTypeFactory().constructCollectionType(List.class, (Class<?>) clazz);
            return json.readValue(response.body().asReader(Charset.defaultCharset()), type);
        } catch (Exception e) {
            throw new UserProfileException(INTERNAL_SERVER_ERROR,
                    String.format(ERROR_IN_PARSING_THE_FEIGN_RESPONSE, ((Class<?>) clazz).getSimpleName()),
                    e.getLocalizedMessage());
        }
    }

    public static MultiValueMap<String, String> convertHeaders(Map<String, Collection<String>> responseHeaders) {
        MultiValueMap<String, String> responseEntityHeaders = new LinkedMultiValueMap<>();
        responseHeaders.entrySet().stream().forEach(e -> {
            if (!(e.getKey().equalsIgnoreCase("request-context") || e.getKey()
                    .equalsIgnoreCase("x-powered-by") || e.getKey()
                    .equalsIgnoreCase("content-length"))) {
                responseEntityHeaders.put(e.getKey(), new ArrayList<>(e.getValue()));
            }
        });

        return responseEntityHeaders;
    }

    public static ResponseEntity<Object> toResponseEntity(Response response, Object clazz) {
        Optional<Object> payload = decode(response, clazz);

        return ResponseEntity.status(response.status())
                .headers(headers -> convertHeaders(response.headers())
                        .forEach(headers::addAll))
                .body(payload.orElse("unknown"));
    }

    public static ResponseEntity<Object> toResponseEntity(Response response, TypeReference<?> reference) {
        Optional<Object> payload;

        try {
            payload = Optional.of(json.readValue(response.body().asReader(Charset.defaultCharset()), reference));

        } catch (IOException ex) {
            throw new JudicialDataLoadException("Response parsing failed");
        }

        return ResponseEntity.status(response.status())
                .headers(headers -> convertHeaders(response.headers())
                        .forEach(headers::addAll))
                .body(payload.orElse(null));
    }

    public static Optional<Object> decode(Response response, Object clazz) {
        try {
            return Optional.of(json.readValue(response.body().asReader(Charset.defaultCharset()),
                    (Class<Object>) clazz));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
