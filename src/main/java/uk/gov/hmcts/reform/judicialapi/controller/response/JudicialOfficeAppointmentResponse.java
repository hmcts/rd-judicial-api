package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uk.gov.hmcts.reform.judicialapi.domain.BaseLocationType;
import uk.gov.hmcts.reform.judicialapi.domain.ContractType;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialOfficeAppointment;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.domain.RegionType;

import java.util.List;

@Getter
public class JudicialOfficeAppointmentResponse {

    @JsonProperty
    private String roleId;
    @JsonProperty
    private String roleDesc_En;
    @JsonProperty
    private String contractTypeId;
    @JsonProperty
    private String contractTypeDescEN;
    @JsonProperty
    private String baseLocationType;
    @JsonProperty
    private String courtName;
    @JsonProperty
    private String bench;
    @JsonProperty
    private String courtType;
    @JsonProperty
    private  String circuit;
    @JsonProperty
    private String areaOfExpertise;
    @JsonProperty
    private String nationalCourtCode;
    @JsonProperty
    private String regionId;
    @JsonProperty
    private String regionDescEN;
    @JsonProperty
    private String isPrincipleAppointment;
    @JsonProperty
    private String startDate;
    @JsonProperty
    private String endDate;
    @JsonProperty
    private String activeFlag;




    public JudicialOfficeAppointmentResponse (JudicialOfficeAppointment judicialOfficeAppointment, BaseLocationType baseLocationType, JudicialRoleType judicialRoleType, ContractType contractType,
                                               RegionType regionType) {
    this.roleId = judicialRoleType.getRoleId();
    this.roleDesc_En = judicialRoleType.getRoleDescEn();
    this.contractTypeId = contractType.getContractTypeDescCy();
    this.contractTypeDescEN = contractType.getContractTypeDescEn();
    this.baseLocationType = baseLocationType.getBaseLocationId();
    this.courtName = baseLocationType.getCourtName();
    this.bench = baseLocationType.getBench();
    this.courtType = baseLocationType.getCourtType();
    this.circuit = baseLocationType.getCircuit();
    this.areaOfExpertise = baseLocationType.getAreaOfExpertise();
    this.nationalCourtCode = baseLocationType.getNationalCourtCode();
    this.regionId = regionType.getRegionId();
    this.regionDescEN = regionType.getRegionDescEn();
    this.isPrincipleAppointment = judicialOfficeAppointment.toString();
    this.startDate = judicialOfficeAppointment.toString();
    this.endDate = judicialOfficeAppointment.toString();
    this.activeFlag = judicialOfficeAppointment.toString();


}

}
