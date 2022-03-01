INSERT INTO judicial_user_profile
(per_id, personal_code, known_as, surname, full_name, post_nominals,
work_pattern, ejudiciary_email, joining_date,
last_working_date, active_flag, extracted_date, created_date,
last_loaded_date, object_id, sidam_id)
VALUES
('1001', '29', 'Refresh1KA', 'Refresh1SN', 'Refresh1FN', 'Ms', 'No Work Pattern (M to F)', 'test1001@test.net', '2017-03-06',
NULL, true, '2021-07-14 12:25:28.763', '2021-08-11 09:10:44.682', '2021-08-11 09:10:44.682',
'1111', '1111'),
('40399', '4923413', 'Joe', 'Bloggs', 'Joe Bloggs', 'Mr', 'Fee Paid Judiciary 5 Days Mon - Fri', 'EMP40399@ejudiciary.net', '2006-12-11',
NULL, true, '2021-07-14 12:25:28.763', '2021-08-11 09:10:44.682', '2021-08-11 09:10:44.682',
'd4774030-32cc-4b64-894f-d475b0b1129c', ''),
('40704', '4923818', 'Joe', 'Bloggs', 'Joe Bloggs', 'Mr', 'Fee Paid Judiciary 5 Days Mon - Fri', 'EMP40704@ejudiciary.net', '2006-01-01',
NULL, true, '2021-07-14 12:25:28.763', '2021-08-11 09:10:44.682', '2021-08-11 09:10:44.682',
'fcb4f03c-4b3f-4c3c-bf3a-662b4557b470', '');

INSERT INTO judicial_office_appointment
(judicial_office_appointment_id, per_id, base_location_id, region_id, is_prinicple_appointment,
start_date, end_date, active_flag, extracted_date, created_date, last_loaded_date, personal_code,
epimms_id, service_code, object_id, appointment, appointment_type)
VALUES
(1001, '1001', '1029', '1', true, '1995-03-27', NULL, true, '2021-07-14 12:25:26.330',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '27', '20014', 'BBA4', '1111', 'refreshTest', 'TestApptype'),
(1002, '40399', '1029', '1', true, '2006-12-11', NULL, true, '2021-07-14 12:25:26.330',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '4923413', '20014', 'BBA4', 'd4774030-32cc-4b64-894f-d475b0b1129c', 'Tribunal Judge', 'Fee Paid'),
(1003, '40704', '1029', '1', true, '2008-03-11', NULL, true, '2021-07-14',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '4923818', '20014', 'BBA4', 'fcb4f03c-4b3f-4c3c-bf3a-662b4557b470', 'Tribunal Judge', 'Fee Paid');


INSERT INTO judicial_office_authorisation
(judicial_office_auth_id, per_id, jurisdiction, ticket_id, start_date, end_date, created_date,
last_updated, lower_level, personal_code, ticket_code, object_id)
VALUES
(1001, '1001', 'Authorisation Magistrate', 25374, '2002-09-09', NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '28', '364', '1111'),
(1002, '40399', 'Authorisation Tribunals', 31000, '2006-12-11 00:00:00.000', NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'First Tier - Immigration and Asylum', '4923413', '364', 'd4774030-32cc-4b64-894f-d475b0b1129c'),
(1003, '40704', 'Authorisation Tribunals', 35970, '2018-09-10 00:00:00.000', '2020-02-14 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Criminal Injuries Compensations', '4923818', '364', 'fcb4f03c-4b3f-4c3c-bf3a-662b4557b470'),
(1004, '40704', 'Authorisation Tribunals', 25992, null, '2020-02-14 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'First Tier - Tax', '4923818', '364', 'fcb4f03c-4b3f-4c3c-bf3a-662b4557b470'),
(1005, '40704', 'Authorisation Tribunals', 26166, '2018-09-10 00:00:00.000', '2020-02-14 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'First Tier - Immigration and Asylum', '4923818', '364', 'fcb4f03c-4b3f-4c3c-bf3a-662b4557b470'),
(1006, '40704', 'Authorisation Tribunals', 20155, '1901-01-01 00:00:00.000', '2020-02-14 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', '04 - Incapacity Benefit Employment Support', '4923818', '364', 'fcb4f03c-4b3f-4c3c-bf3a-662b4557b470'),
(1007, '40704', 'Authorisation Tribunals', 18620, '1901-01-01 00:00:00.000', '2020-02-14 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', '01 - Social Security', '4923818', '364', 'fcb4f03c-4b3f-4c3c-bf3a-662b4557b470'),
(1008, '40704', 'Authorisation Tribunals', 19607, '1901-01-01 00:00:00.000', '2020-02-14 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', '03 - Disability Living Allowance', '4923818', '364', 'fcb4f03c-4b3f-4c3c-bf3a-662b4557b470'),
(1009, '40704', 'Authorisation Tribunals', 18254, null, '2020-02-14 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'First Tier - Social Entitlement', '4923818', '364', 'fcb4f03c-4b3f-4c3c-bf3a-662b4557b470'),
(1010, '40704', 'Authorisation Tribunals', 25645, null, '2019-03-31 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Asylum Support', '4923818', '364', 'fcb4f03c-4b3f-4c3c-bf3a-662b4557b470');

INSERT INTO judicial_role_type
(role_id, per_Id, title ,location, start_date, end_date)
values
(1,'1001','testTitle', 'testLocation', '2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054'),
(2,'1001','testTitle1', 'testLocation', '2021-08-11 09:14:30.054', current_timestamp + interval '30' day),
(3,'1001','testTitle2', 'testLocation', '2021-08-11 09:14:30.054', null);