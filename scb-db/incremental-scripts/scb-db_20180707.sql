ALTER TABLE configuration MODIFY name ENUM('COURSE_APPLICATION_ALLOWED','COURSE_APPLICATION_YEAR','ORGANIZATION_NAME','ORGANIZATION_PHONE','ORGANIZATION_EMAIl','WELCOME_INFO') NOT NULL;
ALTER TABLE configuration MODIFY type  ENUM('STRING','INTEGER','BOOLEAN','ENUM') NOT NULL;

UPDATE configuration SET type ='ENUM' WHERE name = 'COURSE_APPLICATION_YEAR';

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499612', 'ORGANIZATION_NAME', 'Název klubu', 'Plavecký klub Bohumín', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499613', 'ORGANIZATION_PHONE', 'Telefonní kontakt na klub', '+420 604 920 452', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499614', 'ORGANIZATION_EMAIl', 'Emailový kontakt na klub', 'info@pkbohumin.cz', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499615', 'WELCOME_INFO', 'Uvítací informace na homepage', 'Vítejte na stránkách Plaveckého klubu Bohumín.', 'STRING', now(), 'SYSTEM');

