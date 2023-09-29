package uk.gov.hmcts.reform.judicialapi.elinks.util;


import com.google.common.io.CharStreams;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinksResponses;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinksResponsesRepository;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;


@Component
@Slf4j
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ElinksResponsesHelper {

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @Autowired
    private ElinksResponsesRepository elinksResponsesRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Response saveElinksResponse(String apiName, Response elinksData) {
        try {
            byte[] jsonBody = CharStreams.toString(elinksData.body().asReader(Charset.defaultCharset())).getBytes();
            ElinksResponses elinksResponses = ElinksResponses.builder().apiName(apiName)
                    .createdDate(LocalDateTime.now()).elinksData(new String(jsonBody)
                            .replaceAll("[\n\r]","")).build();
            elinksResponsesRepository.save(elinksResponses);

            return elinksData.toBuilder().body(jsonBody).build();
        } catch (IOException e) {
            log.error("Saving base request failed " + apiName);
        }
        return elinksData;
    }
}

