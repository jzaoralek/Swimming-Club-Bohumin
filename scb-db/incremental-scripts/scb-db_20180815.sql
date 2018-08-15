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
	type ENUM('GDPR','HEALTH_INFO','HEALTH_EXAM', 'CLUB_RULES') NOT NULL,
	file_uuid varchar(36) REFERENCES file(uuid),
	page_text VARCHAR(1000) CHARACTER SET utf8,
	page_attachment ENUM('0','1') NOT NULL,
	email_attachment ENUM('0','1') NOT NULL,
	description VARCHAR(1000) CHARACTER SET utf8,
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

INSERT INTO file (uuid, name, description, content, content_type) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499628', 'prvni-soubor.docx', 'prvni soubor', LOAD_FILE('Users/jakub.zaoralek/Downloads/gdpr-souhlas.docx'), 'application/file');

INSERT INTO course_application_file_config(uuid, type, file_uuid, page_attachment, email_attachment, description, modif_at, modif_by)
VALUES  ('fd33a4d4-7e99-11e6-ae22-56b6b6499629', 'GDPR', 'fd33a4d4-7e99-11e6-ae22-56b6b6499628', '1', '1', 'popis gdpr souboru', now(), 'SYSTEM');

application/file