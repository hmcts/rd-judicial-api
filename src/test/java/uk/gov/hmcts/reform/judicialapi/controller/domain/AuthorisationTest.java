package uk.gov.hmcts.reform.judicialapi.controller.domain;

import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createAuthorisation;

public class AuthorisationTest {

    @Test
    public void testAuthorisation() {
        Authorisation authorisation = createAuthorisation();

        assertNotNull(authorisation);
        assertThat(authorisation.getOfficeAuthId()).isEqualTo(2L);
        assertThat(authorisation.getJurisdiction()).isEqualTo("Languages");
        assertThat(authorisation.getTicketId()).isEqualTo(233432L);
        assertNotNull(authorisation.getStartDate());
        assertNotNull(authorisation.getEndDate());
        assertNotNull(authorisation.getCreatedDate());
        assertNotNull(authorisation.getLastUpdated());
        assertThat(authorisation.getLowerLevel()).isEqualTo("Welsh");
        assertNotNull(authorisation.getUserProfile());
    }
}
