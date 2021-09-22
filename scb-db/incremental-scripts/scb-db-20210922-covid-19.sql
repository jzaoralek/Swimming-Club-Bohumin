CREATE TABLE covid19_confirmation (
	uuid varchar(36),
	type ENUM('VACCINATION', 'TEST_ANTIGEN', 'TEST_PCR', 'TEST_SELF') NOT NULL,
	file LONGBLOB,
	description VARCHAR(1000) CHARACTER SET utf8mb4,
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	course_participant_uuid varchar(36) REFERENCES course_participant(uuid),
	PRIMARY KEY (uuid)
);

CREATE TABLE covid19_config (
	uuid varchar(36),
	enabled ENUM('0','1'),
	vaccination_renew_interval SMALLINT,
	test_antigen_renew_interval SMALLINT,
	test_pcr_renew_interval SMALLINT,
	test_self_renew_interval SMALLINT,
	modif_at TIMESTAMP  NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);