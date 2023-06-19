
UPDATE dbjudicialdata.judicial_office_authorisation SET appointment_id='179' where personal_code='27';


INSERT INTO dbjudicialdata.judicial_user_profile (personal_code,known_as,surname,full_name,post_nominals,ejudiciary_email,active_flag,created_date,last_loaded_date,object_id,sidam_id,initials)
VALUES ('31','Test2Kb','Test2Sb','Test2Fb','Mr','test531@test.net','TRUE','2021-07-14 12:25:28.763','2021-07-14 12:25:28.763','1.11112E+12','1.11112E+12','J.K');


INSERT INTO dbjudicialdata.judicial_office_appointment
(judicial_office_appointment_id, base_location_id, region_id, is_prinicple_appointment,
start_date, end_date, created_date, last_loaded_date, personal_code,
epimms_id, service_code, object_id, appointment, appointment_type,appointment_id)
VALUES(193, '1029', '1', true, '1995-03-27', '2023-03-27',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '31', NULL, 'AAA5', '1111122223333', 'TestApp', 'TestApptype','193'),
(194, '1029', '1', true, '1995-03-27', NULL,
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '31', NULL, 'ABA3', '1111122223333', 'TestApp', 'TestApptype','194');

INSERT INTO dbjudicialdata.judicial_office_authorisation
(judicial_office_auth_id, jurisdiction, start_date, end_date, created_date,
last_updated, lower_level, personal_code, ticket_code, object_id,authorisation_id,jurisdiction_id,appointment_id)
VALUES(1021, 'Authorisation Magistrate', '2002-09-09 00:00:00.000', NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '31', NULL, '1111122223333',7,1,'193'),
(1022, 'Authorisation Magistrate', '2002-09-09 00:00:00.000', NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '31', '373', '1111122223333',8,2,'194');

INSERT INTO dbjudicialdata.judicial_user_profile (personal_code,known_as,surname,full_name,post_nominals,ejudiciary_email,active_flag,created_date,last_loaded_date,object_id,sidam_id,initials)
VALUES ('32','One2Kb','One2Sb','One2Fb','Mr','One531@test.net','TRUE','2021-07-14 12:25:28.763','2021-07-14 12:25:28.763','1.11112E+12','1.11112E+12','J.K');
INSERT INTO dbjudicialdata.judicial_user_profile (personal_code,known_as,surname,full_name,post_nominals,ejudiciary_email,active_flag,created_date,last_loaded_date,object_id,sidam_id,initials)
VALUES ('33','Two2Kb','Two2Sb','Two2Fb','Mr','Two532@test.net','TRUE','2021-07-14 12:25:28.763','2021-07-14 12:25:28.763','1.11112E+12','1.11112E+12','O.K');
INSERT INTO dbjudicialdata.judicial_user_profile (personal_code,known_as,surname,full_name,post_nominals,ejudiciary_email,active_flag,created_date,last_loaded_date,object_id,sidam_id,initials)
VALUES ('34','Three2Kb','Three2Sb','Three2Fb','Mr','Three532@test.net','TRUE','2021-07-14 12:25:28.763','2021-07-14 12:25:28.763','1.11112E+12','1.11112E+12','T.K'),
('35','Four2Kb','Four2Sb','Four2Fb','Mr','Four534@test.net','TRUE','2021-07-14 12:25:28.763','2021-07-14 12:25:28.763','1.11112E+12','1.11112E+12','F.K'),
('36','Five2Kb','Five2Sb','Five2Fb','Mr','Five535@test.net','TRUE','2021-07-14 12:25:28.763','2021-07-14 12:25:28.763','1.11112E+12','1.11112E+12','F.K'),
('37','SIX2Kb','Six2Sb','Six2Fb','Mr','Six536@test.net','TRUE','2021-07-14 12:25:28.763','2021-07-14 12:25:28.763','1.11112E+12','1.11112E+12','F.K'),
('38','Seven2Kb','Seven2Sb','Seven2Fb','Mr','Seven537@test.net','TRUE','2021-07-14 12:25:28.763','2021-07-14 12:25:28.763','1.11112E+12','1.11112E+12','S.K');



INSERT INTO dbjudicialdata.judicial_office_appointment
(judicial_office_appointment_id, base_location_id, region_id, is_prinicple_appointment,
start_date, end_date, created_date, last_loaded_date, personal_code,
epimms_id, service_code, object_id, appointment, appointment_type,appointment_id)
VALUES(195, '1029', '1', true, '1995-03-27', NULL,
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '32', NULL, 'BBA3', '1111122223333', 'TestApp', 'TestApptype','195'),
(196, '1029', '1', true, '1995-03-27', NULL,
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '32', NULL, 'BFA1', '1111122223333', 'TestApp', 'TestApptype','196');

