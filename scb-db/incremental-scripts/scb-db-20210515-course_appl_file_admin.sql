ALTER TABLE course_application_file_config MODIFY type ENUM('GDPR','HEALTH_INFO','HEALTH_EXAM', 'CLUB_RULES', 'OTHER') NOT NULL;
ALTER TABLE course_application_file_config DROP INDEX type;

UPDATE course_application_file_config SET description = 'GDPR dokument' WHERE type = 'GDPR';
UPDATE course_application_file_config SET description = 'Zdravotní způsobilost' WHERE type = 'HEALTH_INFO';
UPDATE course_application_file_config SET description = 'Zdravotní prohlídka' WHERE type = 'HEALTH_EXAM';
UPDATE course_application_file_config SET description = 'Pravidla klubu' WHERE type = 'CLUB_RULES';

