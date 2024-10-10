-- DROP SCHEMA dbjudicialdata;

-- NB Flyway requires lowercase for table names
create schema if not exists dbjudicialdata;

-- DROP SEQUENCE dbjudicialdata.dataload_exception_records_id_seq;

CREATE SEQUENCE dbjudicialdata.dataload_exception_records_id_seq
INCREMENT BY 1
MINVALUE 1
MAXVALUE 2147483647
START 1
CACHE 1
NO CYCLE;
-- DROP SEQUENCE dbjudicialdata.dataload_schedular_audit_id_seq;

CREATE SEQUENCE dbjudicialdata.dataload_schedular_audit_id_seq
INCREMENT BY 1
MINVALUE 1
MAXVALUE 2147483647
START 1
CACHE 1
NO CYCLE;
-- DROP SEQUENCE dbjudicialdata.dataload_schedular_job_id_seq;

CREATE SEQUENCE dbjudicialdata.dataload_schedular_job_id_seq
INCREMENT BY 1
MINVALUE 1
MAXVALUE 2147483647
START 1
CACHE 1
NO CYCLE;
-- DROP SEQUENCE dbjudicialdata.elink_audit_scheduler_id_sequence;

CREATE SEQUENCE dbjudicialdata.elink_audit_scheduler_id_sequence
INCREMENT BY 1
MINVALUE 1
MAXVALUE 2147483647
START 1
CACHE 1
NO CYCLE;
-- DROP SEQUENCE dbjudicialdata.elink_dataload_schedular_job_id_sequence;

CREATE SEQUENCE dbjudicialdata.elink_dataload_schedular_job_id_sequence
INCREMENT BY 1
MINVALUE 1
MAXVALUE 2147483647
START 1
CACHE 1
NO CYCLE;
-- DROP SEQUENCE dbjudicialdata.elink_exception_records_id_sequence;

CREATE SEQUENCE dbjudicialdata.elink_exception_records_id_sequence
INCREMENT BY 1
MINVALUE 1
MAXVALUE 2147483647
START 1
CACHE 1
NO CYCLE;
-- DROP SEQUENCE dbjudicialdata.elinks_responses_id_seq;

CREATE SEQUENCE dbjudicialdata.elinks_responses_id_seq
INCREMENT BY 1
MINVALUE 1
MAXVALUE 2147483647
START 1
CACHE 1
NO CYCLE;
-- DROP SEQUENCE dbjudicialdata.elinks_responses_sequence;

CREATE SEQUENCE dbjudicialdata.elinks_responses_sequence
INCREMENT BY 1
MINVALUE 1
MAXVALUE 2147483647
START 1
CACHE 1
NO CYCLE;
-- DROP SEQUENCE dbjudicialdata.judicial_office_appointment_id_sequence;

CREATE SEQUENCE dbjudicialdata.judicial_office_appointment_id_sequence
INCREMENT BY 1
MINVALUE 1
MAXVALUE 2147483647
START 1
CACHE 1
NO CYCLE;
-- DROP SEQUENCE dbjudicialdata.judicial_office_auth_id_sequence;

CREATE SEQUENCE dbjudicialdata.judicial_office_auth_id_sequence
INCREMENT BY 1
MINVALUE 1
MAXVALUE 2147483647
START 1
CACHE 1
NO CYCLE;
-- DROP SEQUENCE dbjudicialdata.judicial_role_type_id_sequence;

CREATE SEQUENCE dbjudicialdata.judicial_role_type_id_sequence
INCREMENT BY 1
MINVALUE 1
MAXVALUE 2147483647
START 1
CACHE 1
NO CYCLE;
-- DROP SEQUENCE dbjudicialdata.judicial_role_type_role_id_seq;

CREATE SEQUENCE dbjudicialdata.judicial_role_type_role_id_seq
INCREMENT BY 1
MINVALUE 1
MAXVALUE 2147483647
START 1
CACHE 1
NO CYCLE;-- dbjudicialdata.dataload_exception_records definition

-- Drop table

-- DROP TABLE dbjudicialdata.dataload_exception_records;

CREATE TABLE dbjudicialdata.dataload_exception_records (
	id int4 DEFAULT nextval('dbjudicialdata.elink_exception_records_id_sequence'::regclass) NOT NULL,
table_name varchar(64) NULL,
scheduler_start_time timestamp NOT NULL,
scheduler_name varchar(64) NOT NULL,
"key" varchar(256) NULL,
field_in_error varchar(256) NULL,
error_description varchar(512) NULL,
updated_timestamp timestamp NOT NULL,
row_id varchar(256) NULL,
page_id int8 NULL,
CONSTRAINT dataload_exception_records_pk PRIMARY KEY (id)
);


-- dbjudicialdata.dataload_schedular_audit definition

-- Drop table

-- DROP TABLE dbjudicialdata.dataload_schedular_audit;

CREATE TABLE dbjudicialdata.dataload_schedular_audit (
	id int4 DEFAULT nextval('dbjudicialdata.elink_audit_scheduler_id_sequence'::regclass) NOT NULL,
scheduler_name varchar(64) NOT NULL,
scheduler_start_time timestamp NOT NULL,
scheduler_end_time timestamp NULL,
status varchar(32) NULL,
api_name varchar(128) NULL,
CONSTRAINT dataload_schedular_audit_pk PRIMARY KEY (id)
);


-- dbjudicialdata.dataload_schedular_job definition

-- Drop table

-- DROP TABLE dbjudicialdata.dataload_schedular_job;

CREATE TABLE dbjudicialdata.dataload_schedular_job (
	id serial4 NOT NULL,
	publishing_status varchar(16) NULL,
	job_start_time timestamp NOT NULL,
	job_end_time timestamp NULL,
CONSTRAINT dataload_schedular_job_pk PRIMARY KEY (id)
);


-- dbjudicialdata.elinks_responses definition

-- Drop table

-- DROP TABLE dbjudicialdata.elinks_responses;

CREATE TABLE dbjudicialdata.elinks_responses (
	id int4 DEFAULT nextval('dbjudicialdata.elinks_responses_sequence'::regclass) NOT NULL,
api_name varchar(64) NOT NULL,
elinks_data jsonb NOT NULL,
created_date timestamp NOT NULL,
CONSTRAINT elinks_responses_pk PRIMARY KEY (id)
);


-- dbjudicialdata.hmcts_region_type definition

-- Drop table

-- DROP TABLE dbjudicialdata.hmcts_region_type;

