create table judicial_service_code_mapping(
        service_code varchar(16),
        jurisdiction varchar(256),
        lower_level varchar(256),
        service_description varchar(512),
        CONSTRAINT service_code UNIQUE (service_code)
);