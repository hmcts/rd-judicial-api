package uk.gov.hmcts.reform.judicialapi.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseLocationTypeRefreshResponse implements Serializable {

    private String baseLocationId;

    private String courtName;

    private String courtType;

    private String circuit;

    private String areaOfExpertise;

}
