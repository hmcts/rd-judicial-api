package uk.gov.hmcts.reform.judicialapi.elinks.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("all")
@Repository

public interface JudicialRoleTypeRepository extends JpaRepository<uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType, String> {

    void deleteByPersonalCode(String personalCode);

}
