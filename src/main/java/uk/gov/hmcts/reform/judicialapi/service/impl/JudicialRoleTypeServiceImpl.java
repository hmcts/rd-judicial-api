package uk.gov.hmcts.reform.judicialapi.service.impl;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialRoleTypeResponse;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.persistence.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.service.JudicialRoleTypeService;

@Service
@Slf4j
public class JudicialRoleTypeServiceImpl implements JudicialRoleTypeService {

    @Autowired
    JudicialRoleTypeRepository judicialRoleTypeRepository;

    public List<JudicialRoleTypeResponse> retrieveJudicialRoles() {
        List<JudicialRoleType> judicialRoleTypes = judicialRoleTypeRepository.findAll();

        List<JudicialRoleTypeResponse> judicialRoleTypeResponses = new ArrayList<>();

        if (judicialRoleTypes.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        for (JudicialRoleType judicialRoleType : judicialRoleTypes) {
            judicialRoleTypeResponses.add(JudicialRoleTypeResponse.builder().roleId(judicialRoleType.getRoleId())
                    .roleDescCy(judicialRoleType.getRoleDescCy())
                    .roleDescEn(judicialRoleType.getRoleDescEn()).build());
        }

        return judicialRoleTypeResponses;
    }

}
