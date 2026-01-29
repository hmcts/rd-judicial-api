package uk.gov.hmcts.reform.judicialapi.elinks.util;

public class SqlContants {

    private SqlContants() {
    }

    public static final String INSERT_AUDIT_JOB = "INSERT INTO dbjudicialdata.dataload_schedular_job(job_start_time, "
            + "publishing_status) VALUES(?, ?)";

    public static final String SELECT_JOB_STATUS_SQL = "SELECT id, publishing_status FROM "
            + "dbjudicialdata.dataload_schedular_job WHERE DATE(job_start_time) = current_date";

    public static final String GET_DISTINCT_SIDAM_ID = "SELECT DISTINCT sidam_id FROM "
            + "dbjudicialdata.judicial_user_profile WHERE sidam_id IS NOT NULL";

    public static final String UPDATE_JOB_SQL = "UPDATE dbjudicialdata.dataload_schedular_job "
            + "SET job_end_time = NOW() , publishing_status = ? WHERE id =?";

    public static final String GET_DELTA_LOAD_SIDAM_ID = "SELECT DISTINCT jup.sidam_id" +
        " FROM dbjudicialdata.judicial_user_profile jup LEFT JOIN dbjudicialdata.judicial_office_appointment joa" +
        "  ON joa.personal_code = jup.personal_code LEFT JOIN dbjudicialdata.judicial_office_authorisation joa2" +
        "  ON joa2.personal_code = jup.personal_code LEFT JOIN dbjudicialdata.judicial_additional_roles jar" +
        "  ON jar.personal_code = jup.personal_code WHERE jup.sidam_id IS NOT NULL" +
        "  AND(jup.last_loaded_date >= '2025-01-22 07:08:45.775'" +
        "  OR joa.last_loaded_date >= '2026-01-22 07:08:45.775'" +
        "  OR joa2.last_updated >= '2026-01-22 07:08:45.775'" +
        "  OR jar.end_date BETWEEN '2026-01-22 07:08:45.775' AND now()" +
        "  OR joa.end_date BETWEEN '2026-01-22 07:08:45.775' AND now()" +
        "  OR joa2.end_date BETWEEN '2026-01-22 07:08:45.775' AND now())";
}
