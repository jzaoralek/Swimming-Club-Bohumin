ALTER TABLE course ADD price_semester_1 INT NOT NULL;
ALTER TABLE course ADD price_semester_2 INT NOT NULL;

CREATE TABLE payment(
	uuid varchar(36),
	amount INT,
	type ENUM('CASH','BANK_TRANS','DONATE','OTHER') NOT NULL,
	description VARCHAR(100),
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	course_course_participant_uuid varchar(36) REFERENCES course_course_participant(uuid),
	PRIMARY KEY (uuid)
);