ALTER TABLE user MODIFY role ENUM('USER','ADMIN','TRAINER') NOT NULL;

ALTER TABLE learning_lesson ADD additional_column_int INT;