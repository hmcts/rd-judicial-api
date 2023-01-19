
CREATE TABLE dbjudicialdata.dataload_schedular_audit (
	id serial NOT NULL,
	scheduler_name varchar(64) NOT NULL,
	scheduler_start_time timestamp NOT NULL,
	scheduler_end_time timestamp NULL,
	status varchar(32) NULL,
	api_name varchar(128) NULL,
	CONSTRAINT dataload_schedular_audit_pk PRIMARY KEY (id)
);

CREATE TABLE dbjudicialdata.dataload_schedular_job(
    id serial  NOT NULL,
    publishing_status VARCHAR(16),
    job_start_time TIMESTAMP NOT NULL,
    job_end_time TIMESTAMP,
    CONSTRAINT dataload_schedular_job_pk PRIMARY KEY (id)
);