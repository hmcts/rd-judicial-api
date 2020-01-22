package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.judicialapi.domain.BaseLocationType;


public class BaseLocationTypeResponse {

    @JsonProperty
    private String courtName;

    @JsonProperty
    private String bench;

    @JsonProperty
    private String courtType;

    @JsonProperty
    private String circuit;

    @JsonProperty
    private String areaOfExpertise;

    @JsonProperty
    private String nationalCourtCode;

    public BaseLocationTypeResponse(BaseLocationType baseLocationType) {
        this.courtName = baseLocationType.getCourtName();
        this.bench = baseLocationType.getBench();
        this.courtType = baseLocationType.getCourtType();
        this.circuit = baseLocationType.getCourtType();
        this.areaOfExpertise = baseLocationType.getAreaOfExpertise();
        this.nationalCourtCode = baseLocationType.getNationalCourtCode();
    }
}
