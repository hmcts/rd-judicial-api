package uk.gov.hmcts.reform.judicialapi.elinks.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;

import java.io.Serializable;

import static uk.gov.hmcts.reform.judicialapi.elinks.util.DateUtil.convertToLocalDate;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.DateUtil.convertToLocalDateTime;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.CREATED_AT;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.END_DATE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.START_DATE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.UPDATED_AT;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BaseLocationResponse implements Serializable {


    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type_id")
    private String typeId;

    @JsonProperty("parent_id")
    private String parentId;
    @JsonProperty("jurisdiction_id")
    private String jurisdictionId;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static BaseLocation toBaseLocationEntity(BaseLocationResponse baseLocationResponse) {
        BaseLocation baseLocation = new BaseLocation();
        baseLocation.setBaseLocationId(baseLocationResponse.getId());
        baseLocation.setName(baseLocationResponse.getName());
        baseLocation.setTypeId(baseLocationResponse.getTypeId());
        baseLocation.setParentId(baseLocationResponse.getParentId());
        baseLocation.setJurisdictionId(baseLocationResponse.getJurisdictionId());
        baseLocation.setStartDate(convertToLocalDate(START_DATE, baseLocationResponse.getStartDate()));
        baseLocation.setEndDate(convertToLocalDate(END_DATE, baseLocationResponse.getEndDate()));
        baseLocation.setCreatedAt(convertToLocalDateTime(CREATED_AT,
                DATE_TIME_FORMAT,
                baseLocationResponse.getCreatedAt()));
        baseLocation.setUpdatedAt(convertToLocalDateTime(UPDATED_AT,
                DATE_TIME_FORMAT,
                baseLocationResponse.getUpdatedAt()));
        return baseLocation;
    }
}
