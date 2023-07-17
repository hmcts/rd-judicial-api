ALTER TABLE judicial_user_profile ALTER COLUMN extracted_date TYPE date USING extracted_date::date;
ALTER TABLE judicial_office_appointment ALTER COLUMN extracted_date TYPE date USING extracted_date::date;



--Alter dbjudicialdata.judicial_user_profile
ALTER TABLE dbjudicialdata.judicial_user_profile ADD COLUMN deleted_flag boolean;
ALTER TABLE dbjudicialdata.judicial_user_profile ADD COLUMN date_of_deletion TIMESTAMP;

