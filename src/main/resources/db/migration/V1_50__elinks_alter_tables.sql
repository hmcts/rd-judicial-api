--Create sequence for dbjudicialdata.judicial_office_authorisation
CREATE SEQUENCE dbjudicialdata.judicial_office_auth_id_sequence AS integer START 1 OWNED
BY dbjudicialdata.judicial_office_authorisation.judicial_office_auth_id;

ALTER TABLE dbjudicialdata.judicial_office_authorisation
ALTER COLUMN judicial_office_auth_id SET DEFAULT nextval('dbjudicialdata.judicial_office_auth_id_sequence');

--Create sequence for dbjudicialdata.judicial_office_appointment
CREATE SEQUENCE dbjudicialdata.judicial_office_appointment_id_sequence AS integer START 1 OWNED
BY dbjudicialdata.judicial_office_appointment.judicial_office_appointment_id;


ALTER TABLE dbjudicialdata.judicial_office_appointment
ALTER COLUMN judicial_office_appointment_id SET DEFAULT nextval('dbjudicialdata.judicial_office_appointment_id_sequence');

insert into dbjudicialdata.base_location_type(base_location_id,court_name,court_type,circuit,area_of_expertise)
values ('0', 'default', 'default','default', 'default');

insert into dbjudicialdata.region_type(region_id,region_desc_en,region_desc_cy)
values ('0', 'default', 'default');
