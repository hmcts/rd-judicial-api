package uk.gov.hmcts.reform.judicialapi.client.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import lombok.Builder;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseLocationType implements Serializable {

    private String baseLocationId;

    private String courtName;

    private String courtType;

    private String circuit;

    private String areaOfExpertise;

}
