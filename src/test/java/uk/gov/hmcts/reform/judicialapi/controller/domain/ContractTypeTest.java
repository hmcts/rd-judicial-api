package uk.gov.hmcts.reform.judicialapi.controller.domain;

import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.domain.ContractType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createContractType;

public class ContractTypeTest {

    @Test
    public void testContractType() {

        ContractType contractType = createContractType();

        assertNotNull(contractType);
        assertNotNull(contractType.getAppointments());
        assertThat(contractType.getContractTypeId()).isEqualTo("0");
        assertThat(contractType.getContractTypeDescEn()).isEqualTo("default");
        assertThat(contractType.getContractTypeDescCy()).isEqualTo("default");
    }
}
