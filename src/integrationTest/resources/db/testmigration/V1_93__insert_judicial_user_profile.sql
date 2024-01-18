INSERT INTO dbjudicialdata.judicial_user_profile (personal_code,known_as,surname,full_name,post_nominals,ejudiciary_email,active_flag,created_date,last_loaded_date,object_id,sidam_id,initials)
VALUES ('12345477','recc2Kb','recc2Sb','recc2Fb','Mr','recc531@test.net','TRUE','2021-07-14 12:25:28.763','2021-07-14 12:25:28.763','5f8b26ba-0c8b-4192-b5c7-311d73qwert','33333344','J.K');


INSERT INTO dbjudicialdata.judicial_office_appointment (personal_code,base_location_id,hmcts_region_id,is_prinicple_appointment,start_date,end_date,created_date,last_loaded_date,epimms_id,appointment,appointment_type,appointment_id,role_name_id,"type",contract_type_id,"location",jo_base_location_id) VALUES
 ('12345477','1032','1',true,'1995-03-27', NULL,'2023-08-03 13:28:10.188406','2023-08-03 13:28:10.188432',NULL,'Magistrate','Voluntary','477','1','LJA','2','South East','1032');

 INSERT INTO dbjudicialdata.judicial_office_authorisation
 (judicial_office_auth_id, jurisdiction, start_date, end_date, created_date,
 last_updated, lower_level, personal_code, ticket_code,authorisation_id,jurisdiction_id,appointment_id)
 VALUES(7899, 'Authorisation Magistrate', '2002-09-09 00:00:00.000', NULL,
 '2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '12345477', '368',7,1,'477');
