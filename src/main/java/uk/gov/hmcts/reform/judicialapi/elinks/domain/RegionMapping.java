package uk.gov.hmcts.reform.judicialapi.elinks.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "regionMapping")
@Table(name = "jrd_lrd_region_mapping", schema = "dbjudicialdata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionMapping {

    @Id
    @Column(name = "jrd_region_id")
    private String jrdRegionId;

    @Column(name = "jrd_region")
    private String jrdRegion;

    @Column(name = "region_id")
    private String regionId;

    @Column(name = "region")
    private String region;

}

