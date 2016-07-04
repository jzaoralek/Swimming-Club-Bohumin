DROP TABLE contact;
DROP TABLE course_participant;
DROP TABLE user;
DROP TABLE course_application;
DROP TABLE result;
DROP TABLE course;
DROP TABLE course_course_participant;
DROP TABLE lesson;

CREATE TABLE contact(
	uuid char(16),
	firstname VARCHAR(100) NOT NULL,
	surname VARCHAR(100) NOT NULL,
	street VARCHAR(240),
	land_registry_number SMALLINT,
	city VARCHAR(240),
	zip_code SMALLINT,
	email1 VARCHAR(100),
	email2 VARCHAR(100),
	phone1 VARCHAR(14),
	phone2 VARCHAR(14),
	PRIMARY KEY (uuid)
);

CREATE TABLE course_participant(
	uuid char(16),
	contact_uuid char(16) REFERENCES contact(uuid),
	PRIMARY KEY (uuid)
);

CREATE TABLE user(
	uuid char(16),
	firstname VARCHAR(100) NOT NULL,
	password VARCHAR(100) NOT NULL,
	password_generated ENUM('0','1') NOT NULL,
	role ENUM('USER','ADMIN') NOT NULL,
	contact_uuid char(16) REFERENCES contact(uuid),
	PRIMARY KEY (uuid)
);

CREATE TABLE course_application(
	uuid char(16),
	year_from YEAR NOT NULL,
	year_to YEAR NOT NULL,
	course_participant_uuid char(16) REFERENCES course_participant(uuid),
	user_uuid char(16) REFERENCES user(uuid),
	PRIMARY KEY (uuid)
);

CREATE TABLE result(
	uuid char(16),
	result_time time NOT NULL,
	result_date date NOT NULL,
	style ENUM('VOLNY_STYL','PRSA','ZNAK','MOTYL') NOT NULL,
	distance MEDIUMINT NOT NULL,
	course_participant_uuid char(16) REFERENCES course_participant(uuid),
	PRIMARY KEY (uuid)
);

CREATE TABLE course(
	uuid char(16),
	name VARCHAR(100) NOT NULL,
	description VARCHAR(240),
	year_from YEAR,
	year_to YEAR,
	PRIMARY KEY (uuid)
);

CREATE TABLE course_course_participant(
	uuid char(16),
	course_participant_uuid char(16) REFERENCES course_participant(uuid),
	course_uuid char(16) REFERENCES course(uuid),
	PRIMARY KEY (uuid)
);

CREATE TABLE lesson(
	uuid char(16),
	time_from time NOT NULL,
	time_to time NOT NULL,
	day_of_week ENUM('SUNDAY','MONDAY','TUESDAY','WENDSDAY','THURSDAY','FRIDAY','SATURDAY'),
	course_uuid char(16) REFERENCES course(uuid),
	PRIMARY KEY (uuid)
)