DELETE FROM configuration;

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_ALLOWED', 'P�ihl�ky do kurzu povoleny', 'true', 'BOOLEAN', now(), 'SYSTEM');

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_YEAR', 'Aktu�ln� ro�n�k', '2016/2017', 'STRING', now(), 'SYSTEM');