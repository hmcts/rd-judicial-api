INSERT INTO dbjudicialdata.judicial_service_code_mapping (service_id,ticket_code,service_code,service_description) VALUES
	 ((select service_id from dbjudicialdata.judicial_service_code_mapping order by service_id  desc limit 1)+1,'405','ABA5','Family Private Law');

COMMIT;

INSERT INTO dbjuddata.judicial_service_code_mapping (service_id,ticket_code,service_code,service_description) VALUES
	 ((select service_id from dbjuddata.judicial_service_code_mapping order by service_id  desc limit 1)+1,'405','ABA5','Family Private Law');

COMMIT;