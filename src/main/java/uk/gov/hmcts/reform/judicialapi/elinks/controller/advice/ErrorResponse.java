package uk.gov.hmcts.reform.judicialapi.elinks.controller.advice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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