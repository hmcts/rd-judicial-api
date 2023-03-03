package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;

@Repository
public interface ElinkDataExceptionRepository extends JpaRepository<ElinkDataExceptionRecords, Long> {

}