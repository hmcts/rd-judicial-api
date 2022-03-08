--Alter judicial_user_profile
ALTER TABLE judicial_user_profile ADD COLUMN is_judge BOOLEAN;
ALTER TABLE judicial_user_profile ADD COLUMN is_panel_member BOOLEAN;
ALTER TABLE judicial_user_profile ADD COLUMN is_magistrate BOOLEAN;
ALTER TABLE judicial_user_profile ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE judicial_user_profile ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE judicial_user_profile ADD COLUMN mrd_deleted_time TIMESTAMP;

--Alter judicial_office_appointment
ALTER TABLE judicial_office_appointment ADD COLUMN primary_location VARCHAR(16);
ALTER TABLE judicial_office_appointment ADD COLUMN secondary_location VARCHAR(16);
ALTER TABLE judicial_office_appointment ADD COLUMN tertiary_location VARCHAR(16);
ALTER TABLE judicial_office_appointment ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE judicial_office_appointment ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE judicial_office_appointment ADD COLUMN mrd_deleted_time TIMESTAMP;

COMMIT;