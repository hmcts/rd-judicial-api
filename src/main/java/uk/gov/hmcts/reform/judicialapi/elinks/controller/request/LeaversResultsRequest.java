package uk.gov.hmcts.reform.judicialapi.elinks.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeaversResultsRequest {

    @JsonProperty("personal_code")
    private String personalCode;
    @JsonProperty("id")
    private String objectId;
    @JsonProperty("per_id")
    private String perId;
    @JsonProperty("leaver")
    private String leaver;
    @JsonProperty("left_on")
    private String leftOn;
}