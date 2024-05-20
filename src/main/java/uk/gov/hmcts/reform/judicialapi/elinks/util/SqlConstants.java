package uk.gov.hmcts.reform.judicialapi.elinks.util;

public class SqlConstants {

    private SqlConstants() {
    }

    public static final String SELECT_IDM_JOB_STATUS_SQL = "SELECT MAX(job_start_time) FROM"
            + " dbjudicialdata.dataload_schedular_job WHERE publishing_status IN ('SUCCESS')";

}
