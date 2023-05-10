package uk.gov.hmcts.reform.judicialapi.elinks.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {

    @JsonProperty("results")
    private Integer results;
    @JsonProperty("pages")
    private Integer pages;
    @JsonProperty("current_page")
    private Integer currentPage;
    @JsonProperty("results_per_page")
    private Integer resultsPerPage;
    @JsonProperty("more_pages")
    private Boolean morePages;

}