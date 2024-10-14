-- Grant usage and select privs on Reader user
GRANT USAGE ON SCHEMA dbjudicialdata TO "${dbReaderUserName}";
GRANT SELECT ON ALL TABLES IN SCHEMA dbjudicialdata TO "${dbReaderUserName}";
