package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialRoleType;

@Getter
@AllArgsConstructor
public class JudicialRoleTypeResponse {

    @JsonProperty
    private String roleId;
    @JsonProperty
    private String roleDescEn;
    @JsonProperty
    private String roleDescCy;

    public JudicialRoleTypeResponse(JudicialRoleType judicialRoleType) {
        this.roleId = judicialRoleType.getRoleId();
        this.roleDescEn = judicialRoleType.getRoleDescEn();
        this.roleDescCy = judicialRoleType.getRoleDescCy();
    }

    @Override
    public String toString() {
        return "{roleId=" + this.getRoleId() + ", roleDescEn=" + this.getRoleDescEn() + ", roleDescCy=" + this.getRoleDescCy() + "}";
    }

}
