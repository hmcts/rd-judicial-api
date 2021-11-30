package uk.gov.hmcts.reform.judicialapi.controller.controller.advice;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorResponseTest {

    @Test
    void test_ErrorResponse() {

        final int code = 1;
        final String status = "status";
        final String expectMsg = "msg";
        final String expectDesc = "desc";
        final String expectTs = "time";

        ErrorResponse errorDetails = ErrorResponse.builder()
                .errorCode(code)
                .status(status)
                .errorDescription("desc")
                .errorMessage(expectMsg)
                .timeStamp("time")
                .build();

        assertNotNull(errorDetails);
        assertEquals(code, errorDetails.getErrorCode());
        assertEquals(status, errorDetails.getStatus());
        assertEquals(expectMsg, errorDetails.getErrorMessage());
        assertEquals(expectTs, errorDetails.getTimeStamp());
        assertEquals(expectDesc, errorDetails.getErrorDescription());
    }

    @Test
    void test_NoArgsConstructor() {
        ErrorResponse errorResponse = new ErrorResponse();
        assertNotNull(errorResponse);
    }

    @Test
    void test_ErrorResponseWithConstructor() {

        final int code = 1;
        final String status = "status";
        final String expectMsg = "msg";
        final String expectDesc = "desc";
        final String expectTs = "time";

        ErrorResponse errorResponse = new ErrorResponse(1,"status","msg",
                "desc","time");
        assertNotNull(errorResponse);
        assertEquals(code, errorResponse.getErrorCode());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(expectMsg, errorResponse.getErrorMessage());
        assertEquals(expectTs, errorResponse.getTimeStamp());
        assertEquals(expectDesc, errorResponse.getErrorDescription());
    }
}
