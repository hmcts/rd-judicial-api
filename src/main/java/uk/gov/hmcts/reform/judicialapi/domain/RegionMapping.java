package uk.gov.hmcts.reform.judicialapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "jrd_lrd_region_mapping")
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
