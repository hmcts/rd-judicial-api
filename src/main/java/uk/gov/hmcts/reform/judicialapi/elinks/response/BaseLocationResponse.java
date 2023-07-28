package uk.gov.hmcts.reform.judicialapi.elinks.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BaseLocationResponse implements Serializable {

    private String id;
    private String name;

    @JsonProperty(value = "orgunit2name")
    private String typeId;

    @JsonProperty(value = "orgunit3name")
    private String parentId;

    @JsonProperty(value = "orgunit4name")
    private String jurisdictionId;

    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;

    public static BaseLocation toBaseLocationEntity(BaseLocationResponse baseLocationResponse) {
        BaseLocation baseLocation = new BaseLocation();
        baseLocation.setBaseLocationId(baseLocationResponse.getId());
        baseLocation.setCourtName(baseLocationResponse.getName());

        baseLocation.setTypeId(baseLocationResponse.getTypeId());
        baseLocation.setParentId(baseLocationResponse.getParentId());
        baseLocation.setJurisdictionId(baseLocationResponse.getJurisdictionId());
        return baseLocation;
    }



}
