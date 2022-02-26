DELIMITER $$
DROP PROCEDURE IF EXISTS `create_db_user`;
CREATE DEFINER=`root`@`localhost` PROCEDURE `create_db_user`(IN  `dbName_in` VARCHAR(25), IN  `usrName_in` VARCHAR(75), IN  `usrPassword_in` VARCHAR(25))
BEGIN
	DECLARE SQLStmtDb TEXT;
	DECLARE SQLStmtUsr TEXT;
    DECLARE SQLStmtPriv TEXT;
    
    SET @SQLStmtDb = CONCAT('CREATE DATABASE ', dbName_in, ' CHARACTER SET utf8');
	PREPARE StmtDb FROM @SQLStmtDb;
	EXECUTE StmtDb;
	DEALLOCATE PREPARE StmtDb;
  
	SET @SQLStmtUsr = CONCAT('CREATE USER \'', usrName_in, '\'', '@', '\'localhost\'', ' IDENTIFIED BY \'', usrPassword_in, '\'');
    PREPARE StmtUsr FROM @SQLStmtUsr;
	EXECUTE StmtUsr;
	DEALLOCATE PREPARE StmtUsr;
    
    SET @SQLStmtPriv = CONCAT('GRANT ALL PRIVILEGES ON ', dbName_in, '. * TO \'', usrName_in, '\'', '@', '\'localhost\'');
    PREPARE StmtPriv FROM @SQLStmtPriv;
	EXECUTE StmtPriv;
	DEALLOCATE PREPARE StmtPriv;
    
END$$
DELIMITER ;

