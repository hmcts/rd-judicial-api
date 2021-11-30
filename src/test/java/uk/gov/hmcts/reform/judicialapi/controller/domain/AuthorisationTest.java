package uk.gov.hmcts.reform.judicialapi.controller.domain;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createAuthorisation;

class AuthorisationTest {

    @Test
    void testAuthorisation() {
        Authorisation authorisation = createAuthorisation();

        assertNotNull(authorisation);
        assertEquals(2L, authorisation.getOfficeAuthId());
        assertEquals("Languages", authorisation.getJurisdiction());
        assertEquals(233432L,authorisation.getTicketId());
        assertNotNull(authorisation.getStartDate());
        assertNotNull(authorisation.getEndDate());
        assertNotNull(authorisation.getCreatedDate());
        assertNotNull(authorisation.getLastUpdated());
        assertEquals("Welsh", authorisation.getLowerLevel());
        assertNotNull(authorisation.getUserProfile());
    }
}
