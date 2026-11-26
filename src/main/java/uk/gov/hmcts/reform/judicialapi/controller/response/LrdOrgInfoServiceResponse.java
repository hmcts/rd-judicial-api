package uk.gov.hmcts.reform.judicialapi.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@com.fasterxml.jackson.databind.annotation.JsonNaming(
    com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy.class)
@tools.jackson.databind.annotation.JsonNaming(
    tools.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LrdOrgInfoServiceResponse {

    private Long serviceId;

    private String orgUnit;

    private String businessArea;

    private String subBusinessArea;

    private String jurisdiction;

    private String serviceDescription;

    private String serviceCode;

    private String serviceShortDescription;

    private String ccdServiceName;

    private String lastUpdate;

    private List<String> ccdCaseTypes;
}
