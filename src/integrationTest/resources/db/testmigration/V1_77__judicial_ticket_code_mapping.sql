delete from judicial_ticket_code_mapping where ticket_code in ('380','1413');
INSERT INTO judicial_ticket_code_mapping (ticket_code,jurisdiction,lower_level)
VALUES ('380', 'Authorisation Tribunals', 'Others - Employment Tribunal (Scotland)');
INSERT INTO judicial_ticket_code_mapping (ticket_code,jurisdiction,lower_level)
VALUES ('1413', 'Authorisation Tribunals','Others - Employment Tribunal England & Wales');


INSERT INTO dbjudicialdata.jrd_lrd_region_mapping (jrd_region_id,jrd_region,region_id,region) VALUES
    ('71',	'Cambridge',          '5',	'South East');