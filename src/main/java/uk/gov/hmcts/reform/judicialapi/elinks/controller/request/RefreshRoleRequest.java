package uk.gov.hmcts.reform.judicialapi.elinks.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RefreshRoleRequest {

    @JsonProperty("ccdServiceName")
    private String ccdServiceNames;

    @JsonProperty("object_ids")
    private List<String> objectIds;

    @JsonProperty("sidam_ids")
    private List<String> sidamIds;

    @JsonProperty("personal_code")
    private List<String> personalCodes;

    @Override
    public String toString() {
        return "RefreshRoleRequest{"
                + "ccdServiceNames='" + ccdServiceNames + '\''
                + ", objectIds=" + objectIds
                + ", sidamIds=" + sidamIds
                + ", personalCodes=" + personalCodes
                + '}';
    }

}