package uk.gov.hmcts.reform.judicialapi.elinks.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;

import java.util.List;

@SuppressWarnings("all")
@Repository

public interface JudicialRoleTypeRepository extends JpaRepository<JudicialRoleType, String> {

    void deleteByPersonalCode(String personalCode);


    void deleteByPersonalCodeIn(List<String> personalCode);

    List<JudicialRoleType> findAllByPersonalCode(List<String> personalCode);
}
