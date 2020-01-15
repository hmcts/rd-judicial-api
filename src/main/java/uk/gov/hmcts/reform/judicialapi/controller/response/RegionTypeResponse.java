package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.judicialapi.domain.RegionType;

public class RegionTypeResponse {

    @JsonProperty
    private String regionDescEn;

    public RegionTypeResponse (RegionType regionType){
        this.regionDescEn = regionType.getRegionDescEn();
    }

}
