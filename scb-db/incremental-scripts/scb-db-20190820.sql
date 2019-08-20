ALTER TABLE course_participant ADD iscus_role ENUM('ACTIVE_SPORTSMAN','ACTIVE_SPORTSMAN_PROFESSIONAL','OTHER') NULL;
ALTER TABLE course_participant ADD iscus_partic_id VARCHAR(32) NULL;

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499627', 'ISCUS_SYSTEM_ID', 'ID systému pro ISCUS', 'sportologic.cz', 'STRING', now(), 'SYSTEM');
