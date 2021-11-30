package uk.gov.hmcts.reform.judicialapi.controller.util;

import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import uk.gov.hmcts.reform.judicialapi.controller.advice.UserProfileException;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserProfileCreationResponse;
import uk.gov.hmcts.reform.judicialapi.util.JsonFeignResponseUtil;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class JsonFeignResponseUtilTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testDecode() {
        var header = new HashMap<String, Collection<String>>();
        var list = new ArrayList<String>();
        header.put("content-encoding", list);

        var response = Response.builder().status(200).reason("OK").headers(header)
                .body("{\"userIdentifier\": 1}", UTF_8).request(mock(Request.class)).build();
        var createUserProfileResponseOptional = JsonFeignResponseUtil.decode(response,
                UserProfileCreationResponse.class);

        assertThat(createUserProfileResponseOptional).isNotEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_Decode_fails_with_ioException() {
        var header = new HashMap<String, Collection<String>>();
        var list = new ArrayList<String>();
        header.put("content-encoding", list);

        Response.Body bodyMock = mock(Response.Body.class);
        var response = Response.builder().status(200).reason("OK").headers(header).body(bodyMock)
                .request(mock(Request.class)).build();

        try {
            when(bodyMock.asInputStream()).thenThrow(new IOException());
            when(bodyMock.asReader(Charset.defaultCharset())).thenThrow(new IOException());
        } catch (IOException e) {
            e.printStackTrace();
        }

        var createUserProfileResponseOptional = JsonFeignResponseUtil.decode(response,
                UserProfileCreationResponse.class);
        assertThat(createUserProfileResponseOptional).isEmpty();
    }

    @Test
    public void test_convertHeaders() {
        var header = new HashMap<String, Collection<String>>();
        var list = new ArrayList<>(Arrays.asList("gzip", "request-context", "x-powered-by",
                "content-length"));
        header.put("content-encoding", list);

        MultiValueMap<String, String> responseHeader = JsonFeignResponseUtil.convertHeaders(header);
        assertThat(responseHeader).isNotEmpty();

        var emptylist = new ArrayList<String>();
        header.put("content-encoding", emptylist);
        MultiValueMap<String, String> responseHeader1 = JsonFeignResponseUtil.convertHeaders(header);

        assertThat(responseHeader1.get("content-encoding")).isEmpty();
    }

    @Test
    public void test_toResponseEntity_with_payload_not_empty() {
        var header = new HashMap<String, Collection<String>>();
        var list = new ArrayList<>(Arrays.asList("a", "b"));
        header.put("content-encoding", list);

        var response = Response.builder().status(200).reason("OK").headers(header)
                .body("{\"idamId\": 1}", UTF_8).request(mock(Request.class)).build();
        var entity = JsonFeignResponseUtil.toResponseEntity(response, UserProfileCreationResponse.class);
        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode().value()).isEqualTo(200);
        assertThat(entity.getHeaders()).isNotEmpty();
        assertThat(((UserProfileCreationResponse) entity.getBody()).getIdamId()).isEqualTo("1");
    }

    @Test
    public void test_privateConstructor() throws Exception {
        Constructor<JsonFeignResponseUtil> constructor = JsonFeignResponseUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance((Object[]) null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_mapObjectToList() {
        var header = new HashMap<String, Collection<String>>();
        var list = new ArrayList<String>();
        header.put("content-encoding", list);
        String responseBody = "[\n"
                + "  {\n"
                + "    \"jurisdiction\": \"Civil\",\n"
                + "    \"service_id\": 6,\n"
                + "    \"org_unit\": \"HMCTS\",\n"
                + "    \"business_area\": \"Civil, Family and Tribunals\",\n"
                + "    \"sub_business_area\": \"Civil and Family\",\n"
                + "    \"service_description\": \"Specified Money Claims\",\n"
                + "    \"service_code\": \"AAA6\",\n"
                + "    \"service_short_description\": \"Specified Money Claims\",\n"
                + "    \"ccd_service_name\": \"CMC\",\n"
                + "    \"last_update\": \"2020-11-02T16:28:37.259752\",\n"
                + "    \"ccd_case_types\": [\n"
                + "      \"MoneyClaimCase\",\n"
                + "      \"CMC_ExceptionRecord\"\n"
                + "    ]\n"
                + "  }\n"
                + "]";

        var response = Response.builder().status(200).reason("OK").headers(header)
                .body(responseBody, UTF_8).request(mock(Request.class)).build();
        var responseEntity =
                JsonFeignResponseUtil.toResponseEntityWithListBody(
                        response,
                        LrdOrgInfoServiceResponse.class);
        var listLrdServiceMapping =
                (List<LrdOrgInfoServiceResponse>)responseEntity.getBody();
        assertFalse(listLrdServiceMapping.isEmpty());
    }

    @Test
    public void test_mapObjectToEmptyList() {
        var header = new HashMap<String, Collection<String>>();
        var list = new ArrayList<String>();
        header.put("content-encoding", list);
        String responseBody = "";
        Assertions.assertThrows(UserProfileException.class, () -> {
            var response = Response.builder().status(200).reason("OK").headers(header)
                    .body(responseBody, UTF_8).request(mock(Request.class)).build();
            JsonFeignResponseUtil.toResponseEntityWithListBody(
                    response,
                    LrdOrgInfoServiceResponse.class);
        });

    }
}