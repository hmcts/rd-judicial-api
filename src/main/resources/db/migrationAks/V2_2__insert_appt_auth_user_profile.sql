
INSERT INTO region_type
(region_id, region_desc_en, region_desc_cy)
VALUES('1', 'National', NULL);

INSERT INTO base_location_type
(base_location_id, court_name, court_type, circuit, area_of_expertise)
VALUES('1029', 'Aberconwy', 'Old Gwynedd', 'Gwynedd', 'LJA');

INSERT INTO judicial_user_profile
(per_id, personal_code, appointment, known_as, surname, full_name, post_nominals, appointment_type,
work_pattern, ejudiciary_email, joining_date,
last_working_date, active_flag, extracted_date, created_date,
last_loaded_date, object_id, sidam_id)
VALUES('528', '27', 'Magistrate', 'Test2KA', 'Test2SN', 'Test2FN', 'Ms',
'Voluntary', 'No Work Pattern (M to F)', 'EMP528@ejudiciary.net', '2017-03-06', NULL, true,
'2021-07-14 12:25:28.763', '2021-08-11 09:10:44.682', '2021-08-11 09:10:44.682',
'1111122223333', '1111122223333'),
('529', '28', 'Magistrate', 'TestKA', 'TestSN', 'TestFN', 'Ms',
'Voluntary', 'No Work Pattern (M to F)', 'EMP529@ejudiciary.net', '2017-03-06',
NULL, true, '2021-07-14 12:25:28.763', '2021-08-11 09:10:44.682', '2021-08-11 09:10:44.682',
'1111122223333', '1111122223334'),
('530', '29', 'Magistrate', 'Test1KA', 'Test1SN', 'Test1FN', 'Ms',
'Voluntary', 'No Work Pattern (M to F)', 'EMP530@ejudiciary.net', '2017-03-06',
NULL, true, '2021-07-14 12:25:28.763', '2021-08-11 09:10:44.682', '2021-08-11 09:10:44.682',
'1111122223333', '1111122223334');

INSERT INTO judicial_office_appointment
(judicial_office_appointment_id, per_id, base_location_id, region_id, is_prinicple_appointment,
start_date, end_date, active_flag, extracted_date, created_date, last_loaded_date, personal_code,
epimms_id, service_code, object_id)
VALUES(179, '528', '1029', '1', true, '1995-03-27', NULL, true, '2021-07-14 12:25:26.330',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '27', NULL, 'BFA1', '1111122223333'),
(180, '528', '1029', '1', true, '1995-03-27', NULL, true, '2021-07-14 12:25:26.330',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '27', NULL, 'BFA2', '1111122223333'),
(181, '529', '1029', '1', true, '1995-03-27', NULL, true, '2021-07-14 12:25:26.330',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '28', NULL, NULL, '1111122223333'),
(182, '530', '1029', '1', true, '1995-03-27', NULL, true, '2021-07-14 12:25:26.330',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '29', '20012', 'BFA1', '1111122223333'),
(183, '528', '1029', '1', true, '1995-03-27', NULL, true, '2021-07-14 12:25:26.330',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '27', '20013', 'BFA2', '1111122223333');

INSERT INTO judicial_office_authorisation
(judicial_office_auth_id, per_id, jurisdiction, ticket_id, start_date, end_date, created_date,
last_updated, lower_level, personal_code, service_code, object_id)
VALUES(7, '528', 'Authorisation Magistrate', 25374, '2002-09-09 00:00:00.000', NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '27', NULL, '1111122223333'),
(8, '528', 'Authorisation Magistrate', 25374, '2002-09-09 00:00:00.000', NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '27', 'BFA1', '1111122223333'),
(9, '529', 'Authorisation Magistrate', 25374, '2002-09-09 00:00:00.000', NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '28', NULL, '1111122223333');