INSERT INTO dbjudicialdata.judicial_office_appointment
(judicial_office_appointment_id, base_location_id, region_id, is_prinicple_appointment,
start_date, end_date, created_date, last_loaded_date, personal_code,
epimms_id, service_code, object_id, appointment, appointment_type,appointment_id)
VALUES(197, '1029', '1', true, '1995-03-27', '2023-03-27',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '33', NULL, 'BBA3', '1111122223333', 'TestApp', 'TestApptype','197'),
(198, '1029', '1', true, '1995-03-27', '2022-03-27',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '33', NULL, 'BFA1', '1111122223333', 'TestApp', 'TestApptype','198'),
(199, '1029', '1', true, '1995-03-27', NULL,
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '34', NULL, 'BBA3', '1111122223333', 'TestApp', 'TestApptype','199'),
(200, '1029', '1', true, '1995-03-27', NULL,
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '34', NULL, 'BFA1', '1111122223333', 'TestApp', 'TestApptype','200'),
(201, '1029', '1', true, '1995-03-27', '2022-03-27',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '35', NULL, 'BBA3', '1111122223333', 'TestApp', 'TestApptype','201'),
(202, '1029', '1', true, '1995-03-27', '2022-03-27',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '35', NULL, 'BFA1', '1111122223333', 'TestApp', 'TestApptype','202'),
(203, '1029', '1', true, '1995-03-27',  NULL,
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '36', NULL, 'BBA3', '1111122223333', 'TestApp', 'TestApptype','203'),
(204, '1029', '1', true, '1995-03-27',  NULL,
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '36', NULL, 'BFA1', '1111122223333', 'TestApp', 'TestApptype','204'),
(205, '1029', '1', true, '1995-03-27', '2022-03-27',
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '37', NULL, 'BBA3', '1111122223333', 'TestApp', 'TestApptype','205'),
(206, '1029', '1', true, '1995-03-27',  NULL,
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '37', NULL, 'BFA1', '1111122223333', 'TestApp', 'TestApptype','206'),
(207, '1029', '1', true, '1995-03-27', NULL,
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '38', NULL, 'ABA5', '1111122223333', 'TestApp', 'TestApptype','207'),
(208, '1029', '1', true, '1995-03-27',  NULL,
'2021-08-11 09:12:40.134', '2021-08-11 09:12:40.134', '38', NULL, 'ABA3', '1111122223333', 'TestApp', 'TestApptype','208');



INSERT INTO dbjudicialdata.judicial_office_authorisation
(judicial_office_auth_id, jurisdiction, start_date, end_date, created_date,
last_updated, lower_level, personal_code, ticket_code, object_id,authorisation_id,jurisdiction_id,appointment_id)
VALUES(1023, 'Authorisation Magistrate', '2002-09-09 00:00:00.000', NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '32', NULL, '1111122223333',7,1,'195'),
(1024, 'Authorisation Magistrate', '2002-09-09 00:00:00.000', NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '32', '373', '1111122223333',8,2,'196');

INSERT INTO dbjudicialdata.judicial_office_authorisation
(judicial_office_auth_id, jurisdiction, start_date, end_date, created_date,
last_updated, lower_level, personal_code, ticket_code, object_id,authorisation_id,jurisdiction_id,appointment_id)
VALUES(1025, 'Authorisation Magistrate', '2002-09-09 00:00:00.000', '2021-09-09 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '33', NULL, '1111122223333',7,1,'197'),
(1026, 'Authorisation Magistrate', '2002-09-09 00:00:00.000', '2022-09-09 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '33', '373', '1111122223333',8,2,'198'),
(1027, 'Authorisation Magistrate', '2002-09-09 00:00:00.000', '2022-09-09 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '34', '373', '1111122223333',8,2,'199'),
(1028, 'Authorisation Magistrate', '2002-09-09 00:00:00.000', '2022-09-09 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '34', '373', '1111122223333',8,2,'200'),
(1029, 'Authorisation Magistrate', '2002-09-09 00:00:00.000',  NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '35', '373', '1111122223333',8,2,'201'),
(1030, 'Authorisation Magistrate', '2002-09-09 00:00:00.000',  NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '35', '373', '1111122223333',8,2,'202'),
(1031, 'Authorisation Magistrate', '2002-09-09 00:00:00.000',  NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '36', '368', '1111122223333',8,2,'203'),
(1032, 'Authorisation Magistrate', '2002-09-09 00:00:00.000',  '2021-09-09 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '36', '373', '1111122223333',8,2,'204'),
(1033, 'Authorisation Magistrate', '2002-09-09 00:00:00.000',  NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '37', '368', '1111122223333',8,2,'205'),
(1034, 'Authorisation Magistrate', '2002-09-09 00:00:00.000',  NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '37', '373', '1111122223333',8,2,'206'),
(1035, 'Authorisation Magistrate', '2002-09-09 00:00:00.000',  '2021-09-09 00:00:00.000',
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '38', '400', '1111122223333',8,2,'207'),
(1036, 'Authorisation Magistrate', '2002-09-09 00:00:00.000',  NULL,
'2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '38', '400', '1111122223333',8,2,'208');

