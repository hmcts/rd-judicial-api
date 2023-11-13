package uk.gov.hmcts.reform.judicialapi.elinks.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name = "Location")
@Table(name = "hmcts_region_type", schema = "dbjudicialdata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Location implements Serializable {

    @Id
    @Column(name = "hmcts_region_id")
    @JsonProperty(value = "id")
    private String regionId;

    @Column(name = "hmcts_region_desc_en")
    @Size(max = 256)
    private String regionDescEn;

    @Column(name = "hmcts_region_desc_cy")
    @Size(max = 256)
    private String regionDescCy;
}
