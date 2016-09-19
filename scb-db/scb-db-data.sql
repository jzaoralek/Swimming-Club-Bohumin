DELETE FROM configuration;

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_ALLOWED', 'Povolení zadávání pøihlášek do kurzu', 'true', 'BOOLEAN', now(), 'SYSTEM');

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_YEAR', 'Rok pøihlášek do kurzu', '2016', 'INTEGER', now(), 'SYSTEM');