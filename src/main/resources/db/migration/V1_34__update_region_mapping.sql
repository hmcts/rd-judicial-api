truncate table  jrd_lrd_region_mapping  cascade;
INSERT INTO jrd_lrd_region_mapping (jrd_region_id,jrd_region,region_id,region) VALUES
('1','National','12','National'),
('2','National England and Wales','12','National'),
('3','Taylor House (London')',1','London'),
('4','Hatton Cross (London')',1','London'),
('5','Newport (Wales')',7','Wales'),
('6','Glasgow (Scotland and NI')',11','Scotland'),
('7','Birmingham','2','Midlands'),
(',8','North Shields','3','North East'),
('9','Stoke','2','Midlands'),
('10','Manchester','4','North West'),
('11','Bradford','3','North East'),
('12','Nottingham','2','Midlands'),
('13','Field House (London)','1','London'),
('14','London','1','London'),'
('15','London Central','1','London'),
('16','London East','1','London'),
('17','London South','1','London'),
('18','South East','5','South East'),
('19','South Eastern','5','South East'),
('20','Midlands','2','Midlands'),
('21','Midlands East','2','Midlands'),
('22','Midlands West','2','Midlands'),
('23','South West','6','South West'),
('24','South Western','6','South West'),
('25','North West','4','North West'),
('26','North East','3','North East'),
('27','Wales','7','Wales'),
('28','Scotland','11','Scotland'),
('32','Yorkshire and Humberside','3','North East'),
('33','Newcastle','3','North East'),
('35','EAT - Rolls Building','1','London');
COMMIT;
