CREATE TABLE mail_message_send (
	uuid varchar(36),
	mail_to VARCHAR(1000) NOT NULL,
    mail_to_complete_name VARCHAR(1000) NULL,
	mail_cc VARCHAR(1000) NULL,
	mail_subject VARCHAR(1000) CHARACTER SET utf8,
	mail_text LONGTEXT CHARACTER SET utf8,
	success ENUM('0','1') NOT NULL DEFAULT '1',
    description TEXT NULL,
	attachments ENUM('0','1') NOT NULL DEFAULT '0',
	html ENUM('0','1') NOT NULL DEFAULT '0',
	modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);
