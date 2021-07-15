---- new table created : jrd_lrd_region_mapping
CREATE TABLE jrd_lrd_region_mapping(
    jrd_region_id VARCHAR(16) NOT NULL,
    jrd_region VARCHAR(256) NOT NULL,
    region_id VARCHAR(16) NOT NULL,
    region VARCHAR(256) NOT NULL
);

---- inserts for table : jrd_lrd_region_mapping
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('1', 'National', '1', 'National');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('2', 'National England and Wales', '1', 'National');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('3', 'Taylor House (London)', '2', 'London');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('4', 'Hatton Cross (London)', '2', 'London');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('5', 'Newport (Wales)', '8', 'Wales');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('6', 'Glasgow (Scotland and NI)', '9', 'Scotland');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('7', 'Birmingham', '3', 'Midlands');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('8', 'North Shields', '4', 'North East');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('9', 'Stoke', '3', 'Midlands');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('10', 'Manchester', '5', 'North west');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('11', 'Bradford', '4', 'North East');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('12', 'Nottingham', '3', 'Midlands');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('13', 'Field House (London)', '2', 'London');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('14', 'London', '2', 'London');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('15', 'London Central', '2', 'London');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('16', 'London East', '2', 'London');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('17', 'London South', '2', 'London');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('18', 'South East', '6', 'South east');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('19', 'South Eastern', '6', 'South east');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('20', 'Midlands', '3', 'Midlands');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('21', 'Midlands East', '3', 'Midlands');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('22', 'Midlands West', '3', 'Midlands');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('23', 'South West', '7', 'South west');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('24', 'South Western', '7', 'South west');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('25', 'North West', '5', 'North west');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('26', 'North East', '4', 'North East');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('27', 'Wales', '8', 'Wales');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('28', 'Scotland', '9', 'Scotland');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('32', 'Yorkshire and Humberside', '4', 'North East');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('33', 'Newcastle', '4', 'North East');
INSERT INTO jrd_lrd_region_mapping
(jrd_region_id, jrd_region, region_id, region)
VALUES('35', 'EAT - Rolls Building', '2', 'London');
