package uk.gov.hmcts.reform.judicialapi.controller.feign;

import feign.Headers;
import feign.RequestLine;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.reform.judicialapi.configuration.FeignInterceptorConfiguration;
import uk.gov.hmcts.reform.judicialapi.controller.request.TestUserRequest;


@FeignClient(name = "IdamUserFeignClient", url = "${testing.support.idamUrl}", configuration = FeignInterceptorConfiguration.class)
public interface IdamUserFeignClient {

    @PostMapping(value = "/testing-support/accounts")
    @RequestLine("POST /testing-support/accounts")
    @Headers({"Authorization: {authorization}", "ServiceAuthorization: {serviceAuthorization}",
            "Content-Type: application/json"})
    Response createUserProfile(@RequestBody TestUserRequest request);

}

