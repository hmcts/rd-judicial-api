package uk.gov.hmcts.reform.judicialapi.controller.feign;

import feign.Headers;
import feign.RequestLine;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.hmcts.reform.judicialapi.configuration.FeignInterceptorConfiguration;
import uk.gov.hmcts.reform.judicialapi.controller.request.TestUserRequest;


@FeignClient(name = "IdamUserFeignClient", url = "${testing.support.idamUrl}",
        configuration = FeignInterceptorConfiguration.class)
public interface IdamUserFeignClient {

    @PostMapping(value = "/testing-support/accounts")
    @RequestLine("POST /testing-support/accounts")
    @Headers({"Content-Type: application/json"})
    Response createUserProfile(@RequestBody TestUserRequest request);

}

