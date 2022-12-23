package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;

import java.util.List;

@Repository
public interface AppointementsRepository extends JpaRepository<Appointment, Long> {
    void deleteByPersonalCodeIn(List<String> personalCode);


}
