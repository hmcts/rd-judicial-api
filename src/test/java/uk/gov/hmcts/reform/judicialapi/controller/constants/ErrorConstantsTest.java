package uk.gov.hmcts.reform.judicialapi.controller.constants;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.constants.ErrorConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.constants.ErrorConstants.EMPTY_RESULT_DATA_ACCESS;
import static uk.gov.hmcts.reform.judicialapi.constants.ErrorConstants.INVALID_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.judicialapi.constants.ErrorConstants.MALFORMED_JSON;
import static uk.gov.hmcts.reform.judicialapi.constants.ErrorConstants.UNSUPPORTED_MEDIA_TYPES;


class ErrorConstantsTest {

    @Test
    void test_shouldReturnMsgWhenMsgPassed() {
        ErrorConstants mailFormedJson = MALFORMED_JSON;
        assertNotNull(mailFormedJson);
        assertEquals(MALFORMED_JSON.getErrorMessage(), mailFormedJson.getErrorMessage());

        ErrorConstants mediaTypes = UNSUPPORTED_MEDIA_TYPES;
        assertNotNull(mediaTypes);
        assertEquals("2 : Unsupported Media Type", mediaTypes.getErrorMessage());

        ErrorConstants invalidExp = INVALID_REQUEST_EXCEPTION;
        assertNotNull(invalidExp);
        assertEquals("3 : There is a problem with your request. Please check and try again",
                invalidExp.getErrorMessage());

        ErrorConstants emptyResultDataAccess = EMPTY_RESULT_DATA_ACCESS;
        assertNotNull(emptyResultDataAccess);
        assertEquals("4 : Resource not found", emptyResultDataAccess.getErrorMessage());
    }
}
