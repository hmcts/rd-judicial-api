package uk.gov.hmcts.reform.judicialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;

import java.util.UUID;

public interface AuthorisationRepository extends JpaRepository<Authorisation, UUID> {
}
