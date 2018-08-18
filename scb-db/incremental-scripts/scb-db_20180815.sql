CREATE TABLE file (
	uuid varchar(36),
	name VARCHAR(240) CHARACTER SET utf8,
	description VARCHAR(1000) CHARACTER SET utf8,
	content LONGBLOB,
	content_type VARCHAR(128), 
	PRIMARY KEY (uuid)
);

CREATE TABLE course_application_file_config(
	uuid varchar(36),
	type ENUM('GDPR','HEALTH_INFO','HEALTH_EXAM', 'CLUB_RULES') NOT NULL UNIQUE,
	file_uuid varchar(36) REFERENCES file(uuid),
	page_text ENUM('0','1') NOT NULL,
	page_attachment ENUM('0','1') NOT NULL,
	email_attachment ENUM('0','1') NOT NULL,
	description VARCHAR(1000) CHARACTER SET utf8,
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

-- GDPR
INSERT INTO file (uuid, name, description, content, content_type) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499628', 'kosatky_SOUHLAS člena klubu.pdf', 'gdpr', null, 'application/file');
INSERT INTO course_application_file_config(uuid, type, file_uuid, page_attachment, email_attachment, description, modif_at, modif_by)
VALUES  ('fd33a4d4-7e99-11e6-ae22-56b6b6499629', 'GDPR', 'fd33a4d4-7e99-11e6-ae22-56b6b6499628', '0', '0', 'popis gdpr souboru', now(), 'SYSTEM');

-- HEALTH_INFO
INSERT INTO file (uuid, name, description, content, content_type) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499630', 'prvni-soubor.docx', 'health info', null, 'application/file');
INSERT INTO course_application_file_config(uuid, type, file_uuid, page_attachment, email_attachment, description, modif_at, modif_by)
VALUES  ('fd33a4d4-7e99-11e6-ae22-56b6b6499631', 'HEALTH_INFO', 'fd33a4d4-7e99-11e6-ae22-56b6b6499630', '0', '0', 'popis health info souboru', now(), 'SYSTEM');

-- HEALTH_EXAM
INSERT INTO file (uuid, name, description, content, content_type) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499632', 'kosatky_formular_zdravotni prohlidka.pdf', 'health exam', null, 'application/file');
INSERT INTO course_application_file_config(uuid, type, file_uuid, page_attachment, email_attachment, description, modif_at, modif_by)
VALUES  ('fd33a4d4-7e99-11e6-ae22-56b6b6499633', 'HEALTH_EXAM', 'fd33a4d4-7e99-11e6-ae22-56b6b6499632', '0', '0', 'popis health exam souboru', now(), 'SYSTEM');

-- CLUB_RULES
INSERT INTO file (uuid, name, description, content, content_type) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499634', 'kosatky_zásady-pro-přijetí-do-klubu.pdf', 'club rules', null, 'application/file');
INSERT INTO course_application_file_config(uuid, type, file_uuid, page_attachment, email_attachment, description, modif_at, modif_by)
VALUES  ('fd33a4d4-7e99-11e6-ae22-56b6b6499635', 'CLUB_RULES', 'fd33a4d4-7e99-11e6-ae22-56b6b6499634', '0', '0', 'popis club rules souboru', now(), 'SYSTEM');