package uk.gov.hmcts.reform.judicialapi.controller.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uk.gov.hmcts.reform.judicialapi.domain.JudicialRoleType;


public class JudicialRoleTypeEntityResponseUnitTest {

    private JudicialRoleTypeEntityResponse sut;

    @Test
    public void testJudicialRoleTypeEntityResponse() {
        JudicialRoleType roleType = new JudicialRoleType("1", "testDescEn", "testDescCy");
        List<JudicialRoleType> roleTypes = new ArrayList<>();
        roleTypes.add(roleType);

        sut = new JudicialRoleTypeEntityResponse(roleTypes);

        JudicialRoleTypeResponse judicialRoleTypeResponse = new JudicialRoleTypeResponse(roleType);
        List<JudicialRoleTypeResponse> roleExpected = new ArrayList<>();
        roleExpected.add(judicialRoleTypeResponse);

        assertThat(sut.getJudicialRoleTypes().get(0).getRoleId()).isEqualTo(roleExpected.get(0).getRoleId());
        assertThat(sut.getJudicialRoleTypes().get(0).getRoleDescEn()).isEqualTo(roleExpected.get(0).getRoleDescEn());
        assertThat(sut.getJudicialRoleTypes().get(0).getRoleDescCy()).isEqualTo(roleExpected.get(0).getRoleDescCy());
    }
}
