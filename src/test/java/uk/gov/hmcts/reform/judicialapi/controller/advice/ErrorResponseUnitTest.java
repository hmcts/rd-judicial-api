package uk.gov.hmcts.reform.judicialapi.controller.advice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.http.HttpStatus;

public class ErrorResponseUnitTest {

    @Test
    public void testErrorResponse() {
        HttpStatus httpStatus = HttpStatus.OK;
        String expectMsg = "msg";
        String expectDesc = "desc";
        String expectTs = "time";

        ErrorResponse errorDetails = ErrorResponse.builder()
                .errorDescription("desc")
                .errorMessage(expectMsg)
                .timeStamp("time")
                .build();

        assertThat(errorDetails).isNotNull();
        assertThat(errorDetails.getErrorMessage()).isEqualTo(expectMsg);
        assertThat(errorDetails.getTimeStamp()).isEqualTo(expectTs);
        assertThat(errorDetails.getErrorDescription()).isEqualTo(expectDesc);
    }

    @Test
    public void test_NoArgsConstructor() {
        ErrorResponse errorResponse = new ErrorResponse();
        assertThat(errorResponse).isNotNull();
    }
}
