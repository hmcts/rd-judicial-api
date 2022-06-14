ALTER TABLE judicial_user_profile ADD COLUMN is_judge BOOLEAN;
ALTER TABLE judicial_user_profile ADD COLUMN is_panel_member BOOLEAN;
ALTER TABLE judicial_user_profile ADD COLUMN is_magistrate BOOLEAN;
ALTER TABLE judicial_user_profile ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE judicial_user_profile ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE judicial_user_profile ADD COLUMN mrd_deleted_time TIMESTAMP;


INSERT INTO judicial_user_profile
(per_id, personal_code, known_as, surname, full_name, post_nominals,
work_pattern, ejudiciary_email, joining_date,
last_working_date, active_flag, extracted_date, created_date,
last_loaded_date, object_id, sidam_id, is_judge, is_panel_member, is_magistrate)
VALUES('900', '300', 'Sample_KA_1', 'Sample_SN_1', 'Sample_FN_1', 'Ms', 'No Work Pattern (M to F)', 'test900@test.net', '2022-03-01', NULL, true,
'2022-03-01 12:25:28.763', '2022-03-01 09:10:44.682', '2022-03-01 09:10:44.682',
'900', '900', true, false, true),

('901', '301', 'Sample_KA_2', 'Sample_SN_2', 'Sample_FN_2', 'Ms', 'No Work Pattern (M to F)', 'test901@test.net', '2022-03-02',
NULL, true, '2022-03-02 12:25:28.763', '2022-03-02 09:10:44.682', '2022-03-02 09:10:44.682',
'901', '901', false, true, true),

('902', '302', 'Sample_KA_3', 'Sample_SN_3', 'Sample_FN_3', 'Ms', 'No Work Pattern (M to F)', 'test902@test.net', '2022-03-03',
NULL, true, '2022-03-03 12:25:28.763', '2022-03-03 09:10:44.682', '2022-03-03 09:10:44.682',
'902', '902', true, true, false),

('903', '303', 'Sample_KA_4', 'Sample_SN_4', 'Sample_FN_4', 'Ms', 'No Work Pattern (M to F)', 'test903@test.net', '2022-03-04',
NULL, true, '2022-03-04 12:25:28.763', '2022-03-04 09:10:44.682', '2022-03-04 09:10:44.682',
'903', '903', false, false, false);