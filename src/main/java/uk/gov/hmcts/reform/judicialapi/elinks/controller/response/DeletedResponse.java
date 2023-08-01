package uk.gov.hmcts.reform.judicialapi.elinks.controller.response;

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
public class DeletedResponse {

    @JsonProperty("personal_code")
    private String personalCode;
    @JsonProperty("deleted")
    private String deleted;
    @JsonProperty("deleted_on")
    private String deletedOn;

}