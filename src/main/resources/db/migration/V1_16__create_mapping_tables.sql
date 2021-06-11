create table judicial_service_code_mapping(
        service_code varchar(16),
        jurisdiction varchar(256),
        lower_level varchar(256),
        service_description varchar(512),
        CONSTRAINT service_code UNIQUE (service_code)
);

create table judicial_location_mapping(
        epimms_id varchar(16),
        judicial_base_location_id varchar(64),
        building_location_name varchar(256),
        base_location_name varchar(128),
        CONSTRAINT epimms_id UNIQUE (epimms_id)
);

insert into judicial_service_code_mapping(
       service_code,
       jurisdiction,
       lower_level,
       service_description)
values(
        'BFA1',
        'Authorisation Tribunals',
        'First Tier - Immigration and Asylum',
        'Immigration and Asylum Appeals')
;
