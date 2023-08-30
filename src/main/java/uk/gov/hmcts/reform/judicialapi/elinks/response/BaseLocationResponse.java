package uk.gov.hmcts.reform.judicialapi.elinks.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BaseLocationResponse implements Serializable {


    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "type_id")
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

    public static BaseLocation toBaseLocationEntity(BaseLocationResponse baseLocationResponse) {
        BaseLocation baseLocation = new BaseLocation();
        baseLocation.setBaseLocationId(baseLocationResponse.getId());
        baseLocation.setName(baseLocationResponse.getName());
        baseLocation.setTypeId(baseLocationResponse.getTypeId());
        baseLocation.setParentId(baseLocationResponse.getParentId());
        baseLocation.setJurisdictionId(baseLocationResponse.getJurisdictionId());
        baseLocation.setStartDate(convertToLocalDate(baseLocationResponse.getStartDate()));
        baseLocation.setEndDate(convertToLocalDate(baseLocationResponse.getEndDate()));
        baseLocation.setCreatedAt(convertToLocalDateTime(baseLocationResponse.getCreatedAt()));
        baseLocation.setUpdatedAt(convertToLocalDateTime(baseLocationResponse.getUpdatedAt()));
        return baseLocation;
    }

    private static LocalDate convertToLocalDate(String date) {
        if (Optional.ofNullable(date).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
        }
        return null;
    }

    private static LocalDateTime convertToLocalDateTime(String date) {
        if (Optional.ofNullable(date).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            return LocalDateTime.parse(date, formatter);
        }
        return null;
    }

}
