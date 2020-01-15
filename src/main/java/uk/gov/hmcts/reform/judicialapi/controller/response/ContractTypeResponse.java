package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.judicialapi.domain.ContractType;

public class ContractTypeResponse {

    @JsonProperty
    private String contractTypeDescEn;

    public ContractTypeResponse (ContractType contractType) {
        this.contractTypeDescEn = contractType.getContractTypeDescEn();
    }

}
