-- NB Flyway requires lowercase for table names
create schema if not exists dbjuddata;

CREATE TABLE judicial_user_profile(
	per_Id varchar(256) NOT NULL,
	personal_code varchar(32) NOT NULL,
	appointment varchar(64) NOT NULL,
	known_as varchar(64) NOT NULL,
	surname varchar(256) NOT NULL,
	full_name varchar(256) NOT NULL,
	post_nominals varchar(64),
	appointment_type varchar(32),
	work_pattern varchar(64) NOT NULL,
	ejudiciary_email varchar(256),
	joining_date date,
	last_working_date date,
	active_flag boolean,
	extracted_date timestamp NOT NULL,
	created_date timestamp,
	last_loaded_date timestamp,
	CONSTRAINT personal_code_unique UNIQUE (personal_code),
	CONSTRAINT per_id PRIMARY KEY (per_id)

);

CREATE TABLE judicial_office_appointment(
	judicial_office_appointment_id bigint NOT NULL,
	per_Id varchar(256) NOT NULL,
	personal_code varchar(32),
	base_location_id varchar(256),
	region_id varchar(256),
	is_prinicple_appointment boolean,
	start_date date,
	end_date date,
	active_flag boolean,
	extracted_date timestamp NOT NULL,
	created_date timestamp,
	last_loaded_date timestamp,
	CONSTRAINT judicial_office_appointment_id PRIMARY KEY (judicial_office_appointment_id)
);

CREATE TABLE judicial_office_authorisation(
    judicial_office_auth_id bigint NOT NULL,
    per_id varchar(256) NOT NULL,
    personal_code varchar(32),
    jurisdiction varchar(256),
    ticket_id bigint,
    start_date timestamp,
	end_date timestamp,
	created_date timestamp,
    last_updated timestamp,
    lower_level varchar(256),
	CONSTRAINT jud_auth_pk PRIMARY KEY (judicial_office_auth_id)
);


CREATE TABLE base_location_type(
	base_location_id varchar(64) NOT NULL,
	court_name varchar(128),
	court_type varchar(128),
	circuit varchar(128),
	area_of_expertise varchar(128),
	CONSTRAINT base_location_id PRIMARY KEY (base_location_id)

);

CREATE TABLE region_type(
	region_id varchar(64) NOT NULL,
	region_desc_en varchar(256) NOT NULL,
	region_desc_cy varchar(256),
	CONSTRAINT region_id PRIMARY KEY (region_id)

);

ALTER TABLE judicial_office_appointment ADD CONSTRAINT per_id_fk_1 FOREIGN KEY (per_id)
REFERENCES judicial_user_profile (per_id);

ALTER TABLE judicial_office_appointment ADD CONSTRAINT base_location_Id_fk FOREIGN KEY (base_location_Id)
REFERENCES base_location_type (base_location_Id);

ALTER TABLE judicial_office_appointment ADD CONSTRAINT region_Id_fk FOREIGN KEY (region_Id)
REFERENCES region_type (region_Id);

ALTER TABLE judicial_office_authorisation ADD CONSTRAINT per_id_fk_2 FOREIGN KEY (per_id)
REFERENCES judicial_user_profile (per_id);


