package uk.gov.hmcts.reform.judicialapi.elinks.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.reform.idam.client.models.TokenRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamOpenIdTokenResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;

import java.util.List;


@FeignClient(name = "IdamOpenIdFeignClient", url = "${idam.oidc.url}")
public interface IdamOpenIdFeignClient {

    @PostMapping(value = "/o/token", consumes = {"application/x-www-form-urlencoded"})
    IdamOpenIdTokenResponse getOpenIdToken(@RequestBody TokenRequest tokenRequest);

}
