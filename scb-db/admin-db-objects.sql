DROP TABLE IF EXISTS customer_config;

CREATE TABLE customer_config(
	uuid varchar(36),
	cust_id VARCHAR(100) NOT NULL UNIQUE, -- odpovida context path
	db_url VARCHAR(100) NOT NULL,
    db_user VARCHAR(100) NOT NULL,
	db_password VARCHAR(100) NOT NULL,
    modif_at TIMESTAMP NOT NULL,
	modif_by varchar(36) NOT NULL,
	PRIMARY KEY (uuid)
);