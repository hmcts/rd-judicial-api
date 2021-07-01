package uk.gov.hmcts.reform.judicialapi.controller.domain;

import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.domain.RoleType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createRoleType;

public class RoleTypeTest {

    @Test
    public void testRoleType() {
        RoleType roleType = createRoleType();

        assertNotNull(roleType);
        assertNotNull(roleType.getAppointments());
        assertThat(roleType.getRoleId()).isEqualTo("0");
        assertThat(roleType.getRoleDescEn()).isEqualTo("default");
        assertThat(roleType.getRoleDescCy()).isEqualTo("default");
    }
}
