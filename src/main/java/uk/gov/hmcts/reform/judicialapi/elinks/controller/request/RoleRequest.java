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
public class RoleRequest {

    @JsonProperty("judiciary_role_id")
    private String judiciaryRoleId;
    @JsonProperty("judiciary_role_name_id")
    private String judiciaryRoleNameId;
    //TBC
    @JsonProperty("name")
    private String name;
    //TBC
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
}
