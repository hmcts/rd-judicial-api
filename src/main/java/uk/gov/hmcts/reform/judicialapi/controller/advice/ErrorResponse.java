package uk.gov.hmcts.reform.judicialapi.controller.advice;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int errorCode;

    private String status;

    private String errorMessage;

    private String errorDescription;

    private String timeStamp;

}
