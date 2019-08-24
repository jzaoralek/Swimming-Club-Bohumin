DROP TABLE IF EXISTS contact;
DROP TABLE IF EXISTS  course_participant;
DROP TABLE IF EXISTS  user;
DROP TABLE IF EXISTS  course_application;
DROP TABLE IF EXISTS  result;
DROP TABLE IF EXISTS  course;
DROP TABLE IF EXISTS  course_course_participant;
DROP TABLE IF EXISTS  lesson;
DROP TABLE IF EXISTS  configuration;
DROP TABLE IF EXISTS  codelist_item;
DROP TABLE IF EXISTS  learning_lesson;
DROP TABLE IF EXISTS  participant_learning_lesson;
DROP TABLE IF EXISTS  bank_transaction;
DROP TABLE IF EXISTS  payment;
DROP TABLE IF EXISTS  course_location;
DROP TABLE IF EXISTS  file;
DROP TABLE IF EXISTS  course_application_file_config;

CREATE TABLE contact(
	uuid varchar(36),
	firstname VARCHAR(100) NOT NULL,
	surname VARCHAR(100) NOT NULL,
    citizenship VARCHAR(3) NOT NULL DEFAULT 'CZE',
	sex_male ENUM('0','1') NOT NULL DEFAULT '1',
	street VARCHAR(240),
	land_registry_number INT,
    house_number VARCHAR(32),
	evidence_number VARCHAR(32),
	city VARCHAR(240),
    region VARCHAR(240),
	zip_code VARCHAR(32),
    foreign_address VARCHAR(1000) CHARACTER SET utf8,
    address_validation_status ENUM('VALID','INVALID','NOT_VERIFIED') NOT NULL DEFAULT 'NOT_VERIFIED',
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
	user_uuid varchar(36) REFERENCES user(uuid),
	contact_uuid varchar(36) REFERENCES contact(uuid),
    iscus_role ENUM('ACTIVE_SPORTSMAN','ACTIVE_SPORTSMAN_PROFESSIONAL','OTHER') NULL DEFAULT 'ACTIVE_SPORTSMAN',
    iscus_partic_id VARCHAR(32) NULL,
    iscus_system_id VARCHAR(32) NULL,
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE user(
	uuid varchar(36),
	username VARCHAR(100) NOT NULL UNIQUE,
	password VARCHAR(100) NOT NULL,
	password_generated ENUM('0','1') NOT NULL,
	role ENUM('USER','ADMIN','TRAINER') NOT NULL,
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
	payed ENUM('0','1') NOT NULL DEFAULT '0',
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE course(
	uuid varchar(36),
	name VARCHAR(100) CHARACTER SET utf8 NOT NULL,
	description VARCHAR(1000) CHARACTER SET utf8,
	year_from YEAR,
	year_to YEAR,
	price_semester_1 INT NOT NULL,
	price_semester_2 INT NOT NULL,
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	max_participant_count INT,
	course_location_uuid varchar(36) REFERENCES course_location(uuid),
	PRIMARY KEY (uuid)
);

CREATE TABLE course_course_participant(
	uuid varchar(36),
	course_participant_uuid varchar(36) REFERENCES course_participant(uuid),
	course_uuid varchar(36) REFERENCES course(uuid),
	VARSYMBOL_CORE INT NULL auto_increment UNIQUE,
	individual_price_semester_1 INT NULL,
	individual_price_semester_2 INT NULL,
    notified_semester_1_payment_at TIMESTAMP NULL,
    notified_semester_2_payment_at TIMESTAMP NULL,
    course_partic_interrupted_at TIMESTAMP NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE lesson(
	uuid varchar(36),
	time_from time NOT NULL,
	time_to time NOT NULL,
	day_of_week ENUM('SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'),
	course_uuid varchar(36) REFERENCES course(uuid),
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE configuration(
	uuid varchar(36),
	name varchar(36) NOT NULL,
	description VARCHAR(200) CHARACTER SET utf8,
	val VARCHAR(1000) CHARACTER SET utf8,
    type  ENUM('STRING','INTEGER','BOOLEAN','ENUM') NOT NULL,
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
    superadmin_config ENUM('0','1') NOT NULL DEFAULT '0',
	PRIMARY KEY (uuid)
);

CREATE TABLE codelist_item (
	uuid varchar(36),
	item_type ENUM('SWIMMING_STYLE') NOT NULL,
	name VARCHAR(100) NOT NULL,
	description VARCHAR(240) CHARACTER SET utf8,
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
	description varchar(240) CHARACTER SET utf8,
	course_participant_uuid varchar(36) REFERENCES course_participant(uuid),
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE learning_lesson (
	uuid varchar(36),
	lesson_date DATE NOT NULL,
	time_from time NOT NULL,
	time_to time NOT NULL,
	description varchar(1000) CHARACTER SET utf8,
	additional_column_int INT,
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	lesson_uuid varchar(36) REFERENCES lesson(uuid),
	PRIMARY KEY (uuid)
);

CREATE TABLE participant_learning_lesson(
	course_participant_uuid varchar(36) REFERENCES course_participant(uuid),
	learning_lesson_uuid varchar(36) REFERENCES learning_lesson(uuid),
	PRIMARY KEY (course_participant_uuid, learning_lesson_uuid)
);

CREATE TABLE payment(
	uuid varchar(36),
	amount INT,
	type ENUM('CASH','BANK_TRANS','DONATE','OTHER') NOT NULL,
	process_type ENUM('AUTOMATIC','MANUAL','PAIRED') NOT NULL,
	description VARCHAR(100) CHARACTER SET utf8,
	payment_date TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
	modif_at TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	course_participant_uuid varchar(36) REFERENCES course_participant(uuid),
	course_uuid varchar(36) REFERENCES course(uuid),
	bank_transaction_id_pohybu bigint,
	PRIMARY KEY (uuid)
);

CREATE TABLE bank_transaction (
	protiucet_cisloUctu VARCHAR(240),
	protiucet_kodBanky VARCHAR(240),
	protiucet_nazevBanky VARCHAR(240) CHARACTER SET utf8,
	protiucet_nazevUctu VARCHAR(240) CHARACTER SET utf8,
	datumPohybu DATE,
	objem DOUBLE,
	konstantniSymbol VARCHAR(240),
	variabilniSymbol VARCHAR(240),
	uzivatelskaIdentifikace VARCHAR(240) CHARACTER SET utf8,
	typ VARCHAR(100) CHARACTER SET utf8,
	mena VARCHAR(50),
	idPokynu BIGINT,
	idPohybu BIGINT,
	komentar VARCHAR(240) CHARACTER SET utf8,
	provedl VARCHAR(240) CHARACTER SET utf8,
	zpravaProPrijemnce VARCHAR(240) CHARACTER SET utf8,
	PRIMARY KEY (idPohybu)
);

CREATE TABLE course_location (
	uuid varchar(36),
	name VARCHAR(240) CHARACTER SET utf8,
	description VARCHAR(1000) CHARACTER SET utf8,
	PRIMARY KEY (uuid)
);

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