#mysql -u root -p

DROP DATABASE IF EXISTS kosatky;
CREATE DATABASE kosatky CHARACTER SET utf8;
USE kosatky;

DROP USER IF EXISTS 'kosatky'@'localhost';
CREATE USER 'kosatky'@'localhost' IDENTIFIED BY 'kosatky';
GRANT ALL PRIVILEGES ON * . * TO 'kosatky'@'localhost';

FLUSH PRIVILEGES;