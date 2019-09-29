CREATE TABLE user_trainer_course (
	course_uuid varchar(36) REFERENCES course(uuid),
	user_trainer_uuid varchar(36) REFERENCES user(uuid),
	PRIMARY KEY (course_uuid, user_trainer_uuid)
);