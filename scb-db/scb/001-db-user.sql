DROP DATBASE scb;
CREATE DATABASE scb;
USE DATABASE scb;
	
DROP USER 'scb'@'localhost';
CREATE USER 'scb'@'localhost' IDENTIFIED BY 'scb';
GRANT ALL PRIVILEGES ON * . * TO 'scb'@'localhost';

FLUSH PRIVILEGES;