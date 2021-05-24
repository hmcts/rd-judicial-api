ALTER TABLE judicial_user_profile DROP CONSTRAINT elinks_Id;
ALTER TABLE judicial_user_profile RENAME COLUMN elinks_Id TO per_Id;
ALTER TABLE judicial_user_profile ADD PRIMARY KEY (per_Id);
ALTER TABLE judicial_user_profile RENAME COLUMN contract_type TO appointment_type;
ALTER TABLE judicial_user_profile RENAME COLUMN email_Id TO ejudiciary_email;
ALTER TABLE judicial_user_profile RENAME COLUMN title TO appointment;

ALTER TABLE judicial_office_appointment RENAME COLUMN elinks_Id TO per_Id;
ALTER TABLE judicial_office_appointment ADD COLUMN personal_code varchar(32);
ALTER TABLE judicial_office_appointment DROP CONSTRAINT role_id_fk1;
ALTER TABLE judicial_office_appointment DROP CONSTRAINT contract_type_Id_fk1;
ALTER TABLE judicial_office_appointment DROP COLUMN role_id;
ALTER TABLE judicial_office_appointment DROP COLUMN contract_type_Id;

ALTER TABLE judicial_office_authorisation RENAME COLUMN elinks_Id TO per_Id;
ALTER TABLE judicial_office_authorisation ADD COLUMN personal_code varchar(32);

DROP table judicial_role_type;
DROP table contract_type;
