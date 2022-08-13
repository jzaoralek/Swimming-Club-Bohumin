DROP TABLE IF EXISTS customer_config;

CREATE TABLE customer_config(
	uuid varchar(36),
	cust_id VARCHAR(100) NOT NULL UNIQUE, -- odpovida context path
    cust_name VARCHAR(240) NOT NULL,
    cust_default ENUM('0','1') NOT NULL DEFAULT '0',
	db_url VARCHAR(100) NOT NULL,
    db_user VARCHAR(100) NOT NULL,
	db_password VARCHAR(100) NOT NULL,
    modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);