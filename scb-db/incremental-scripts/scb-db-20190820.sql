ALTER TABLE course_participant ADD iscus_role ENUM('ACTIVE_SPORTSMAN','ACTIVE_SPORTSMAN_PROFESSIONAL','OTHER') NULL DEFAULT 'ACTIVE_SPORTSMAN';
ALTER TABLE course_participant ADD iscus_partic_id VARCHAR(32) NULL;
ALTER TABLE course_participant ADD iscus_system_id VARCHAR(32) NULL;

UPDATE course_participant SET iscus_role='ACTIVE_SPORTSMAN';