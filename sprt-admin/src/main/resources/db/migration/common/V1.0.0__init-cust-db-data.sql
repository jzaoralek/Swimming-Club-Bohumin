DELETE FROM configuration;
DELETE FROM codelist_item;
DELETE FROM contact;
DELETE FROm user;

INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_ALLOWED', 'Přihlášky do kurzu povoleny', 'true', 'BOOLEAN', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_YEAR', 'Aktuální ročník', '2017/2018', 'ENUM', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499612', 'ORGANIZATION_NAME', 'Název klubu', 'Plavecký klub Bohumín', 'STRING', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499613', 'ORGANIZATION_PHONE', 'Telefonní kontakt na klub', '+420 604 920 452', 'STRING', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499614', 'ORGANIZATION_EMAIl', 'Emailový kontakt na klub', 'info@pkbohumin.cz', 'STRING', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499615', 'WELCOME_INFO', 'Uvítací informace na homepage', 'Vítejte na stránkách Plaveckého klubu Bohumín.', 'STRING', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499616', 'COURSE_APPL_SEL_REQ', 'Výběr kurzu v rámci přihlášky', 'true', 'BOOLEAN', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499617', 'BASE_URL', 'Základní url aplikace, NEMĚNIT!', 'https://www.pkbohumin.cz', 'STRING', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499618', 'HEALTH_AGREEMENT', 'Text souhlasem se zdravotní způsobilostí, zobrazen na přihlášce.', 'Souhlasím s kolektivním plaveckým výcvikem svého syna/dcery. Prohlašuji na základě lékařského posouzení zdravotního stavu, že můj syn/dcera je způsobilý/způsobilá absolvovat fyzickou zátěž sportovních tréninků a plaveckých závodů bez nebezpečí poškození jeho/jejího zdravotního stavu. V případě změny zdravotního stavu budu neprodleně informovat zástupce Plaveckého klubu Bohumín.', 'STRING', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499619', 'PERSONAL_DATA_PROCESS_AGREEMENT', 'Text souhlasem se zpracováním osobních údajů, zobrazen na přihlášce.', 'Souhlasím se zpracováním osobních údajů podle zákona č. 101/2000 Sb.Souhlasím s možností fotografování svého syna/dcery a s možností zveřejnění fotografií nebo videa v rámci propagace Plaveckého klubu Bohumín. Potvrzuji, že jsem se seznámil s Provozním řádem aquacentra Bohumín.', 'STRING', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499620', 'CLUB_RULES_AGREEMENT', 'Text se souhlasem s pravidly klubu, zobrazen na přihlášce.', 'Souhlasím s pravidly klubu.', 'STRING', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499624', 'COURSE_APPL_EMAIL_SPEC_TEXT', 'Specifický text v emailu rodičům po podání přihlášky na kurz.', 'Na první lekci kurzu prosíme přineste podepsané přiložené dokumenty.', 'STRING', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499622', 'ORGANIZATION_CONTACT_PERSON', 'Jméno a příjmení kontaktní osoby klubu', 'Adrian Kuder', 'STRING', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by, superadmin_config) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499623', 'PAYMENTS_AVAILABLE', 'Dostupnost modulu platby', 'false','BOOLEAN', 'BASIC', '0', now(), 'SYSTEM','1');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499625', 'ORGANIZATION_BANK_ACCOUNT_NUMBER', 'Číslo bankovního účtu klubu.', '', 'STRING', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499626', 'ATTENDANCE_FOR_PARENTS_VISIBLE', 'Viditelnost docházky pro rodiče.',  'true', 'BOOLEAN', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499627', 'ALLOW_COURSE_APPL_NOT_VALID_ADDRESS', 'Povolení založení přihlášky s neověřenou adresou',  'false', 'BOOLEAN', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499628', 'ALLOW_CHECK_SUM_BIRTHNUM_VALIDATION', 'Povolení validace rodného čísla na kontrolní součet',  'true', 'BOOLEAN', 'BASIC', '0', now(), 'SYSTEM');

INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499629', 'COURSE_APPLICATION_TITLE', 'Nadpis na přihlášce',  'Přihláška do kurzu 2019/2020', 'STRING', 'COURSE_APPLICATION', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499630', 'COURSE_APPLICATION_PAYMENT_INSTRUCS', 'Odeslání instrukcí k platbě v rámci vytvoření přihlášky',  'true', 'BOOLEAN', 'COURSE_APPLICATION', '0',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499631', 'COURSE_APPLICATION_PAYMENT_DEADLINE', 'Lhůta ve dnech pro platbu za kurz',  '5', 'INTEGER', 'COURSE_APPLICATION', '0',  now(), 'SYSTEM');

INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499632', 'BANK_AUTH_TOKEN', 'Autorizacni token pro parovani plateb',  '895qPPEdiFhbGpfqPq3Srp57FJjIixZjvbCRnyeCWRzhH9aZ51tnUxv9CXJ5yr2U', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499633', 'SMTP_USER', 'Emailovy ucet - uzivatel',  'info@sportologic.cz', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499634', 'SMTP_PWD', 'Emailovy ucet - heslo',  'sdpTQ_433', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');

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

COMMIT;