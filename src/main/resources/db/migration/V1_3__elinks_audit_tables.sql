-- Drop table

-- DROP TABLE dbjudicialdata.judicial_office_authorisation_audit;

CREATE TABLE dbjudicialdata.judicial_office_authorisation_audit (
judicial_office_auth_id int8 NOT NULL,
personal_code varchar(32) NOT NULL,
jurisdiction varchar(256) NOT NULL,
start_date date NULL,
end_date date NULL,
created_date timestamp NULL,
last_updated timestamp NULL,
lower_level varchar(256) NOT NULL,
ticket_code varchar(16) NOT NULL,
appointment_id varchar(256) NULL,
authorisation_id varchar(256) NOT NULL,
jurisdiction_id varchar(64) NOT NULL
);

-- Drop table

-- DROP TABLE dbjudicialdata.judicial_office_appointment_audit;

CREATE TABLE dbjudicialdata.judicial_office_appointment_audit (
judicial_office_appointment_id int8 NOT NULL,
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
jo_base_location_id varchar(64) NOT NULL
);

-- Drop table

-- DROP TABLE dbjudicialdata.judicial_additional_roles;

CREATE TABLE dbjudicialdata.judicial_additional_roles_audit (
role_id int4 NOT NULL,
personal_code varchar(32) NOT NULL,
title varchar(256) NOT NULL,
start_date timestamp NULL,
end_date timestamp NULL,
jurisdiction_role_id varchar(64) NOT NULL,
jurisdiction_role_name_id varchar(64) NOT NULL
);


-- Drop table

-- DROP TABLE dbjudicialdata.judicial_user_profile_audit;

CREATE TABLE dbjudicialdata.judicial_user_profile_audit (
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
  date_of_deletion timestamp NULL
);