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

    public static final String GET_DELTA_LOAD_SIDAM_ID = "SELECT DISTINCT jup.sidam_id\n" +
        "FROM dbjudicialdata.judicial_user_profile jup\n" +
        "JOIN dbjudicialdata.judicial_office_appointment joa\n" +
        "  ON joa.personal_code = jup.personal_code\n" +
        " AND joa.last_loaded_date >= (\n" +
        "       SELECT MAX(scheduler_end_time)\n" +
        "       FROM dbjudicialdata.dataload_schedular_audit\n" +
        "       WHERE scheduler_name = 'judicial-ref-data-elinks'\n" +
        "         AND api_name = 'PublishSidamIds'\n" +
        "         AND status = 'SUCCESS'\n" +
        "     )\n" +
        "JOIN dbjudicialdata.judicial_office_authorisation joa2\n" +
        "  ON joa2.personal_code = jup.personal_code\n" +
        " AND joa2.last_updated >= (\n" +
        "       SELECT MAX(scheduler_end_time)\n" +
        "       FROM dbjudicialdata.dataload_schedular_audit\n" +
        "       WHERE scheduler_name = 'judicial-ref-data-elinks'\n" +
        "         AND api_name = 'PublishSidamIds'\n" +
        "         AND status = 'SUCCESS'\n" +
        "     )\n" +
        "JOIN dbjudicialdata.judicial_additional_roles jar\n" +
        "  ON jar.personal_code = jup.personal_code\n" +
        " AND jar.end_date >= (\n" +
        "       SELECT MAX(scheduler_end_time)\n" +
        "       FROM dbjudicialdata.dataload_schedular_audit\n" +
        "       WHERE scheduler_name = 'judicial-ref-data-elinks'\n" +
        "         AND api_name = 'PublishSidamIds'\n" +
        "         AND status = 'SUCCESS'\n" +
        "     ) \n" +
        "WHERE jup.sidam_id IS NOT NULL\n" +
        "  AND jup.last_loaded_date >= (\n" +
        "       SELECT MAX(scheduler_end_time)\n" +
        "       FROM dbjudicialdata.dataload_schedular_audit\n" +
        "       WHERE scheduler_name = 'judicial-ref-data-elinks'\n" +
        "         AND api_name = 'PublishSidamIds'\n" +
        "         AND status = 'SUCCESS'\n" +
        "     );";
}
