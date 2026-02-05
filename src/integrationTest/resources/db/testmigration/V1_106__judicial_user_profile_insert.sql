INSERT INTO dbjudicialdata.judicial_user_profile
(personal_code, known_as, surname, full_name, post_nominals, ejudiciary_email, last_working_date, active_flag, created_date, last_loaded_date, object_id, sidam_id, initials, title, retirement_date, deleted_flag, date_of_deletion,last_updated)
VALUES('66920248', 'John', 'Chung', 'District Judge John Chung', '', 'EmploymentJudge.JohnChung@ejudiciary.net', NULL, true, '2023-11-17 19:07:06.351', '2026-02-04 11:18:58.185', 'b9f66c45-dba5-4f71-be81-d6bb3692211e', '006245c8-fccf-4265-80a2-6e5c573f662c', 'JC', 'Mr', '2031-07-29', NULL, NULL,'2026-02-04 11:18:58.185');

INSERT INTO dbjudicialdata.judicial_office_appointment(personal_code,base_location_id,hmcts_region_id,is_prinicple_appointment,start_date,end_date,created_date,last_loaded_date,epimms_id,appointment,appointment_type,appointment_id,role_name_id,"type",contract_type_id,"location",jo_base_location_id)
 VALUES('66920248','1030','1',true,'2010-03-23', '2023-03-08','2023-08-03 13:28:10.188406','2023-08-03 13:28:10.188432',NULL,'Magistrate','Voluntary','998','90004','LJA','5','South East','1032');

INSERT INTO dbjudicialdata.judicial_office_appointment(personal_code,base_location_id,hmcts_region_id,is_prinicple_appointment,start_date,end_date,created_date,last_loaded_date,epimms_id,appointment,appointment_type,appointment_id,role_name_id,"type",contract_type_id,"location",jo_base_location_id) VALUES
('66920248','1036','1',false,'2007-05-11', '2014-06-20','2023-08-03 13:28:10.188406','2023-08-03 13:28:10.188432',NULL,'Magistrate','Tribunals','999','90005','LJA','1','South East','1032');


INSERT INTO dbjudicialdata.judicial_office_authorisation
 (judicial_office_auth_id, jurisdiction, start_date, end_date, created_date,
 last_updated, lower_level, personal_code, ticket_code,authorisation_id,jurisdiction_id,appointment_id)
 VALUES(66899, 'Authorisation Magistrate', '2002-09-09 00:00:00.000', '2022-07-23 00:00:00.000',
 '2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '66920248', '313',7,1,'998');

INSERT INTO dbjudicialdata.judicial_office_authorisation
  (judicial_office_auth_id, jurisdiction, start_date, end_date, created_date,
  last_updated, lower_level, personal_code, ticket_code,authorisation_id,jurisdiction_id,appointment_id)
  VALUES(76899, 'Authorisation Magistrate', '2002-09-09 00:00:00.000', '2014-06-20 00:00:00.000',
  '2021-08-11 09:14:30.054', '2021-08-11 09:14:30.054', 'Family Court', '66920248', '368',7,27,'999');

INSERT INTO dbjudicialdata.judicial_additional_roles
(role_id, personal_code, title, start_date, end_date, jurisdiction_role_id, jurisdiction_role_name_id)
VALUES(13003, '66920248', 'District Judge', '2015-04-01 00:00:00.000', '2019-03-31 00:00:00.000', '638', '90003');


