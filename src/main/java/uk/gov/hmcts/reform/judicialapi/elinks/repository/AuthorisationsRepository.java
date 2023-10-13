package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;

import java.util.List;

@Repository
public interface AuthorisationsRepository extends JpaRepository<Authorisation, Long> {

    void deleteByAppointmentId(String appointmentId);

    void deleteByPersonalCode(String personalCode);

    @Modifying
    @Query(value = "DELETE FROM judicialOfficeAuthorisation auth WHERE auth.personalCode IN :personalCode")
    void deleteAuthorisationRepository(List<String> personalCode);
}
