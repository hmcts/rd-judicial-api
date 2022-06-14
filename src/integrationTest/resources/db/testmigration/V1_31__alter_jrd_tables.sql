--Alter judicial_office_authorisation
ALTER TABLE judicial_office_authorisation ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE judicial_office_authorisation ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE judicial_office_authorisation ADD COLUMN mrd_deleted_time TIMESTAMP;
--Alter judicial_role_type
ALTER TABLE  judicial_role_type ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE  judicial_role_type ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE  judicial_role_type ADD COLUMN mrd_deleted_time TIMESTAMP;
--Alter judicial_location_mapping
ALTER TABLE judicial_location_mapping ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE judicial_location_mapping ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE judicial_location_mapping ADD COLUMN mrd_deleted_time TIMESTAMP;
--Alter judicial_service_code_mapping
ALTER TABLE judicial_service_code_mapping ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE judicial_service_code_mapping ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE judicial_service_code_mapping ADD COLUMN mrd_deleted_time TIMESTAMP;
--Alter judicial_ticket_code_mapping
ALTER TABLE judicial_ticket_code_mapping ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE judicial_ticket_code_mapping ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE judicial_ticket_code_mapping ADD COLUMN mrd_deleted_time TIMESTAMP;
--Alter base_location_type
ALTER TABLE base_location_type ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE base_location_type ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE base_location_type ADD COLUMN mrd_deleted_time TIMESTAMP;
--Alter region_type
ALTER TABLE region_type ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE region_type ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE region_type ADD COLUMN mrd_deleted_time TIMESTAMP;
--Alter jrd_lrd_region_mapping
ALTER TABLE jrd_lrd_region_mapping ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE jrd_lrd_region_mapping ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE jrd_lrd_region_mapping ADD COLUMN mrd_deleted_time TIMESTAMP;
--Alter judicial_office_appointment
ALTER TABLE judicial_office_appointment ADD COLUMN primary_location VARCHAR(16);
ALTER TABLE judicial_office_appointment ADD COLUMN secondary_location VARCHAR(16);
ALTER TABLE judicial_office_appointment ADD COLUMN tertiary_location VARCHAR(16);
ALTER TABLE judicial_office_appointment ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE judicial_office_appointment ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE judicial_office_appointment ADD COLUMN mrd_deleted_time TIMESTAMP;

INSERT INTO judicial_user_profile
(per_id, personal_code, known_as, surname, full_name, post_nominals,
work_pattern, ejudiciary_email, joining_date,
last_working_date, active_flag, extracted_date, created_date,
last_loaded_date, object_id, sidam_id, is_judge, is_panel_member, is_magistrate)
VALUES('9001', 'A123', 'Sample_KA_1', 'Sample_SN_1', 'Sample_FN_1', 'Ms', 'No Work Pattern (M to F)', 'test900@test.net', '2022-03-01', NULL, true,
'2022-03-01 12:25:28.763', '2022-03-01 09:10:44.682', '2022-03-01 09:10:44.682',
'900', '900', true, false, true);

INSERT INTO judicial_office_appointment
(judicial_office_appointment_id, per_id, base_location_id, region_id, is_prinicple_appointment,
start_date, end_date, active_flag, extracted_date, created_date, last_loaded_date, personal_code,
epimms_id, service_code, object_id, appointment, appointment_type,primary_location,secondary_location,tertiary_location)
VALUES
(9001, '9001', '1029', '1', true, '1995-03-27', NULL, true, '2021-07-14 12:25:26.330',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '27', '20014', 'BBA4', '1111', 'refreshTest', 'TestApptype','Nottingham','Queens Road',NULL);

COMMIT;