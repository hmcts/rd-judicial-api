package uk.gov.hmcts.reform.judicialapi.elinks.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;


@Entity(name = "BaseLocation")
@Table(name = "location_type", schema = "dbjudicialdata")
@Getter
@Setter
@NoArgsConstructor
public class BaseLocation {

    @Id
    @Column(name = "base_location_Id")
    @JsonProperty(value = "id")
    private String baseLocationId;

    @Column(name = "name")
    @Size(max = 256)
    @JsonProperty(value = "name")
    private String name;

    @Column(name = "type_id")
    @Size(max = 64)
    @JsonProperty(value = "typeId")
    private String typeId;

    @Column(name = "parent_id")
    @Size(max = 64)
    @JsonProperty(value = "parentId")
    private String parentId;

    @Column(name = "jurisdiction_id")
    @Size(max = 64)
    @JsonProperty(value = "jurisdictionId")
    private String jurisdictionId;

    @Column(name = "start_date")
    @JsonProperty(value = "startDate")
    private LocalDate startDate;

    @Column(name = "end_date")
    @JsonProperty(value = "endDate")
    private LocalDate endDate;

    @Column(name = "created_at")
    @JsonProperty(value = "createAt")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonProperty(value = "updatedAt")
    private LocalDateTime updatedAt;

}