CREATE TABLE dbjudicialdata.hmcts_region_type (
	hmcts_region_id varchar(64) NOT NULL,
	hmcts_region_desc_en varchar(256) NOT NULL,
	hmcts_region_desc_cy varchar(256) NULL,
CONSTRAINT hmcts_region_id PRIMARY KEY (hmcts_region_id)
);


-- dbjudicialdata.jrd_lrd_region_mapping definition

-- Drop table

-- DROP TABLE dbjudicialdata.jrd_lrd_region_mapping;

CREATE TABLE dbjudicialdata.jrd_lrd_region_mapping (
	jrd_region_id varchar(16) NOT NULL,
	jrd_region varchar(256) NOT NULL,
	region_id varchar(16) NOT NULL,
	region varchar(256) NOT NULL
);


-- dbjudicialdata.judicial_location_mapping definition

-- Drop table

-- DROP TABLE dbjudicialdata.judicial_location_mapping;

CREATE TABLE dbjudicialdata.judicial_location_mapping (
	epimms_id varchar(16) NULL,
	judicial_base_location_id varchar(64) NULL,
	service_code varchar(16) NULL
);


-- dbjudicialdata.judicial_service_code_mapping definition

-- Drop table

-- DROP TABLE dbjudicialdata.judicial_service_code_mapping;

CREATE TABLE dbjudicialdata.judicial_service_code_mapping (
	service_id int8 NOT NULL,
	ticket_code varchar(16) NOT NULL,
	service_code varchar(64) NULL,
	service_description varchar(512) NULL,
CONSTRAINT service_id PRIMARY KEY (service_id)
);


-- dbjudicialdata.judicial_ticket_code_type definition

-- Drop table

-- DROP TABLE dbjudicialdata.judicial_ticket_code_type;

CREATE TABLE dbjudicialdata.judicial_ticket_code_type (
	ticket_code varchar(16) NOT NULL,
	ticket_category_id varchar(16) NOT NULL,
	lower_level varchar(256) NOT NULL
);


-- dbjudicialdata.judicial_user_profile definition

-- Drop table

-- DROP TABLE dbjudicialdata.judicial_user_profile;

CREATE TABLE dbjudicialdata.judicial_user_profile (
	personal_code varchar(32) NOT NULL,
	known_as varchar(256) NOT NULL,
	surname varchar(256) NOT NULL,
	full_name varchar(256) NOT NULL,
	post_nominals varchar(256) NULL,
	ejudiciary_email varchar(256) NULL,
	last_working_date date NULL,
	active_flag bool NULL,
	created_date timestamp NULL,
	last_loaded_date timestamp NULL,
	object_id varchar(64) NULL,
	sidam_id varchar(64) NULL,
	initials varchar(256) NULL,
	title varchar(256) NULL,
	retirement_date date NULL,
	deleted_flag bool NULL,
	date_of_deletion timestamp NULL,
CONSTRAINT judicial_user_profile_pkey PRIMARY KEY (personal_code)
);


-- dbjudicialdata.location_type definition

-- Drop table

-- DROP TABLE dbjudicialdata.location_type;

CREATE TABLE dbjudicialdata.location_type (
	base_location_id varchar(64) NOT NULL,
"name" varchar(256) NULL,
type_id varchar(64) NULL,
parent_id varchar(64) NULL,
jurisdiction_id varchar(64) NULL,
start_date timestamp NULL,
end_date timestamp NULL,
created_at timestamp NULL,
updated_at timestamp NULL,
CONSTRAINT base_location_id PRIMARY KEY (base_location_id)
);


-- dbjudicialdata.lock_details_provider definition

-- Drop table

-- DROP TABLE dbjudicialdata.lock_details_provider;

CREATE TABLE dbjudicialdata.lock_details_provider (
	"name" varchar(64) NOT NULL,
	lock_until timestamp NOT NULL,
	locked_at timestamp NOT NULL,
	locked_by varchar(255) NOT NULL,
CONSTRAINT lock_details_provider_pkey PRIMARY KEY (name)
);


-- dbjudicialdata.judicial_additional_roles definition

-- Drop table

-- DROP TABLE dbjudicialdata.judicial_additional_roles;

CREATE TABLE dbjudicialdata.judicial_additional_roles (
	role_id int4 DEFAULT nextval('dbjudicialdata.judicial_role_type_id_sequence'::regclass) NOT NULL,
personal_code varchar(32) NOT NULL,
title varchar(256) NOT NULL,
start_date timestamp NULL,
end_date timestamp NULL,
jurisdiction_role_id varchar(64) NOT NULL,
jurisdiction_role_name_id varchar(64) NOT NULL,
CONSTRAINT role_id PRIMARY KEY (role_id),
CONSTRAINT personal_code_fk FOREIGN KEY (personal_code) REFERENCES dbjudicialdata.judicial_user_profile(personal_code)
);


-- dbjudicialdata.judicial_office_appointment definition

-- Drop table

-- DROP TABLE dbjudicialdata.judicial_office_appointment;

CREATE TABLE dbjudicialdata.judicial_office_appointment (
	judicial_office_appointment_id int8 DEFAULT nextval('dbjudicialdata.judicial_office_appointment_id_sequence'::regclass) NOT NULL,
personal_code varchar(32) NOT NULL,
base_location_id varchar(64) NOT NULL,
hmcts_region_id varchar(64) NULL,
is_prinicple_appointment bool NULL,
start_date date NULL,
end_date date NULL,
created_date timestamp NULL,
last_loaded_date timestamp NULL,
epimms_id varchar(16) NULL,
appointment varchar(64) NOT NULL,
appointment_type varchar(32) NULL,
appointment_id varchar(256) NOT NULL,
role_name_id varchar(256) NULL,
"type" varchar(64) NULL,
contract_type_id varchar(64) NOT NULL,
"location" varchar(64) NULL,
jo_base_location_id varchar(64) NOT NULL,
CONSTRAINT judicial_office_appointment_appointment_id_key UNIQUE (appointment_id),
CONSTRAINT judicial_office_appointment_id PRIMARY KEY (judicial_office_appointment_id),
CONSTRAINT base_location_id_fk1 FOREIGN KEY (base_location_id) REFERENCES dbjudicialdata.location_type(base_location_id),
CONSTRAINT hmcts_region_id_fk1 FOREIGN KEY (hmcts_region_id) REFERENCES dbjudicialdata.hmcts_region_type(hmcts_region_id),
CONSTRAINT jo_base_location_id_fk1 FOREIGN KEY (jo_base_location_id) REFERENCES dbjudicialdata.location_type(base_location_id),
CONSTRAINT personal_code_fk FOREIGN KEY (personal_code) REFERENCES dbjudicialdata.judicial_user_profile(personal_code)
);


-- dbjudicialdata.judicial_office_authorisation definition

