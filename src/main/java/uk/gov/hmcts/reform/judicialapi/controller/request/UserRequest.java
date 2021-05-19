package uk.gov.hmcts.reform.judicialapi.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserRequest implements Serializable {
    //Commenting out the Json Property in order to bring it inline with Staff Ref Data
    // @JsonProperty("user_ids")
    private List<String> userIds;
}