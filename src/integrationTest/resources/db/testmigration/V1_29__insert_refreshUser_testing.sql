INSERT INTO judicial_user_profile
(per_id, personal_code, known_as, surname, full_name, post_nominals,
work_pattern, ejudiciary_email, joining_date,
last_working_date, active_flag, extracted_date, created_date,
last_loaded_date, object_id, sidam_id)
VALUES
('1001', '29', 'Refresh1KA', 'Refresh1SN', 'Refresh1FN', 'Ms', 'No Work Pattern (M to F)', 'test1001@test.net', '2017-03-06',
NULL, true, '2021-07-14 12:25:28.763', '2021-08-11 09:10:44.682', '2021-08-11 09:10:44.682',
'1111', '1111');

INSERT INTO judicial_office_appointment
(judicial_office_appointment_id, per_id, base_location_id, region_id, is_prinicple_appointment,
start_date, end_date, active_flag, extracted_date, created_date, last_loaded_date, personal_code,
epimms_id, service_code, object_id, appointment, appointment_type)
VALUES
(1001, '1001', '1029', '1', true, '1995-03-27', NULL, true, '2021-07-14 12:25:26.330',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '27', '20014', 'BBA4', '1111', 'refreshTest', 'TestApptype');


INSERT INTO judicial_office_authorisation
(judicial_office_auth_id, per_id, jurisdiction, ticket_id, start_date, end_date, created_date,
last_updated, lower_level, personal_code, ticket_code, object_id)
VALUES
(1001, '1001', 'Authorisation Magistrate', 25374, '2002-09-09 00:00:00.000', NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '28', '364', '1111');

INSERT INTO judicial_role_type
(role_id, per_Id, title ,location, start_date, end_date)
values
(1,'1001','testTitle', 'testLocation', '2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054');