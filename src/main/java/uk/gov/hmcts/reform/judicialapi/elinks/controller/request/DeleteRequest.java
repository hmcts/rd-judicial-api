package uk.gov.hmcts.reform.judicialapi.elinks.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.response.DeletedResponse;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteRequest {

    @JsonProperty("pagination")
    private PaginationRequest pagination;
    @JsonProperty("results")
    private List<DeletedResponse> deletedResponse;

}