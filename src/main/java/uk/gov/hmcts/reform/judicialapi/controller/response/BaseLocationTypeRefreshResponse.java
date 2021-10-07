package uk.gov.hmcts.reform.judicialapi.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
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
