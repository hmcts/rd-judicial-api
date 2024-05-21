package uk.gov.hmcts.reform.judicialapi.elinks.feign;

import feign.Headers;
import feign.RequestLine;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping(value = "/people")
    @RequestLine("GET /people")
    @Headers({"Authorization: {authorization}",
        "Content-Type: application/json"})
    Response getPeopleDetails(@RequestParam("updated_since") String updatedSince,
                              @RequestParam("per_page") String perPage, @RequestParam("page") String page,
                              @RequestParam("include_previous_appointments") boolean includePreviousAppointments);

    @GetMapping(value = "/leavers")
    @RequestLine("GET /leavers")
    @Headers({"Authorization: {authorization}",
        "Content-Type: application/json"})

    Response getLeaversDetails(@RequestParam("left_since") String updatedSince,
                              @RequestParam("per_page") String perPage, @RequestParam("page") String page);


    @GetMapping(value = "/deleted")
    @RequestLine("GET /deleted")
    @Headers({"Authorization: {authorization}",
        "Content-Type: application/json"})

    Response getDeletedDetails(@RequestParam("deleted_since") String updatedSince,
                               @RequestParam("per_page") String perPage, @RequestParam("page") String page);


}
