package uk.gov.hmcts.reform.judicialapi.controller.util;

import org.junit.Test;
import org.springframework.data.domain.Pageable;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;

public class RefDataUtilTest {


    @Test
    public void testPaginationObject() {
        Pageable page = createPageableObject(0, 10, 10);
        assertEquals(0, page.first().getPageNumber());
        assertEquals(10, page.first().getPageSize());
    }

    @Test
    public void testPaginationObjectWhenSizeNotProvided() {
        Pageable page = createPageableObject(0, null, 10);
        assertEquals(0, page.first().getPageNumber());
        assertEquals(10, page.first().getPageSize());
    }

    @Test
    public void testPaginationObjectWhenPageNotProvided() {
        Pageable page = createPageableObject(null, 10, 10);
        assertEquals(0, page.first().getPageNumber());
        assertEquals(10, page.first().getPageSize());
    }

}
