package uk.gov.hmcts.reform.judicialapi.elinks.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinksResponsesRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.BaseLocationResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION;

@ExtendWith(MockitoExtension.class)
class ElinksResponsesHelperTest {

    @Spy
    private ElinksResponsesRepository elinksResponsesRepository;

    @InjectMocks
    ElinksResponsesHelper elinksResponsesHelper;


    @Test
    void saveElinksResponsesSuccess() throws JsonProcessingException {

        List<BaseLocationResponse> baseLocations = getBaseLocationResponseData();
        ElinkBaseLocationResponse elinkBaseLocationResponse = new ElinkBaseLocationResponse();
        elinkBaseLocationResponse.setResults(baseLocations);

        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(elinkBaseLocationResponse);

        elinksResponsesHelper.saveElinksResponse(LOCATION, Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(HttpStatus.OK.value()).build());

        verify(elinksResponsesRepository, times(1))
                .save(any());

    }

    @Test
    void saveElinksResponses_thenExceptionIsThrown() throws Exception {
        InputStream inputStream = mock(InputStream.class);
        when(inputStream.readAllBytes()).thenThrow(new IOException());
        elinksResponsesHelper.saveElinksResponse("word",
                Response.builder().request(mock(Request.class)).body(inputStream, 1).build());

        verify(elinksResponsesRepository, times(0)).save(any());
    }

    private List<BaseLocationResponse> getBaseLocationResponseData() {
        BaseLocationResponse baseLocationOne = new BaseLocationResponse();
        baseLocationOne.setId("1");
        baseLocationOne.setName("Aberconwy");
        baseLocationOne.setTypeId("46");
        baseLocationOne.setParentId("1722");
        baseLocationOne.setTypeId("28");
        baseLocationOne.setCreatedAt("2023-04-12T16:42:35Z");
        baseLocationOne.setUpdatedAt("2023-04-12T16:42:35Z");

        BaseLocationResponse baseLocationTwo = new BaseLocationResponse();

        baseLocationTwo.setId("2");
        baseLocationTwo.setName("Aldridge and Brownhills");
        baseLocationTwo.setTypeId("48");
        baseLocationTwo.setParentId("1723");
        baseLocationTwo.setTypeId("29");
        baseLocationTwo.setCreatedAt("2023-04-12T16:42:35Z");
        baseLocationTwo.setUpdatedAt("2023-04-12T16:42:35Z");

        return List.of(baseLocationOne,baseLocationTwo);

    }

}
