package uk.gov.hmcts.reform.judicialapi.elinks.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity(name = "hmctsRegionType")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionType {

    @Id
    @Column(name = "hmcts_region_id")
    @Size(max = 64)
    private String RegionId;

    @Column(name = "hmcts_region_desc_en")
    @Size(max = 256)
    private String RegionDescEn;

    @Column(name = "hmcts_region_desc_cy")
    @Size(max = 256)
    private String RegionDescCy;

}
