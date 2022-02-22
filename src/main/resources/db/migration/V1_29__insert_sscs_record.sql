DELETE FROM judicial_service_code_mapping WHERE service_id ='21' OR service_id ='55';
DELETE FROM judicial_ticket_code_mapping WHERE ticket_code ='363' OR ticket_code ='376';
INSERT INTO judicial_location_mapping (judicial_base_location_id,base_location_name,epimms_id,building_location_name, service_code) VALUES ('1032','Social Entitlement','','','BBA3');
COMMIT;
