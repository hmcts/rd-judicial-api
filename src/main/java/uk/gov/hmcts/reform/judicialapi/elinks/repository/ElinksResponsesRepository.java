package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinksResponses;

import java.time.LocalDateTime;

@Repository
public interface ElinksResponsesRepository extends JpaRepository<ElinksResponses, Long> {

    void deleteByCreatedDateBefore(LocalDateTime createdDate);

}
