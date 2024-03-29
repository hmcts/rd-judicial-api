package uk.gov.hmcts.reform.judicialapi.elinks.util;

public enum JobStatus {

    IN_PROGRESS("IN_PROGRESS"),

    FAILED("FAILED"),

    SUCCESS("SUCCESS");

    String status;

    JobStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
