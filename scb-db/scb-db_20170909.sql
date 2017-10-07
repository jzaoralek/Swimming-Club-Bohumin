ALTER TABLE course ADD price_semester_1 INT NOT NULL;
ALTER TABLE course ADD price_semester_2 INT NOT NULL;

CREATE TABLE payment(
	uuid varchar(36),
	amount INT,
	type ENUM('CASH','BANK_TRANS','DONATE','OTHER') NOT NULL,
	process_type ENUM('AUTOMATIC','MANUAL') NOT NULL,
	description VARCHAR(100),
	payment_date TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
	modif_at TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	course_participant_uuid varchar(36) REFERENCES course_participant(uuid),
	course_uuid varchar(36) REFERENCES course(uuid),
	PRIMARY KEY (uuid)
);