-- NEJPRVE NAROVNAT DATA !!!

ALTER TABLE course_participant ADD user_uuid varchar(36) REFERENCES user(uuid);

UPDATE course_participant cp SET cp.user_uuid = (SELECT ca.user_uuid from course_application ca where cp.uuid = ca.course_participant_uuid);

ALTER TABLE user ADD UNIQUE (username);