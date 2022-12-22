package uk.gov.hmcts.reform.judicialapi.elinks.feign;

import feign.Headers;
import feign.RequestLine;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.ElinksFeignInterceptorConfiguration;

@FeignClient(name = "ElinksFeignClient", url = "${elinksUrl}",
        configuration = ElinksFeignInterceptorConfiguration.class)
public interface ElinksFeignClient {

    @GetMapping(value = "/reference_data/base_location")
    @RequestLine("GET /reference_data/base_location")
    @Headers({"Authorization: {authorization}",
            "Content-Type: application/json"})
    Response getBaseLocationDetails();

    @GetMapping(value = "/reference_data/location")
    @RequestLine("GET /reference_data/location")
    @Headers({"Authorization: {authorization}",
            "Content-Type: application/json"})
    Response getLocationDetails();


}
