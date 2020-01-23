CREATE INDEX elinks_Id_idx1 ON judicial_user_profile (elinks_Id);

CREATE INDEX base_location_Id_idx1 ON base_location_type (base_location_Id);

CREATE INDEX region_Id_idx1 ON region_type (region_Id);

CREATE INDEX contract_type_Id_idx1 ON contract_type (contract_type_Id);

CREATE INDEX role_id_idx1 ON judicial_role_type (role_id);

CREATE INDEX judicial_office_appointment_Id_idx ON judicial_office_appointment (judicial_office_appointment_Id);

CREATE INDEX elinks_Id_idx2 ON judicial_office_appointment (elinks_Id);
CREATE INDEX role_id_idx2 ON judicial_office_appointment (role_id);
CREATE INDEX contract_type_Id_idx2 ON judicial_office_appointment (contract_type_Id);
CREATE INDEX base_location_Id_idx2 ON judicial_office_appointment (base_location_Id);
CREATE INDEX region_Id_idx2 ON judicial_office_appointment (region_Id);

CREATE INDEX authorisation_Id_idx1 ON authorisation_type (authorisation_Id);

CREATE INDEX judicial_office_auth_Id_idx ON judicial_office_authorisation (judicial_office_auth_Id);

CREATE INDEX elinks_Id_idx3 ON judicial_office_authorisation (elinks_Id);
CREATE INDEX authorisation_Id_idx2 ON judicial_office_authorisation (authorisation_Id);

