DELETE FROM configuration;
DELETE FROM codelist_item;
DELETE FROM contact;
DELETE FROm user;

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_ALLOWED', 'Přihlášky do kurzu povoleny', 'true', 'BOOLEAN', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_YEAR', 'Aktuální ročník', '2017/2018', 'ENUM', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499612', 'ORGANIZATION_NAME', 'Název klubu', 'Klub plaveckých sportů Ostrava', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499613', 'ORGANIZATION_PHONE', 'Telefonní kontakt na klub', '+420 736 210 698', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499614', 'ORGANIZATION_EMAIl', 'Emailový kontakt na klub', 'petka.formankova@seznam.cz', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499615', 'WELCOME_INFO', 'Uvítací informace na homepage', 'Vítejte na stránkách Plaveckých kurzů Ostrava.', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499616', 'COURSE_APPL_SEL_REQ', 'Výběr kurzu v rámci přihlášky', 'true', 'BOOLEAN', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499617', 'BASE_URL', 'Základní url aplikace, NEMĚNIT!', 'https://www.sportologic.cz/kpsostrava', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499618', 'HEALTH_AGREEMENT', 'Text souhlasem se zdravotní způsobilostí, zobrazen na přihlášce.', 'Souhlasím s kolektivním plaveckým výcvikem svého syna/dcery. Prohlašuji na základě lékařského posouzení zdravotního stavu, že můj syn/dcera je způsobilý/způsobilá absolvovat fyzickou zátěž sportovních tréninků a plaveckých závodů bez nebezpečí poškození jeho/jejího zdravotního stavu. V případě změny zdravotního stavu budu neprodleně informovat zástupce Plaveckého klubu Bohumín.', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499619', 'PERSONAL_DATA_PROCESS_AGREEMENT', 'Text souhlasem se zpracováním osobních údajů, zobrazen na přihlášce.', 'Souhlasím se zpracováním osobních údajů podle zákona č. 101/2000 Sb.Souhlasím s možností fotografování svého syna/dcery a s možností zveřejnění fotografií nebo videa v rámci propagace Plaveckého klubu Bohumín. Potvrzuji, že jsem se seznámil s Provozním řádem aquacentra Bohumín.', 'STRING', now(), 'SYSTEM');

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
VALUES ('62225052-4dd2-4150-91c3-8ebf26fd1571', 'Petra', 'Formánková', null, null, null, 'Ostrava', null, 'petka.formankova@seznam.cz', null, '736210698', null, now(), 'SYSTEM');
INSERT INTO user (uuid, username, password, password_generated, role, contact_uuid, modif_at, modif_by) 
VALUES ('56f26d38-e100-4505-ac74-ec65bf6869aa', 'p.formankova', 'kpsostrava', '0', 'ADMIN', '62225052-4dd2-4150-91c3-8ebf26fd1571', now(), 'SYSTEM');


COMMIT;