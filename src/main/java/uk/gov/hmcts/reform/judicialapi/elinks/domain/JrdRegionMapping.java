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



@Entity(name = "jrdlrdregionmapping")
@Table(name = "jrd_lrd_region_mapping", schema = "dbjudicialdata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JrdRegionMapping implements Serializable {

    @Id
    @Column(name = "jrd_region_id")
    @JsonProperty(value = "id")
    private String jrdRegionId;

    @Column(name = "jrd_region")
    @Size(max = 256)
    private String jrdRegion;

    @Column(name = "region_id")
    @Size(max = 16)
    private String regionId;

    @Column(name = "region")
    @Size(max = 16)
    private String region;
}
