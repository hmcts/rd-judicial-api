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
@Table(name = "region_type", schema = "dbjudicialdata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Location implements Serializable {

    @Id
    @Column(name = "region_Id")
    @JsonProperty(value = "id")
    private String regionId;

    @Column(name = "region_desc_en")
    @Size(max = 256)
    @JsonProperty(value = "name")
    private String regionDescEn;

    @Column(name = "region_desc_cy")
    @Size(max = 256)
    private String regionDescCy;
}
