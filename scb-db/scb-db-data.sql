DELETE FROM configuration;
DELETE FROM codelist_item;
DELETE FROM contact;
DELETE FROm user;

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_ALLOWED', 'Přihlášky do kurzu povoleny', 'true', 'BOOLEAN', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_YEAR', 'Aktuální ročník', '2017/2018', 'STRING', now(), 'SYSTEM');

INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) 
VALUES ('82bb2300-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Prsa', '',  now(), 'SYSTEM');
INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) 
VALUES ('82bb2648-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Volný styl', '',  now(), 'SYSTEM');
INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) 
VALUES ('82bb27ec-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Motýlek', '',  now(), 'SYSTEM');
INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) 
VALUES ('82bb29f4-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Znak', '',  now(), 'SYSTEM');

-- ADMIN USER
INSERT INTO contact (uuid, firstname, surname, street, land_registry_number, house_number, city, zip_code, email1, email2, phone1, phone2, modif_at, modif_by) 
VALUES ('62225052-4dd2-4150-91c3-8ebf26fd1571', 'Adrian', 'Kuder', null, null, null, 'Bohumín', null, 'kuder.a@zscsa.cz', null, '604920452', null, now(), 'SYSTEM');
INSERT INTO user (uuid, username, password, password_generated, role, contact_uuid, modif_at, modif_by) 
VALUES ('56f26d38-e100-4505-ac74-ec65bf6869aa', 'a.kuder', 'popov', '0', 'ADMIN', '62225052-4dd2-4150-91c3-8ebf26fd1571', now(), 'SYSTEM');

INSERT INTO contact (uuid, firstname, surname, street, land_registry_number, house_number, city, zip_code, email1, email2, phone1, phone2, modif_at, modif_by) 
VALUES ('62225052-4dd2-4150-91c3-8ebf26fd1572', 'Tonda', 'Blaník', null, null, null, 'Karviná', null, 'kosatky@kosatkykarvina.cz', null, '+420001001002', null, now(), 'SYSTEM');
INSERT INTO user (uuid, username, password, password_generated, role, contact_uuid, modif_at, modif_by) 
VALUES ('56f26d38-e100-4505-ac74-ec65bf6869ab', 'kosatky', 'kosatky', '0', 'ADMIN', '62225052-4dd2-4150-91c3-8ebf26fd1572', now(), 'SYSTEM');


COMMIT;