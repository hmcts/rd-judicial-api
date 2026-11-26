package uk.gov.hmcts.reform.judicialapi.elinks.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.reform.idam.client.models.TokenRequest;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamOpenIdTokenResponse;


@FeignClient(name = "IdamOpenIdFeignClient", url = "${idam.oidc.url}")
public interface IdamOpenIdFeignClient {

    @GetMapping("/o/userinfo")
    UserInfo retrieveUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation);

    @PostMapping(value = "/o/token", consumes = {"application/x-www-form-urlencoded"})
    IdamOpenIdTokenResponse getOpenIdToken(@RequestBody TokenRequest tokenRequest);

}
