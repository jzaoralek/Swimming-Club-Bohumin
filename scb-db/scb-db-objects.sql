DROP TABLE contact;
DROP TABLE course_participant;
DROP TABLE user;
DROP TABLE course_application;
DROP TABLE result;
DROP TABLE course;
DROP TABLE course_course_participant;
DROP TABLE lesson;

CREATE TABLE contact(
	uuid varchar(36),
	firstname VARCHAR(100) NOT NULL,
	surname VARCHAR(100) NOT NULL,
	street VARCHAR(240),
	land_registry_number INT,
  house_number SMALLINT,
	city VARCHAR(240),
	zip_code VARCHAR(32),
	email1 VARCHAR(100),
	email2 VARCHAR(100),
	phone1 VARCHAR(14),
	phone2 VARCHAR(14),
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE course_participant(
	uuid varchar(36),
  birthdate DATE,
  personal_number varchar(12),
	health_insurance varchar(240),
	health_info varchar(524),
	contact_uuid varchar(36) REFERENCES contact(uuid),
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE user(
	uuid varchar(36),
	username VARCHAR(100) NOT NULL,
	password VARCHAR(100) NOT NULL,
	password_generated ENUM('0','1') NOT NULL,
	role ENUM('USER','ADMIN') NOT NULL,
	contact_uuid varchar(36) REFERENCES contact(uuid),
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE course_application(
	uuid varchar(36),
	year_from YEAR NOT NULL,
	year_to YEAR NOT NULL,
	course_participant_uuid varchar(36) REFERENCES course_participant(uuid),
	user_uuid varchar(36) REFERENCES user(uuid),
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE result(
	uuid varchar(36),
	result_time time NOT NULL,
	result_date date NOT NULL,
	style ENUM('VOLNY_STYL','PRSA','ZNAK','MOTYL') NOT NULL,
	distance MEDIUMINT NOT NULL,
	course_participant_uuid varchar(36) REFERENCES course_participant(uuid),
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE course(
	uuid varchar(36),
	name VARCHAR(100) NOT NULL,
	description VARCHAR(240),
	year_from YEAR,
	year_to YEAR,
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE course_course_participant(
	uuid varchar(36),
	course_participant_uuid varchar(36) REFERENCES course_participant(uuid),
	course_uuid varchar(36) REFERENCES course(uuid),
	PRIMARY KEY (uuid)
);

CREATE TABLE lesson(
	uuid varchar(36),
	time_from time NOT NULL,
	time_to time NOT NULL,
	day_of_week ENUM('SUNDAY','MONDAY','TUESDAY','WENDSDAY','THURSDAY','FRIDAY','SATURDAY'),
	course_uuid varchar(36) REFERENCES course(uuid),
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
)
