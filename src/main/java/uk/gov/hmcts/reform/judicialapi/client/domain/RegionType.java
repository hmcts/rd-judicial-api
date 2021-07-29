package uk.gov.hmcts.reform.judicialapi.client.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class RegionType implements Serializable {

    private String regionId;

    private String regionDescEn;

    private String regionDescCy;


}
