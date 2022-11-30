package uk.gov.hmcts.reform.judicialapi.configuration;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PublishingData {
    @JsonProperty
    private List<String> userIds;
}
