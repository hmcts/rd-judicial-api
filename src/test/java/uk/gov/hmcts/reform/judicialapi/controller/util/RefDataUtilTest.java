package uk.gov.hmcts.reform.judicialapi.controller.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;

class RefDataUtilTest {

    @Test
    void testPaginationObject() {
        final Pageable page = createPageableObject(0, 10, 10);
        assertEquals(0, page.first().getPageNumber());
        assertEquals(10, page.first().getPageSize());
    }

    @Test
    void testPaginationObjectWhenSizeNotProvided() {
        final Pageable page = createPageableObject(0, null, 10);
        assertEquals(0, page.first().getPageNumber());
        assertEquals(10, page.first().getPageSize());
    }

    @Test
    void testPaginationObjectWhenPageNotProvided() {
        final Pageable page = createPageableObject(null, 10, 10);
        assertEquals(0, page.first().getPageNumber());
        assertEquals(10, page.first().getPageSize());
    }

}
