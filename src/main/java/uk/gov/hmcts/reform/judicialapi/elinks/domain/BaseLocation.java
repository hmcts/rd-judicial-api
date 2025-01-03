package uk.gov.hmcts.reform.judicialapi.elinks.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity(name = "BaseLocation")
@Table(name = "location_type", schema = "dbjudicialdata")
@Getter
@Setter
@NoArgsConstructor
public class BaseLocation {

    @Id
    @Column(name = "base_location_Id")
    @JsonProperty("id")
    private String baseLocationId;

    @Column(name = "name")
    @Size(max = 256)
    @JsonProperty
    private String name;

    @Column(name = "type_id")
    @Size(max = 64)
    @JsonProperty
    private String typeId;

    @Column(name = "parent_id")
    @Size(max = 64)
    @JsonProperty
    private String parentId;

    @Column(name = "jurisdiction_id")
    @Size(max = 64)
    @JsonProperty
    private String jurisdictionId;

    @Column(name = "start_date")
    @JsonProperty
    private LocalDate startDate;

    @Column(name = "end_date")
    @JsonProperty
    private LocalDate endDate;

    @Column(name = "created_at")
    @JsonProperty
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonProperty
    private LocalDateTime updatedAt;

}
