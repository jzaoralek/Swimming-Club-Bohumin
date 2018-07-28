CREATE TABLE course_location (
	uuid varchar(36),
	name VARCHAR(240) CHARACTER SET utf8,
	description VARCHAR(1000) CHARACTER SET utf8,
	PRIMARY KEY (uuid)
);

ALTER TABLE course ADD course_location_uuid varchar(36) REFERENCES course_location(uuid);
ALTER TABLE course ADD max_participant_count INT;

ALTER TABLE configuration MODIFY name varchar(36) NOT NULL;

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499616', 'COURSE_APPL_SEL_REQ', 'Výběr kurzu v rámci přihlášky', 'true', 'BOOLEAN', now(), 'SYSTEM');

ALTER TABLE course MODIFY description VARCHAR(1000) CHARACTER SET utf8;
ALTER TABLE course_location MODIFY description VARCHAR(1000) CHARACTER SET utf8;