-- Drop table

-- DROP TABLE dbjudicialdata.judicial_office_authorisation;

CREATE TABLE dbjudicialdata.judicial_office_authorisation (
	judicial_office_auth_id int8 DEFAULT nextval('dbjudicialdata.judicial_office_auth_id_sequence'::regclass) NOT NULL,
personal_code varchar(32) NOT NULL,
jurisdiction varchar(256) NOT NULL,
start_date date NULL,
end_date date NULL,
created_date timestamp NULL,
last_updated timestamp NULL,
lower_level varchar(256) NOT NULL,
ticket_code varchar(16) NOT NULL,
appointment_id varchar(256) NOT NULL,
authorisation_id varchar(256) NOT NULL,
jurisdiction_id varchar(64) NOT NULL,
CONSTRAINT jud_auth_pk PRIMARY KEY (judicial_office_auth_id),
CONSTRAINT appointment_id_fk2 FOREIGN KEY (appointment_id) REFERENCES dbjudicialdata.judicial_office_appointment(appointment_id),
CONSTRAINT personal_code_fk FOREIGN KEY (personal_code) REFERENCES dbjudicialdata.judicial_user_profile(personal_code)
);


INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('29096', '754', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('425094', '893', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('', '1030', 'BFA1');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('', '1032', 'BBA3');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '747', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '748', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '749', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '750', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('312962', '752', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('407494', '753', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('337856', '755', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('718075', '756', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('699560', '757', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('127994', '758', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('337959', '759', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('403689', '760', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('353988', '761', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('229786', '762', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('36791', '764', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('29656', '765', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('356855', '766', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '767', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('739514', '769', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('407494', '770', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('898213', '771', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('25463', '772', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('13660', '773', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('674229', '775', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('400947', '776', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('621184', '777', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('268374', '778', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('373584', '780', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('228015', '781', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('817181', '783', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('694840', '784', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('507931', '785', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('259679', '786', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('428073', '787', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('468679', '789', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('357989', '790', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101183', '791', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('486853', '792', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('465872', '793', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('471332', '794', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('371016', '795', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('471569', '796', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('256379', '797', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('198303', '798', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('694840', '802', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('446255', '803', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('478896', '804', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('298390', '805', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('257431', '806', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('650344', '807', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('259679', '808', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('816875', '809', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('194172', '812', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('609320', '814', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('568484', '815', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('784691', '816', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('256913', '817', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('437303', '818', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('317442', '819', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('471349', '820', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101183', '822', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('465872', '826', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('815997', '828', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('471332', '829', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('371016', '830', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('471569', '831', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('185657', '832', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('224403', '834', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('781139', '835', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('298828', '836', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('659591', '838', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('403751', '840', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('493880', '841', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('411234', '842', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('819890', '843', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('477819', '844', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('198592', '845', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('771467', '827', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('100539', '846', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('382409', '847', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('382409', '848', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('545334', '849', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('315404', '850', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('774335', '851', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('271813', '853', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101633', '854', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101633', '855', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('624161', '856', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('624161', '858', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('735217', '859', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('735217', '860', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('339463', '861', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('339463', '862', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('235617', '863', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('475776', '864', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('475776', '865', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('457273', '867', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('457273', '868', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('416742', '871', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('416742', '872', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('460592', '873', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('460592', '874', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('817113', '875', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('817113', '876', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('43104', '877', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('43104', '878', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('438850', '879', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('438850', '880', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('886493', '881', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('886493', '882', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('652852', '884', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('201339', '885', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('201339', '886', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('455368', '887', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('424213', '888', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('424213', '889', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('117667', '890', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('223503', '892', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195465', '894', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('318389', '895', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195489', '896', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195489', '897', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('228883', '898', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('259170', '899', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195472', '900', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195472', '901', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195496', '902', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195496', '903', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('102050', '905', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('102050', '906', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('482914', '907', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('231596', '908', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('231596', '909', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('497679', '910', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('497679', '911', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('758998', '912', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('177463', '914', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('736719', '915', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('41047', '917', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('41047', '918', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('574546', '919', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('88516', '920', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('88516', '921', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('288691', '922', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('45900', '925', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('491107', '926', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('386393', '928', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('427519', '929', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('478126', '930', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('478126', '931', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('505683', '933', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('197852', '935', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195520', '936', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195520', '937', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('497583', '904', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('455174', '938', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('455174', '939', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('659436', '943', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('562808', '945', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('744412', '947', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('232607', '949', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('232607', '950', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('318324', '951', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('563156', '952', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('517400', '953', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195537', '954', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195537', '955', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('852649', '956', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('107581', '957', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('67542', '958', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('761518', '961', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('444097', '962', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('150431', '963', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('447533', '965', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('448747', '967', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('448747', '968', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('45106', '970', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('45106', '971', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('226511', '972', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('566296', '973', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('353615', '975', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('345663', '976', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('701411', '978', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('232580', '980', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('563906', '982', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('560788', '983', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('209396', '987', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('245068', '988', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('468040', '989', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('448077', '990', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('144641', '991', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('326944', '992', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('409795', '993', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('827534', '994', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('304576', '995', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('304576', '996', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('366572', '999', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('366572', '1000', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('234850', '1001', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('234850', '1002', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('302630', '1003', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101959', '1004', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101959', '1005', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('700596', '1008', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('860090', '1009', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('390932', '1010', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('448345', '1012', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('448345', '1013', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('211138', '1014', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('211138', '1015', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('846055', '1016', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('217250', '1017', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('217250', '1018', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('200518', '1019', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('232298', '1020', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('846055', '1021', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('234946', '1023', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('234946', '1024', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('279152', '1025', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('103147', '1026', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('637145', '1027', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('416695', '1060', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('839746', '1061', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('549957', '1063', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('664444', '1065', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('745389', '1066', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('514973', '1067', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('597501', '1068', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('784131', '1069', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('747662', '1070', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('490237', '1451', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('536548', '1071', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('218723', '1073', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('264828', '1074', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('618632', '1075', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('703200', '1076', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('784730', '1077', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('417439', '1078', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('652852', '1079', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('370964', '1080', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('484482', '1081', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('590621', '1082', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('443257', '1083', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('580554', '1084', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('517400', '1085', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('641199', '1086', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('720624', '1087', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('320113', '1088', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('574546', '1089', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('449358', '1090', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('569737', '1091', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('362420', '1093', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('360566', '1094', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('336348', '1095', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('783803', '1096', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('243126', '1097', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('345663', '1098', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('215156', '1099', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('409795', '1101', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('568484', '1102', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('659591', '1103', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('497356', '1104', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('287515', '1105', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('624810', '1106', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('245287', '1107', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('252292', '1108', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('732661', '1109', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('625697', '1110', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('583034', '1111', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('417418', '1112', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('777942', '1113', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('224403', '1114', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('379247', '1115', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('314074', '1117', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('450049', '1118', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('764728', '1119', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('781155', '1120', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('330480', '1121', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('506742', '1123', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('779109', '1124', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('211138', '1125', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('324413', '1126', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('296111', '1127', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '1147', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('427519', '1156', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('326813', '1163', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('602948', '1164', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('534157', '1165', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('330480', '1166', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('541183', '1167', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('787030', '1168', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('361595', '1169', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('218723', '1170', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('478126', '1171', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('371016', '1172', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('819890', '1173', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('379656', '1192', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('640119', '1194', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('711798', '1437', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('270253', '1445', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('771467', '1446', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('736719', '916', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('232580', '981', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('415903', '1191', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('450049', '1447', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('450049', '1448', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('640119', '1449', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('45900', '1450', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('366796', '1452', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('365554', '825', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('228231', '913', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('214320', '964', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('356855', '768', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('541183', '1072', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('317334', '1100', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('366796', '942', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('498443', '1159', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('', '1036', 'BHA1');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('', '1037', 'BHA1');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('', '1032', 'BBA2');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('', '1032', 'BBA1');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('450049', '866', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('774335', '1863', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('457273', '1915', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('457273', '1455', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('336348', '1920', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('117667', '1867', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101633', '1122', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('257431', '969', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101959', '2026', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('787030', '2016', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('288691', '1931', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('640119', '923', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('609320', '2054', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('361595', '2044', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('455368', '2068', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('448345', '2051', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('846055', '2070', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('200518', '2079', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('416742', '2067', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('416742', '2071', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('270253', '2076', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('659436', '2111', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('562808', '2116', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '1633', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('817113', '2109', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('563156', '2139', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('781139', '1456', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('213971', '839', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('852649', '1092', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('409795', '985', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('409795', '2105', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('624161', '2149', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('624161', '2150', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('498443', '941', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('701411', '2218', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('735217', '2293', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('782795', '2294', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('29096', '754', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('425094', '893', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('', '1030', 'BFA1');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('', '1032', 'BBA3');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '747', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '748', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '749', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '750', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('312962', '752', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('407494', '753', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('337856', '755', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('718075', '756', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('699560', '757', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('127994', '758', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('337959', '759', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('403689', '760', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('353988', '761', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('229786', '762', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('36791', '764', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('29656', '765', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('356855', '766', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '767', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('739514', '769', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('407494', '770', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('898213', '771', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('25463', '772', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('13660', '773', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('674229', '775', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('400947', '776', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('621184', '777', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('268374', '778', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('373584', '780', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('228015', '781', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('817181', '783', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('694840', '784', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('507931', '785', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('259679', '786', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('428073', '787', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('468679', '789', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('357989', '790', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101183', '791', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('486853', '792', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('465872', '793', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('471332', '794', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('371016', '795', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('471569', '796', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('256379', '797', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('198303', '798', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('694840', '802', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('446255', '803', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('478896', '804', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('298390', '805', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('257431', '806', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('650344', '807', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('259679', '808', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('816875', '809', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('194172', '812', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('609320', '814', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('568484', '815', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('784691', '816', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('256913', '817', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('437303', '818', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('317442', '819', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('471349', '820', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101183', '822', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('465872', '826', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('815997', '828', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('471332', '829', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('371016', '830', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('471569', '831', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('185657', '832', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('224403', '834', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('781139', '835', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('298828', '836', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('659591', '838', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('403751', '840', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('493880', '841', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('411234', '842', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('819890', '843', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('477819', '844', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('198592', '845', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('771467', '827', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('100539', '846', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('382409', '847', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('382409', '848', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('545334', '849', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('315404', '850', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('774335', '851', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('271813', '853', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101633', '854', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101633', '855', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('624161', '856', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('624161', '858', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('735217', '859', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('735217', '860', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('339463', '861', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('339463', '862', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('235617', '863', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('475776', '864', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('475776', '865', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('457273', '867', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('457273', '868', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('416742', '871', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('416742', '872', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('460592', '873', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('460592', '874', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('817113', '875', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('817113', '876', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('43104', '877', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('43104', '878', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('438850', '879', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('438850', '880', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('886493', '881', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('886493', '882', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('652852', '884', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('201339', '885', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('201339', '886', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('455368', '887', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('424213', '888', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('424213', '889', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('117667', '890', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('223503', '892', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195465', '894', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('318389', '895', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195489', '896', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195489', '897', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('228883', '898', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('259170', '899', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195472', '900', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195472', '901', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195496', '902', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195496', '903', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('102050', '905', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('102050', '906', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('482914', '907', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('231596', '908', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('231596', '909', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('497679', '910', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('497679', '911', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('758998', '912', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('177463', '914', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('736719', '915', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('41047', '917', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('41047', '918', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('574546', '919', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('88516', '920', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('88516', '921', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('288691', '922', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('45900', '925', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('491107', '926', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('386393', '928', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('427519', '929', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('478126', '930', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('478126', '931', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('505683', '933', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('197852', '935', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195520', '936', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195520', '937', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('497583', '904', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('455174', '938', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('455174', '939', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('659436', '943', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('562808', '945', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('744412', '947', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('232607', '949', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('232607', '950', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('318324', '951', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('563156', '952', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('517400', '953', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195537', '954', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('195537', '955', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('852649', '956', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('107581', '957', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('67542', '958', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('761518', '961', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('444097', '962', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('150431', '963', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('447533', '965', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('448747', '967', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('448747', '968', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('45106', '970', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('45106', '971', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('226511', '972', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('566296', '973', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('353615', '975', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('345663', '976', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('701411', '978', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('232580', '980', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('563906', '982', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('560788', '983', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('209396', '987', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('245068', '988', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('468040', '989', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('448077', '990', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('144641', '991', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('326944', '992', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('409795', '993', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('827534', '994', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('304576', '995', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('304576', '996', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('366572', '999', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('366572', '1000', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('234850', '1001', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('234850', '1002', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('302630', '1003', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101959', '1004', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101959', '1005', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('700596', '1008', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('860090', '1009', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('390932', '1010', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('448345', '1012', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('448345', '1013', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('211138', '1014', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('211138', '1015', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('846055', '1016', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('217250', '1017', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('217250', '1018', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('200518', '1019', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('232298', '1020', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('846055', '1021', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('234946', '1023', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('234946', '1024', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('279152', '1025', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('103147', '1026', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('637145', '1027', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('416695', '1060', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('839746', '1061', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('549957', '1063', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('664444', '1065', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('745389', '1066', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('514973', '1067', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('597501', '1068', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('784131', '1069', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('747662', '1070', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('490237', '1451', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('536548', '1071', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('218723', '1073', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('264828', '1074', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('618632', '1075', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('703200', '1076', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('784730', '1077', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('417439', '1078', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('652852', '1079', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('370964', '1080', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('484482', '1081', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('590621', '1082', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('443257', '1083', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('580554', '1084', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('517400', '1085', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('641199', '1086', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('720624', '1087', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('320113', '1088', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('574546', '1089', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('449358', '1090', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('569737', '1091', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('362420', '1093', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('360566', '1094', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('336348', '1095', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('783803', '1096', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('243126', '1097', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('345663', '1098', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('215156', '1099', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('409795', '1101', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('568484', '1102', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('659591', '1103', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('497356', '1104', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('287515', '1105', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('624810', '1106', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('245287', '1107', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('252292', '1108', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('732661', '1109', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('625697', '1110', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('583034', '1111', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('417418', '1112', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('777942', '1113', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('224403', '1114', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('379247', '1115', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('314074', '1117', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('450049', '1118', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('764728', '1119', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('781155', '1120', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('330480', '1121', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('506742', '1123', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('779109', '1124', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('211138', '1125', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('324413', '1126', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('296111', '1127', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '1147', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('427519', '1156', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('326813', '1163', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('602948', '1164', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('534157', '1165', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('330480', '1166', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('541183', '1167', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('787030', '1168', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('361595', '1169', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('218723', '1170', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('478126', '1171', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('371016', '1172', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('819890', '1173', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('379656', '1192', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('640119', '1194', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('711798', '1437', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('270253', '1445', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('771467', '1446', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('736719', '916', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('232580', '981', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('415903', '1191', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('450049', '1447', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('450049', '1448', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('640119', '1449', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('45900', '1450', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('366796', '1452', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('365554', '825', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('228231', '913', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('214320', '964', NULL);
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('356855', '768', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('541183', '1072', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('317334', '1100', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('366796', '942', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('498443', '1159', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('', '1036', 'BHA1');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('', '1037', 'BHA1');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('', '1032', 'BBA2');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('', '1032', 'BBA1');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('450049', '866', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('774335', '1863', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('457273', '1915', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('457273', '1455', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('336348', '1920', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('117667', '1867', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101633', '1122', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('257431', '969', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('101959', '2026', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('787030', '2016', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('288691', '1931', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('640119', '923', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('609320', '2054', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('361595', '2044', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('455368', '2068', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('448345', '2051', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('846055', '2070', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('200518', '2079', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('416742', '2067', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('416742', '2071', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('270253', '2076', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('659436', '2111', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('562808', '2116', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('20262', '1633', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('817113', '2109', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('563156', '2139', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('781139', '1456', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('213971', '839', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('852649', '1092', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('409795', '985', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('409795', '2105', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('624161', '2149', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('624161', '2150', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('498443', '941', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('701411', '2218', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('735217', '2293', '');
INSERT INTO dbjudicialdata.judicial_location_mapping (epimms_id, judicial_base_location_id, service_code) VALUES('782795', '2294', '');


INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1', 'National', '12', 'National');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2', 'National England and Wales', '12', 'National');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('3', 'Taylor House (London)', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('4', 'Hatton Cross (London)', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('5', 'Newport (Wales)', '7', 'Wales');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('6', 'Glasgow (Scotland and NI)', '11', 'Scotland');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('7', 'Birmingham', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('8', 'North Shields', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('9', 'Stoke', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('10', 'Manchester', '4', 'North West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('11', 'Bradford', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('12', 'Nottingham', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('13', 'Field House (London)', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('14', 'London', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('15', 'London Central', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('16', 'London East', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('17', 'London South', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('18', 'South East', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('19', 'South Eastern', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('20', 'Midlands', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('21', 'Midlands East', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('22', 'Midlands West', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('23', 'South West', '6', 'South West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('24', 'South Western', '6', 'South West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('25', 'North West', '4', 'North West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('26', 'North East', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('27', 'Wales', '7', 'Wales');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('28', 'Scotland', '11', 'Scotland');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('33', 'Newcastle', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('35', 'EAT - Rolls Building', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('32', 'Yorkshire and the Humber', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1561', 'East Midlands', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2010', 'West Midlands', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2166', 'North and East London', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2207', 'Thames Valley', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2152', 'Kent', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2053', 'Central and South London', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2204', 'Sussex', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2137', 'Greater Manchester', '4', 'North West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2167', 'North and West Yorkshire', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2163', 'Mid and South Glamorgan', '7', 'Wales');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2045', 'Cambridgeshire', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2173', 'North Wales', '7', 'Wales');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2147', 'Humber', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2174', 'Northamptonshire', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2145', 'Hampshire and Isle of Wight', '6', 'South West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2129', 'Gloucestershire', '6', 'South West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2173', 'North Yorkshire', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2066', 'Cleveland and County Durham and Darling', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2177', 'Nottinghamshire', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2214', 'West Yorkshire', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2197', 'South Yorkshire', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2203', 'Surrey', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2040', 'Black Country', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2090', 'Derbyshire', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2117', 'Dyfed Powys', '7', 'Wales');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2141', 'Gwent', '7', 'Wales');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2058', 'Central London', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2199', 'Staffordshire', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2212', 'West Glamorgan', '7', 'Wales');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2014', 'Avon and Somerset', '6', 'South West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2064', 'Cleveland', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1719', 'East of England', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2213', 'West Mercia', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2175', 'Northumbria', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1959', 'Southern', '6', 'South West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1516', 'Eastern', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('71', 'Cambridge', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1565', 'Hertfordshire', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1787', 'Belfast', '10', 'Northern Ireland');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1533', 'Bath', '6', 'South West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2258', 'Field House FT (London)', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1819', 'Glasgow (Scotland & NI)', '11', 'Scotland');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2051', 'Hampshire', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1268', 'Oxfordshire', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2113', 'St Ives', '6', 'South West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1282', 'Berkshire', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1261', 'Suffolk', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1967', 'Chepstow', '7', 'Wales');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2033', 'Edinburgh', '11', 'Scotland');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1214', 'Somerset', '6', 'South West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1973', 'Yorkshire & Humberside', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1524', 'Shropshire', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1630', 'South Wales', '7', 'Wales');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2086', 'Northumberland', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1672', 'Bedfordshire', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1623', 'Essex', '5', 'South East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2295', 'Field House - Upper Tribunal', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1252', 'Birmingham and Solihull', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1218', 'Cheshire', '4', 'North West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1254', 'Coventry', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1644', 'Coventry and Warwickshire', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1635', 'Devon and Cornwall', '6', 'South West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1554', 'Lancashire', '4', 'North West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1551', 'Leeds District', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('2157', 'Leicestershire', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1548', 'Leicestershire and Rutland', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1543', 'London West', '1', 'London');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1240', 'Lincolnshire', '2', 'Midlands');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1798', 'Merseyside', '4', 'North West');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1539', 'Newcastle Upon Tyne', '3', 'North East');
INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id, jrd_region, region_id, region) VALUES('1260', 'Norfolk', '6', 'South East');


INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(1, '368', 'BBA3', 'Social Security and Child Support');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(2, '364', 'BBA3', 'Social Security and Child Support');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(3, '362', 'BBA3', 'Social Security and Child Support');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(4, '365', 'BBA3', 'Social Security and Child Support');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(5, '366', 'BBA3', 'Social Security and Child Support');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(6, '367', 'BBA3', 'Social Security and Child Support');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(7, '369', 'BBA3', 'Social Security and Child Support');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(8, '290', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(9, '291', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(10, '387', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(11, '318', 'BGA1', 'Agricultural Land and Drainage');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(13, '303', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(14, '320', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(15, '304', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(16, '321', 'BCA1', 'Care Standards');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(17, '305', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(18, '292', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(19, '293', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(20, '322', 'BAA2', 'Charity');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(22, '407', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(23, '294', 'AAA1', 'Civil Enforcement');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(24, '294', 'AAA2', 'Insolvency');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(25, '294', 'AAA3', 'Mortgage and Landlord Possession Claims');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(26, '294', 'AAA4', 'Non-money Claims');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(27, '294', 'AAA5', 'Return of Goods Claims');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(28, '294', 'AAA6', 'Specified Money Claims');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(29, '294', 'AAA7', 'Damages');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(30, '295', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(31, '325', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(32, '326', 'BAA4', 'Consumer Credit');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(33, '307', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(34, '313', 'ABA7', 'Court of Protections');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(35, '306', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(36, '328', 'BBA2', 'Criminal Injuries Compensation');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(40, '405', 'ABA8', 'REMO');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(41, '331', 'BAA5', 'Environment');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(42, '332', 'BAA6', 'Estate Agents');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(43, '308', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(44, '389', 'ABA3', 'Family Public Law');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(45, '389', 'ABA4', 'Adoption');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(46, '389', 'ABA5', 'Family Private Law');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(47, '389', 'ABA8', 'REMO');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(48, '410', 'ABA2', 'Financial Remedy');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(49, '409', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(51, '392', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(52, '374', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(53, '373', 'BFA1', 'Immigration and Asylum Appeals');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(54, '375', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(56, '377', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(57, '378', 'BEA1', 'War Pensions Appeals');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(58, '395', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(59, '334', 'BAA8', 'Gambling Appeals');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(60, '336', 'BAB1', 'Information Rights');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(61, '337', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(62, '340', 'BGA2', 'Land Registration');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(63, '341', 'BAB2', 'Local Government Standards');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(64, '406', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(65, '412', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(66, '342', 'BCA2', 'Mental Health');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(67, '343', 'BCA2', 'Mental Health');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(68, '298', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(69, '309', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(70, '346', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(71, '404', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(72, '379', 'BHA1', 'Employment Claims');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(73, '381', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(74, '382', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(75, '383', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(76, '384', 'BBA1', 'Asylum Support');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(77, '299', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(78, '347', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(79, '350', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(80, '394', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(81, '349', 'BCA3', 'Primary Health Lists');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(82, '315', 'ABA5', 'Family Private Law');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(83, '316', 'ABA3', 'Family Public Law');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(84, '351', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(85, '352', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(86, '353', 'BHA3', 'Reserve Forces Appeal Tribunal');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(87, '354', 'BGA3', 'Residential Property');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(88, '356', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(89, '397', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(91, '398', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(93, '300', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(94, '396', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(95, '301', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(96, '393', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(97, '311', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(98, '312', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(99, '408', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(100, '357', 'BBA3', 'Social Security and Child Support');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(101, '358', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(102, '411', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(103, '302', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(104, '310', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(105, '359', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(106, '360', 'BAB3', 'Transport');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(107, '370', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(108, '371', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(109, '372', 'BAA9', 'Immigration Services');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(110, '385', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(111, '386', 'BDA2', 'Tax Appeals');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(112, '289', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(92, '317', 'ABA5', 'Family Private Law');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(90, '400', 'ABA5', 'Family Private Law');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(113, '388', ' ', '');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(114, '1416', 'BBA3', 'Social Security and Child Support');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(115, '400', 'ABA3', 'Family Public Law');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(116, '317', 'ABA3', 'Family Public Law');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(117, '374', 'BCA1', 'Care Standards');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(118, '374', 'BCA2', 'Mental Health');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(119, '374', 'BCA3', 'Primary Health Lists');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(120, '374', 'BCA4', 'Special Educational Needs');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(122, '380', 'BHA1', 'Employment Claims');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(123, '1413', 'BHA1', 'Employment Claims');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(124, '405', 'ABA5', 'Family Private Law');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(125, '1452', 'ABA3', 'Family Public Law');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(126, '1452', 'ABA5', 'Family Private Law');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(127, '1453', 'ABA5', 'Family Private Law');
INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id, ticket_code, service_code, service_description) VALUES(128, '405', 'ABA5', 'Family Private Law');

INSERT INTO dbjudicialdata.hmcts_region_type (hmcts_region_id, hmcts_region_desc_en, hmcts_region_desc_cy) VALUES('0', 'default', 'default');
INSERT INTO dbjudicialdata.hmcts_region_type (hmcts_region_id, hmcts_region_desc_en, hmcts_region_desc_cy) VALUES('1', 'London', '');
INSERT INTO dbjudicialdata.hmcts_region_type (hmcts_region_id, hmcts_region_desc_en, hmcts_region_desc_cy) VALUES('2', 'Midlands', '');
INSERT INTO dbjudicialdata.hmcts_region_type (hmcts_region_id, hmcts_region_desc_en, hmcts_region_desc_cy) VALUES('3', 'North East', '');
INSERT INTO dbjudicialdata.hmcts_region_type (hmcts_region_id, hmcts_region_desc_en, hmcts_region_desc_cy) VALUES('4', 'North West', '');
INSERT INTO dbjudicialdata.hmcts_region_type (hmcts_region_id, hmcts_region_desc_en, hmcts_region_desc_cy) VALUES('5', 'South East', '');
INSERT INTO dbjudicialdata.hmcts_region_type (hmcts_region_id, hmcts_region_desc_en, hmcts_region_desc_cy) VALUES('6', 'South West', '');
INSERT INTO dbjudicialdata.hmcts_region_type (hmcts_region_id, hmcts_region_desc_en, hmcts_region_desc_cy) VALUES('7', 'Wales', '');
INSERT INTO dbjudicialdata.hmcts_region_type (hmcts_region_id, hmcts_region_desc_en, hmcts_region_desc_cy) VALUES('10', 'Northern Ireland', '');
INSERT INTO dbjudicialdata.hmcts_region_type (hmcts_region_id, hmcts_region_desc_en, hmcts_region_desc_cy) VALUES('11', 'Scotland', '');
INSERT INTO dbjudicialdata.hmcts_region_type (hmcts_region_id, hmcts_region_desc_en, hmcts_region_desc_cy) VALUES('12', 'National', '');


INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('289', '59', 'Welsh Language');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('290', '24', 'Administrative Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('291', '24', 'Admiralty - KBD');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('292', '24', 'Chancery');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('293', '24', 'Chancery Business in the County Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('294', '24', 'Civil Authorisation');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('295', '24', 'Commercial');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('296', '24', 'Companies Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('298', '24', 'Mercantile - KBD');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('299', '24', 'Patents');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('300', '24', 'Section 9(1) Chancery');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('301', '24', 'Section 9(1) Kings Bench');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('302', '24', 'Technology and Construction Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('303', '25', 'Appeals in Crown Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('304', '25', 'Attempted Murder');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('305', '25', 'Central Criminal Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('306', '25', 'Criminal Authorisation');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('307', '25', 'Court of Appeal Criminal Division');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('308', '25', 'Extradition');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('309', '25', 'Murder');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('310', '25', 'Terrorism');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('311', '25', 'Serious Sexual Offences');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('312', '25', 'Serious Sexual Offences - Youth Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('313', '26', 'Court of Protection');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('314', '26', 'Financial Remedy Appeals');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('315', '26', 'Private Law');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('316', '26', 'Public Law');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('317', '26', 'Section 9-1 Family');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('318', '16', 'Agricultural Land and Drainage');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('319', '16', 'Agricultural Lands Tribunal Wales');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('320', '10', 'Asylum Support');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('321', '11', 'Care Standards');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('322', '13', 'Charity');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('323', '13', 'Claims Management Services');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('324', '16', 'Community Rights to Bid');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('325', '103', 'Competition Appeal Tribunal');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('326', '13', 'Consumer Credit');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('327', '105', 'Copyright Tribunal');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('328', '10', 'Criminal Injuries Compensations');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('329', '106', 'Design Tribunal');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('330', '14', 'Direct and Indirect Taxation');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('331', '13', 'Environment');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('332', '13', 'Estate Agents');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('333', '13', 'Food');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('334', '13', 'Gambling');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('335', '13', 'Immigration Services');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('336', '13', 'Information Rights');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('337', '21', 'Judicial Review - England and Wales');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('338', '21', 'Judicial Review - Northern Ireland');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('339', '21', 'Judicial Review - Scotland');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('340', '16', 'Land Registration');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('341', '13', 'Local Government Standards - England');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('342', '11', 'Mental Health');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('343', '11', 'Mental Health Tribunal Wales');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('344', '42', 'Motor Insurers Bureau');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('345', '14', 'MP''s Expenses');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('346', '13', 'National Security');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('347', '13', 'Pensions Regulations');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('348', '13', 'Professional Regulations');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('349', '11', 'Primary Health List');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('350', '45', 'Police Appeals Tribunal');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('351', '13', 'Race Panel');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('352', '47', 'Reinstatement Committee');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('353', '15', 'Reserve Forces');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('354', '16', 'Residential Property');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('355', '49', 'Residential Property Tribunal Wales');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('356', '11', 'Restricted Patients Panel');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('357', '10', 'Social Security and Child Support');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('358', '11', 'Special Educational Needs and Disability');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('359', '54', 'Trademark Tribunals');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('360', '55', 'Transport');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('361', '56', 'Valuation Tribunal England');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('362', '10', '02 - Child Support');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('364', '10', '01 - Social Security');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('365', '10', '03 - Disability Living Allowance');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('366', '10', '04 - Incapacity Benefit Employment Support');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('367', '10', '05 - Industrial Injuries');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('368', '10', '00 - Interlocutory');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('369', '10', '07 - Vaccine Damage');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('371', '18', 'Upper - Administrative Appeals');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('372', '19', 'Upper - Immigration and Asylum');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('373', '12', 'First Tier - Immigration and Asylum');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('374', '11', 'First Tier - Health, Education and Social Care');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('375', '16', 'First Tier - Property');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('376', '10', 'First Tier - Social Entitlement');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('377', '14', 'First Tier - Tax');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('378', '15', 'First Tier - War Pensions and Armed Forces Compensation');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('379', '32', 'Employment Appeal Tribunal');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('380', '31', 'Employment Tribunal (Scotland)');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('381', '33', 'Others - Gender Recognition Panel');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('382', '43', 'Others - Pathogens Access Appeals Commission');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('383', '46', 'Others - Proscribed Organisations Appeal Commission');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('384', '52', 'Others - Special Immigration Appeals Commission');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('385', '20', 'Upper - Lands');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('386', '21', 'Upper - Tax and Chancery');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('387', '58', 'Adult Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('388', '58', 'Youth Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('389', '58', 'Family Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('392', '13', 'First Tier - General Regulatory');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('393', '24', 'Section 9(1) Kings Bench - Admin ONLY');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('394', '25', 'Pool of Judges');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('395', '24', 'Freezing Orders in the County Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('396', '24', 'IPEC');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('397', '24', 'S9(4) Appointment - Chancery');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('398', '24', 'S9(4) Appointment - Kings Bench');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('400', '26', 'S9(4) Appointment - Family');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('401', '25', 'Murder - Deputy Circuit Judge Only');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('402', '26', 'Ancillary Relief Appeals');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('403', '24', 'District Judge in the County Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('404', '24', 'Ordinary Planning');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('405', '58', 'Direct Recruitment to Family');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('406', '24', 'London Circuit Commercial Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('407', '24', 'Circuit Commercial Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('408', '24', 'Significant Planning');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('409', '24', 'Financial Remedy');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('410', '26', 'Financial Remedy');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('411', '24', 'Super Planning');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('412', '24', 'Media and Communications List');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1412', '24', 'Election Rota');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1413', '30', 'Employment Tribunal (England & Wales)');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1415', '24', 'Civil Authorisation (High Court Judge Chancery Division)');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1416', '10', '06 - Industrial injuries 2');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1417', '24', 'Financial List');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1450', '58', 'Adult Crime');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1451', '58', 'Adult Crime Presiding Justice');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1452', '58', 'Family Winger');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1453', '58', 'Family Presiding Justice');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1454', '58', 'Youth Winger');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1455', '58', 'Youth Presiding Justice');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('289', '59', 'Welsh Language');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('290', '24', 'Administrative Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('291', '24', 'Admiralty - KBD');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('292', '24', 'Chancery');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('293', '24', 'Chancery Business in the County Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('294', '24', 'Civil Authorisation');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('295', '24', 'Commercial');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('296', '24', 'Companies Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('298', '24', 'Mercantile - KBD');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('299', '24', 'Patents');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('300', '24', 'Section 9(1) Chancery');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('301', '24', 'Section 9(1) Kings Bench');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('302', '24', 'Technology and Construction Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('303', '25', 'Appeals in Crown Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('304', '25', 'Attempted Murder');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('305', '25', 'Central Criminal Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('306', '25', 'Criminal Authorisation');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('307', '25', 'Court of Appeal Criminal Division');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('308', '25', 'Extradition');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('309', '25', 'Murder');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('310', '25', 'Terrorism');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('311', '25', 'Serious Sexual Offences');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('312', '25', 'Serious Sexual Offences - Youth Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('313', '26', 'Court of Protection');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('314', '26', 'Financial Remedy Appeals');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('315', '26', 'Private Law');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('316', '26', 'Public Law');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('317', '26', 'Section 9-1 Family');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('318', '16', 'Agricultural Land and Drainage');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('319', '16', 'Agricultural Lands Tribunal Wales');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('320', '10', 'Asylum Support');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('321', '11', 'Care Standards');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('322', '13', 'Charity');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('323', '13', 'Claims Management Services');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('324', '16', 'Community Rights to Bid');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('325', '103', 'Competition Appeal Tribunal');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('326', '13', 'Consumer Credit');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('327', '105', 'Copyright Tribunal');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('328', '10', 'Criminal Injuries Compensations');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('329', '106', 'Design Tribunal');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('330', '14', 'Direct and Indirect Taxation');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('331', '13', 'Environment');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('332', '13', 'Estate Agents');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('333', '13', 'Food');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('334', '13', 'Gambling');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('335', '13', 'Immigration Services');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('336', '13', 'Information Rights');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('337', '21', 'Judicial Review - England and Wales');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('338', '21', 'Judicial Review - Northern Ireland');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('339', '21', 'Judicial Review - Scotland');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('340', '16', 'Land Registration');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('341', '13', 'Local Government Standards - England');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('342', '11', 'Mental Health');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('343', '11', 'Mental Health Tribunal Wales');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('344', '42', 'Motor Insurers Bureau');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('345', '14', 'MP''s Expenses');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('346', '13', 'National Security');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('347', '13', 'Pensions Regulations');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('348', '13', 'Professional Regulations');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('349', '11', 'Primary Health List');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('350', '45', 'Police Appeals Tribunal');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('351', '13', 'Race Panel');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('352', '47', 'Reinstatement Committee');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('353', '15', 'Reserve Forces');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('354', '16', 'Residential Property');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('355', '49', 'Residential Property Tribunal Wales');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('356', '11', 'Restricted Patients Panel');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('357', '10', 'Social Security and Child Support');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('358', '11', 'Special Educational Needs and Disability');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('359', '54', 'Trademark Tribunals');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('360', '55', 'Transport');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('361', '56', 'Valuation Tribunal England');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('362', '10', '02 - Child Support');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('364', '10', '01 - Social Security');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('365', '10', '03 - Disability Living Allowance');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('366', '10', '04 - Incapacity Benefit Employment Support');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('367', '10', '05 - Industrial Injuries');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('368', '10', '00 - Interlocutory');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('369', '10', '07 - Vaccine Damage');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('371', '18', 'Upper - Administrative Appeals');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('372', '19', 'Upper - Immigration and Asylum');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('373', '12', 'First Tier - Immigration and Asylum');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('374', '11', 'First Tier - Health, Education and Social Care');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('375', '16', 'First Tier - Property');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('376', '10', 'First Tier - Social Entitlement');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('377', '14', 'First Tier - Tax');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('378', '15', 'First Tier - War Pensions and Armed Forces Compensation');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('379', '32', 'Employment Appeal Tribunal');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('380', '31', 'Employment Tribunal (Scotland)');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('381', '33', 'Others - Gender Recognition Panel');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('382', '43', 'Others - Pathogens Access Appeals Commission');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('383', '46', 'Others - Proscribed Organisations Appeal Commission');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('384', '52', 'Others - Special Immigration Appeals Commission');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('385', '20', 'Upper - Lands');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('386', '21', 'Upper - Tax and Chancery');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('387', '58', 'Adult Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('388', '58', 'Youth Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('389', '58', 'Family Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('392', '13', 'First Tier - General Regulatory');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('393', '24', 'Section 9(1) Kings Bench - Admin ONLY');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('394', '25', 'Pool of Judges');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('395', '24', 'Freezing Orders in the County Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('396', '24', 'IPEC');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('397', '24', 'S9(4) Appointment - Chancery');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('398', '24', 'S9(4) Appointment - Kings Bench');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('400', '26', 'S9(4) Appointment - Family');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('401', '25', 'Murder - Deputy Circuit Judge Only');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('402', '26', 'Ancillary Relief Appeals');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('403', '24', 'District Judge in the County Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('404', '24', 'Ordinary Planning');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('405', '58', 'Direct Recruitment to Family');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('406', '24', 'London Circuit Commercial Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('407', '24', 'Circuit Commercial Court');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('408', '24', 'Significant Planning');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('409', '24', 'Financial Remedy');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('410', '26', 'Financial Remedy');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('411', '24', 'Super Planning');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('412', '24', 'Media and Communications List');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1412', '24', 'Election Rota');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1413', '30', 'Employment Tribunal (England & Wales)');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1415', '24', 'Civil Authorisation (High Court Judge Chancery Division)');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1416', '10', '06 - Industrial injuries 2');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1417', '24', 'Financial List');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1450', '58', 'Adult Crime');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1451', '58', 'Adult Crime Presiding Justice');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1452', '58', 'Family Winger');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1453', '58', 'Family Presiding Justice');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1454', '58', 'Youth Winger');
INSERT INTO dbjudicialdata.judicial_ticket_code_type (ticket_code, ticket_category_id, lower_level) VALUES('1455', '58', 'Youth Presiding Justice');


