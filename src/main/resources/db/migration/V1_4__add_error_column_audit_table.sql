ALTER TABLE dbjudicialdata.dataload_schedular_audit ADD COLUMN error_message VARCHAR(500) NULL;

ALTER TABLE dbjudicialdata.dataload_exception_records ADD COLUMN error_message VARCHAR(500) NULL;
