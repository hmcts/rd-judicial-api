INSERT INTO dbjudicialdata.judicial_user_profile (personal_code,known_as,surname,full_name,post_nominals,ejudiciary_email,active_flag,created_date,last_loaded_date,object_id,sidam_id,initials)
VALUES ('40','Eight4Kb','Eight4Sb','Eight4Fb','Mr','Eight539@test.net','TRUE','2021-07-14 12:25:28.763','2021-07-14 12:25:28.763','5f8msdba-0rtb-4192-b5c7-rttd737f0ytt','1.11112E+12','S.K'),
('41','Eight4Kb','Eight4Sb','Eight4Fb','Mr','Eight540@test.net','TRUE','2021-07-14 12:25:28.763','2021-07-14 12:25:28.763','5f8msdba-0rtb-4192-b5c7-rttd737f0ytt','1.11112E+12','S.K');

INSERT INTO dbjudicialdata.location_type (base_location_id,"name",type_id,parent_id,jurisdiction_id,start_date,end_date,created_at,updated_at) values  ('1038','Immigration Service','42','1503','27',NULL,NULL,'2023-05-02 11:43:53','2023-05-02 11:43:53');
INSERT INTO dbjudicialdata.location_type (base_location_id,"name",type_id,parent_id,jurisdiction_id,start_date,end_date,created_at,updated_at) values  ('1039','Immigration Service','42','1503','27',NULL,NULL,'2023-05-02 11:43:53','2023-05-02 11:43:53');


INSERT INTO dbjudicialdata.judicial_office_appointment (personal_code,base_location_id,hmcts_region_id,is_prinicple_appointment,start_date,end_date,created_date,last_loaded_date,epimms_id,appointment,appointment_type,appointment_id,role_name_id,"type",contract_type_id,"location",jo_base_location_id) VALUES
 ('40','1038','1',true,'1995-03-27', NULL,'2023-08-03 13:28:10.188406','2023-08-03 13:28:10.188432',NULL,'Magistrate','Voluntary','310','1','LJA','2','South East','1038'),
 ('41','1039','1',true,'1995-03-27', NULL,'2023-08-03 13:28:10.188406','2023-08-03 13:28:10.188432',NULL,'Magistrate','Voluntary','300','1','LJA','2','South East','1039');


 INSERT INTO dbjudicialdata.judicial_office_authorisation
 (judicial_office_auth_id, jurisdiction, start_date, end_date, created_date,
 last_updated, lower_level, personal_code, ticket_code,authorisation_id,jurisdiction_id,appointment_id)
 VALUES
 (1038, 'Authorisation Magistrate', '2002-09-09 00:00:00.000',  NULL,
  '2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '40', '372',8,2,'300'),
 (1039, 'Authorisation Magistrate', '2002-09-09 00:00:00.000',  NULL,
    '2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '41', '372',8,2,NULL);