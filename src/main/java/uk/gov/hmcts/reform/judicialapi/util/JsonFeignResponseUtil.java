package uk.gov.hmcts.reform.judicialapi.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import org.springframework.http.HttpStatus;
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

    private static final ObjectMapper json = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonFeignResponseUtil() {

    }

    public static ResponseEntity<Object> toResponseEntityWithListBody(Response response, Object clazz) {
        List<Object> payload = mapObjectToList(response, clazz);

        return new ResponseEntity<>(
                payload,
                convertHeaders(response.headers()),
                HttpStatus.valueOf(response.status()));
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

        return new ResponseEntity<>(
                payload.orElse("unknown"),
                convertHeaders(response.headers()),
                HttpStatus.valueOf(response.status()));
    }

    public static ResponseEntity<Object> toResponseEntity(Response response, TypeReference<?> reference) {
        Optional<Object> payload;

        try {
            payload = Optional.of(json.readValue(response.body().asReader(Charset.defaultCharset()), reference));

        } catch (IOException ex) {
            throw new JudicialDataLoadException("Response parsing failed");
        }

        return new ResponseEntity<>(
                payload.orElse(null),
                convertHeaders(response.headers()),
                HttpStatus.valueOf(response.status()));
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
