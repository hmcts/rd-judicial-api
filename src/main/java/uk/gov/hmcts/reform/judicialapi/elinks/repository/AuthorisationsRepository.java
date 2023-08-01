package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;

@Repository
public interface AuthorisationsRepository extends JpaRepository<Authorisation, Long> {

    void deleteByAppointmentId(String appointmentId);

    void deleteByPersonalCode(String personalCode);
}
