DROP TABLE learning_lesson;
DROP TABLE participant_learning_lesson;

CREATE TABLE learning_lesson (
	uuid varchar(36),
	lesson_date DATE NOT NULL,
	time_from time NOT NULL,
	time_to time NOT NULL,
	description varchar(240),
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








