package uk.gov.hmcts.reform.judicialapi.elinks.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("all")
@Repository

public interface JudicialRoleTypeRepository extends JpaRepository<uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType, String> {

    void deleteByPersonalCode(String personalCode);

    @Modifying
    @Query(value = "DELETE FROM judicial_additional_roles role WHERE role.personalCode IN :personalCode")
    void deleteRoleTypeRepository(List<String> personalCode);

}
