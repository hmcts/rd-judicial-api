package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import java.util.List;
import java.util.stream.Collectors;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialRoleType;

public class JudicialRoleTypeEntityResponse {

    private List<JudicialRoleTypeResponse> judicialRoleTypeResponses;

    public JudicialRoleTypeEntityResponse(List<JudicialRoleType> judicialRoleTypes) {
        this.judicialRoleTypeResponses = judicialRoleTypes.stream()
                .map(judicialRoleType -> new JudicialRoleTypeResponse(judicialRoleType))
                .collect(Collectors.toList());
    }

    @JsonGetter("judicialRoles")
    public List<JudicialRoleTypeResponse> getJudicialRoleTypes() {
        return judicialRoleTypeResponses;
    }
}
