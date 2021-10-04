package uk.gov.hmcts.reform.judicialapi.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class RegionTypeRefreshResponse implements Serializable {

    private String regionId;

    private String regionDescEn;

    private String regionDescCy;


}
