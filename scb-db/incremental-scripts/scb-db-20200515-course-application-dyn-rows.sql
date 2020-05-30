CREATE TABLE course_application_dyn_attribute_config (
	uuid varchar(36),
	name VARCHAR(100) NOT NULL,
	description VARCHAR(1000) CHARACTER SET utf8,
	required ENUM('0','1') NOT NULL DEFAULT '1',
    type ENUM('TEXT', 'DATE', 'INT', 'DOUBLE',  'BOOLEAN') NOT NULL DEFAULT 'TEXT',
	created_at TIMESTAMP NOT NULL,
    terminated_at TIMESTAMP NULL,
    modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE course_application_dyn_attribute (
	uuid varchar(36),
	course_participant_uuid varchar(36) REFERENCES course_participant(uuid),
	course_appl_dyn_attr_config_uuid varchar(36) REFERENCES course_application_dyn_attribute_config(uuid),
	text_value VARCHAR(240),
	date_value TIMESTAMP NULL,
	int_value BIGINT,
	double_value DOUBLE,
	boolean_value ENUM('0','1'),
	modif_at TIMESTAMP NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);