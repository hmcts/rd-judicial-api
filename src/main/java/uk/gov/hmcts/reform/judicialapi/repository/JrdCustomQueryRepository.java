package uk.gov.hmcts.reform.judicialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.reform.judicialapi.domain.RegionMapping;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.util.List;

public interface JrdCustomQueryRepository extends JpaRepository<RegionMapping, String> {

    @Query(value = "select distinct per.sidam_id \n" +
            "from judicial_user_profile per\n" +
            "LEFT JOIN judicial_office_appointment appt\n" +
            "ON per.per_id = appt.per_id\n" +
            "left join judicial_office_authorisation auth \n" +
            "on per.per_id = auth.per_id\n" +
            "where (per.object_id <> '') IS NOT false\n" +
            "and ((DATE(appt.end_date) >= CURRENT_DATE \n" +
            "or DATE(appt.end_date) is null)\n" +
            " and (DATE(auth.end_date) >= CURRENT_DATE \n" +
            " or DATE(auth.end_date) is null))\n" +
            "and ( auth.ticket_code IN ('389','315','400','317'))\n" +
            "and per.sidam_id is not NULL",
            nativeQuery = true)
    List<String> findUsersToPublish();
}
