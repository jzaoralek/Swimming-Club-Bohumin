ALTER TABLE payment MODIFY description VARCHAR(100) CHARACTER SET utf8;
ALTER TABLE payment MODIFY process_type ENUM('AUTOMATIC','MANUAL','PAIRED') NOT NULL;