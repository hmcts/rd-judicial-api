package uk.gov.hmcts.reform.judicialapi.elinks.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.hmcts.reform.idam.client.models.TokenRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamOpenIdTokenResponse;

import java.util.List;


@FeignClient(name = "IdamOpenIdFeignClient", url = "${idam.oidc.url}")
public interface IdamOpenIdFeignClient {

    @PostMapping(value = "/o/token", consumes = {"application/x-www-form-urlencoded"})
    IdamOpenIdTokenResponse getOpenIdToken(@RequestBody TokenRequest tokenRequest);

}
