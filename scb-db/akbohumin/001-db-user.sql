#mysql -u root -p

DROP DATABASE IF EXISTS akbohumin;
CREATE DATABASE akbohumin CHARACTER SET utf8;
USE akbohumin;

DROP USER IF EXISTS 'akbohumin'@'localhost';
CREATE USER 'akbohumin'@'localhost' IDENTIFIED BY 'akbohumin';
GRANT ALL PRIVILEGES ON * . * TO 'akbohumin'@'localhost';

FLUSH PRIVILEGES;