-- Grant usage and select privileges on Reader user for the audit tables
GRANT USAGE ON SCHEMA dbjudicialdata TO "${dbReaderUserName}";
GRANT SELECT ON ALL TABLES IN SCHEMA dbjudicialdata TO "${dbReaderUserName}";
