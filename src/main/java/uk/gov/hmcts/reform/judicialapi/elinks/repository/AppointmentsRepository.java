package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;

import java.util.List;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointment, Long> {

    void deleteByPersonalCode(String personalCode);

    void deleteByAppointmentId(String appointmentId);

    @Query(value = "SELECT DISTINCT base_location_Id  FROM "
            + "dbjudicialdata.judicial_office_appointment  WHERE DATE(last_loaded_date) = current_date",
            nativeQuery = true)
    List<String> fetchAppointmentBaseLocation();


    List<Appointment> deleteByPersonalCodeIn(List<String> personalCode);

    List<Appointment> findByPersonalCodeIn(List<String> personalCode);


}
