INSERT INTO judicial_user_profile
(per_id, personal_code, appointment, known_as, surname, full_name, post_nominals, appointment_type, work_pattern, ejudiciary_email, joining_date, last_working_date, active_flag, extracted_date, created_date, last_loaded_date, object_id, sidam_id)
VALUES('37395', '4913940', 'Circuit Judge', 'Joe', 'Bloggs', 'Joe Bloggs', 'His Honour Judge', 'Salaried', 'Salaried Judiciary 5 Days Mon - Fri', 'EMP37395@ejudiciary.net', '2006-08-10', NULL, true, '2021-07-14 12:25:28.763', '2021-07-21 12:40:47.235', '2021-07-21 12:40:47.235', '26ddf2fc-41c2-4893-b7f9-33cf46cc436b', NULL);
INSERT INTO judicial_user_profile
(per_id, personal_code, appointment, known_as, surname, full_name, post_nominals, appointment_type, work_pattern, ejudiciary_email, joining_date, last_working_date, active_flag, extracted_date, created_date, last_loaded_date, object_id, sidam_id)
VALUES('11049', '39157', 'Magistrate', 'Joe', 'Bloggs', 'Joe Bloggs', 'Mr', 'Voluntary', 'No Work Pattern (M to F)', 'EMP11049@ejudiciary.net', '2005-06-02', NULL, true, '2021-07-14 12:25:28.763', '2021-07-21 12:40:47.235', '2021-07-21 12:40:47.235', '631253a5-9ba8-4f6b-aa7d-7cb20a5abb45', NULL);
INSERT INTO judicial_user_profile
(per_id, personal_code, appointment, known_as, surname, full_name, post_nominals, appointment_type, work_pattern, ejudiciary_email, joining_date, last_working_date, active_flag, extracted_date, created_date, last_loaded_date, object_id, sidam_id)
VALUES('62506', '49927031', 'Magistrate', 'Joe', 'Bloggs', 'Joe Bloggs', 'Miss', 'Voluntary', 'No Work Pattern (M to F)', 'EMP62506@ejudiciary.net', '2019-02-13', NULL, true, '2021-07-14 12:25:28.763', '2021-07-21 12:40:47.235', '2021-07-21 12:40:47.235', '86749371-d9da-4e2b-851f-c09f4a035bb9', NULL);



INSERT INTO base_location_type
(base_location_id, court_name, court_type, circuit, area_of_expertise)
VALUES('1371', 'East Kent LJA', 'Kent AC', 'South East', 'Advisory Committee');
INSERT INTO base_location_type
(base_location_id, court_name, court_type, circuit, area_of_expertise)
VALUES('1380', 'Buckinghamshire LJA ', 'Thames Valley AC', 'South East', 'Advisory Committee');
INSERT INTO base_location_type
(base_location_id, court_name, court_type, circuit, area_of_expertise)
VALUES('760', 'Wood Green Crown Court', 'Crown Court', 'London', 'Courts');


INSERT INTO region_type
(region_id, region_desc_en, region_desc_cy)
VALUES('1', 'National', NULL);
INSERT INTO region_type
(region_id, region_desc_en, region_desc_cy)
VALUES('2', 'default', 'default');



select * from judicial_office_appointment;
INSERT INTO judicial_office_appointment
(judicial_office_appointment_id, per_id, base_location_id, region_id, is_prinicple_appointment, start_date, end_date, active_flag, extracted_date, created_date, last_loaded_date, personal_code)
VALUES(16, '37395', '760', '0', true, '2018-02-09', NULL, true, '2021-07-14 12:25:26.330', '2021-07-21 12:42:47.549', '2021-07-21 12:42:47.549', '4913940');
INSERT INTO judicial_office_appointment
(judicial_office_appointment_id, per_id, base_location_id, region_id, is_prinicple_appointment, start_date, end_date, active_flag, extracted_date, created_date, last_loaded_date, personal_code)
VALUES(20116, '11049', '1371', '1', true, '2005-06-02', NULL, true, '2021-07-14 12:25:26.330', '2021-07-21 12:42:47.549', '2021-07-21 12:42:47.549', '39157');
INSERT INTO judicial_office_appointment
(judicial_office_appointment_id, per_id, base_location_id, region_id, is_prinicple_appointment, start_date, end_date, active_flag, extracted_date, created_date, last_loaded_date, personal_code)
VALUES(20339, '62506', '1380', '1', true, '2019-02-13', NULL, true, '2021-07-14 12:25:26.330', '2021-07-21 12:42:47.549', '2021-07-21 12:42:47.549', '49927031');


INSERT INTO judicial_office_authorisation
(judicial_office_auth_id, per_id, jurisdiction, ticket_id, start_date, end_date, created_date, last_updated, lower_level, personal_code, service_code)
VALUES(4117, '11049', 'Authorisation Magistrate', 13751, '2005-10-02 00:00:00.000', NULL, '2021-07-21 12:44:39.894', '2021-07-21 12:44:39.894', 'Adult Court', '39157', 'BFA1');
INSERT INTO judicial_office_authorisation
(judicial_office_auth_id, per_id, jurisdiction, ticket_id, start_date, end_date, created_date, last_updated, lower_level, personal_code, service_code)
VALUES(7845, '37395', 'Authorisation Crime', 23520, NULL, NULL, '2021-07-21 12:44:39.894', '2021-07-21 12:44:39.894', 'Serious Sexual Offences', '4913940', 'ABA1');
INSERT INTO judicial_office_authorisation
(judicial_office_auth_id, per_id, jurisdiction, ticket_id, start_date, end_date, created_date, last_updated, lower_level, personal_code, service_code)
VALUES(7846, '37395', 'Authorisation Crime', 31327, '2018-08-07 00:00:00.000', NULL, '2021-07-21 12:44:39.894', '2021-07-21 12:44:39.894', 'Attempted Murder', '4913940', 'ABA1');
INSERT INTO judicial_office_authorisation
(judicial_office_auth_id, per_id, jurisdiction, ticket_id, start_date, end_date, created_date, last_updated, lower_level, personal_code, service_code)
VALUES(8024, '37395', 'Authorisation Crime', 47517, '2020-08-10 00:00:00.000', NULL, '2021-07-21 12:44:39.894', '2021-07-21 12:44:39.894', 'Murder', '4913940', 'BFA1');
INSERT INTO judicial_office_authorisation
(judicial_office_auth_id, per_id, jurisdiction, ticket_id, start_date, end_date, created_date, last_updated, lower_level, personal_code, service_code)
VALUES(10621, '37395', 'Authorisation Crime', 23518, NULL, NULL, '2021-07-21 12:44:39.894', '2021-07-21 12:44:39.894', 'Criminal Authorisation', '4913940', 'BFA2');
INSERT INTO judicial_office_authorisation
(judicial_office_auth_id, per_id, jurisdiction, ticket_id, start_date, end_date, created_date, last_updated, lower_level, personal_code, service_code)
VALUES(15777, '37395', 'Authorisation Crime', 23519, NULL, NULL, '2021-07-21 12:44:39.894', '2021-07-21 12:44:39.894', 'Appeals in Crown Court', '4913940', 'BFA2');

