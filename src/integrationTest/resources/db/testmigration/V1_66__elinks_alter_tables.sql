--Alter dbjudicialdata.judicial_user_profile
ALTER TABLE dbjudicialdata.judicial_user_profile ADD COLUMN deleted_flag boolean;
ALTER TABLE dbjudicialdata.judicial_user_profile ADD COLUMN date_of_deletion TIMESTAMP;

