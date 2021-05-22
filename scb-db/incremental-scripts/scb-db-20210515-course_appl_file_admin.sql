ALTER TABLE course_application_file_config MODIFY type ENUM('GDPR','HEALTH_INFO','HEALTH_EXAM', 'CLUB_RULES', 'OTHER') NOT NULL;
ALTER TABLE course_application_file_config DROP INDEX type;