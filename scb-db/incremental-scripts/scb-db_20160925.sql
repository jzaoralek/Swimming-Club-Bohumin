DROP TABLE result;

CREATE TABLE codelist_item (
	uuid varchar(36),
	item_type ENUM('SWIMMING_STYLE') NOT NULL,
	name VARCHAR(100) NOT NULL,
	description VARCHAR(240),
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE result(
	uuid varchar(36),
	result_time LONG NOT NULL,
	result_date date NOT NULL,
	style_uuid varchar(36) REFERENCES codelist_item(uuid),
	distance MEDIUMINT NOT NULL,
	description varchar(240),
	course_participant_uuid varchar(36) REFERENCES course_participant(uuid),
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) 
VALUES ('82bb2300-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Prsa', '',  now(), 'SYSTEM');
INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) 
VALUES ('82bb2648-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Volný styl', '',  now(), 'SYSTEM');
INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) 
VALUES ('82bb27ec-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Motýlek', '',  now(), 'SYSTEM');
INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) 
VALUES ('82bb29f4-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Znak', '',  now(), 'SYSTEM');

select * from result


