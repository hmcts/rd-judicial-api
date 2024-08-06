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


@FeignClient(name = "IdamFeignClient", url = "${idam.api.url}")
public interface IdamFeignClient {

    @GetMapping(value = "/api/v1/users")
    List<IdamResponse> searchUsers(@RequestHeader("Authorization") String authorization,
                                   @RequestParam("query") final String elasticSearchQuery,
                                   @RequestParam("size") final String size,
                                   @RequestParam("page") final String page);

    @PostMapping(value = "/o/token", consumes = {"application/x-www-form-urlencoded"})
    IdamOpenIdTokenResponse getOpenIdToken(@RequestBody TokenRequest tokenRequest);

}
