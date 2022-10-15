#!/bin/bash
# ./003-db-data.sh ${user}

echo "Creating database data. User/database $1"
echo ""

# Connect to database and run script
mysql -u $1 -p$2 $3 <<EOF
DELETE FROM configuration;
DELETE FROM codelist_item;
DELETE FROM contact;
DELETE FROM user;
DELETE FROM file;
DELETE FROM course_application_file_config;

-- PARAMS
-- admin user
SET @adm_username='$4';
SET @adm_password='$5';
-- admin contact
SET @adm_contact_firstname='$6';
SET @adm_contact_surname='$7';
SET @adm_contact_email='$8';
SET @adm_contact_phone='$9';
-- configuration
SET @config_ca_year='${10}';
SET @config_org_name='${11}';
SET @config_org_phone='${12}';
SET @config_org_email='${13}';
SET @config_welcome_info='${14}';
SET @config_base_url='${15}';
SET @config_contact_person='${16}';
SET @config_smtp_user='${17}';
SET @config_smpt_pwd='${18}';

-- INSERT
-- ADMIN USER
INSERT INTO contact (uuid, firstname, surname, street, land_registry_number, house_number, city, zip_code, email1, email2, phone1, phone2, modif_at, modif_by) 
VALUES ('62225052-4dd2-4150-91c3-8ebf26fd1571', @adm_contact_firstname, @adm_contact_surname, null, null, null, null, null, @adm_contact_email, null, @adm_contact_phone, null, now(), 'SYSTEM');
INSERT INTO user (uuid, username, password, password_generated, role, contact_uuid, modif_at, modif_by) 
VALUES ('56f26d38-e100-4505-ac74-ec65bf6869aa', @adm_username, @adm_password, '0', 'ADMIN', '62225052-4dd2-4150-91c3-8ebf26fd1571', now(), 'SYSTEM');

-- CONFIGURATION
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_ALLOWED', 'Přihlášky do kurzu povoleny', 'true', 'BOOLEAN', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_YEAR', 'Aktuální ročník', @config_ca_year, 'ENUM', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499612', 'ORGANIZATION_NAME', 'Název klubu', @config_org_name, 'STRING', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499613', 'ORGANIZATION_PHONE', 'Telefonní kontakt na klub', @config_org_phone, 'STRING', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499614', 'ORGANIZATION_EMAIl', 'Emailový kontakt na klub', @config_org_email, 'STRING', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499615', 'WELCOME_INFO', 'Uvítací informace na homepage', @config_welcome_info, 'STRING', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499616', 'COURSE_APPL_SEL_REQ', 'Výběr kurzu v rámci přihlášky', 'true', 'BOOLEAN', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499617', 'BASE_URL', 'Základní url aplikace, NEMĚNIT!', @config_base_url, 'STRING', 'BASIC', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499618', 'HEALTH_AGREEMENT', 'Text souhlasem se zdravotní způsobilostí, zobrazen na přihlášce.', 'Souhlasím s kolektivním plaveckým výcvikem svého syna/dcery. Prohlašuji na základě lékařského posouzení zdravotního stavu, že můj syn/dcera je způsobilý/způsobilá absolvovat fyzickou zátěž sportovních tréninků a plaveckých závodů bez nebezpečí poškození jeho/jejího zdravotního stavu. V případě změny zdravotního stavu budu neprodleně informovat zástupce klubu.', 'STRING', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499619', 'PERSONAL_DATA_PROCESS_AGREEMENT', 'Text souhlasem se zpracováním osobních údajů, zobrazen na přihlášce.', 'Souhlasím se zpracováním osobních údajů podle zákona č. 101/2000 Sb.Souhlasím s možností fotografování svého syna/dcery a s možností zveřejnění fotografií nebo videa v rámci propagace klubu. Potvrzuji, že jsem se seznámil s Provozním řádem klubu.', 'STRING', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499620', 'CLUB_RULES_AGREEMENT', 'Text se souhlasem s pravidly klubu, zobrazen na přihlášce.', 'Souhlasím s pravidly klubu.', 'STRING', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499624', 'COURSE_APPL_EMAIL_SPEC_TEXT', 'Specifický text v emailu rodičům po podání přihlášky na kurz.', '', 'STRING', 'COURSE_APPLICATION', '0', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499622', 'ORGANIZATION_CONTACT_PERSON', 'Jméno a příjmení kontaktní osoby klubu', @config_contact_person, 'STRING', 'BASIC', '0', now(), 'SYSTEM');
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
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499629', 'COURSE_APPLICATION_TITLE', 'Nadpis na přihlášce',  'Přihláška', 'STRING', 'COURSE_APPLICATION', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499630', 'COURSE_APPLICATION_PAYMENT_INSTRUCS', 'Odeslání instrukcí k platbě v rámci vytvoření přihlášky',  'true', 'BOOLEAN', 'COURSE_APPLICATION', '0',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499631', 'COURSE_APPLICATION_PAYMENT_DEADLINE', 'Lhůta ve dnech pro platbu za kurz',  '5', 'INTEGER', 'COURSE_APPLICATION', '0',  now(), 'SYSTEM');

INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499632', 'BANK_AUTH_TOKEN', 'Autorizacni token pro parovani plateb',  '', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499633', 'SMTP_USER', 'Emailovy ucet - uzivatel',  @config_smtp_user, 'STRING', 'BASIC', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499634', 'SMTP_PWD', 'Emailovy ucet - heslo',  @config_smpt_pwd, 'STRING', 'BASIC', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499635', 'ORGANIZATION_ADDRESS', 'Adresa klubu (ulice, město, PSČ), použití na potvrzení o platbě',  '', 'STRING', 'BASIC', '0',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499636', 'ORGANIZATION_IDENT_NO', 'IČO klubu, použití na potvrzení o platbě',  '', 'STRING', 'BASIC', '0',  now(), 'SYSTEM');

-- CODELIST_ITEM
/*
INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) 
VALUES ('82bb2300-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Prsa', '',  now(), 'SYSTEM');
INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) 
VALUES ('82bb2648-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Volný styl', '',  now(), 'SYSTEM');
INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) 
VALUES ('82bb27ec-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Motýlek', '',  now(), 'SYSTEM');
INSERT INTO codelist_item (uuid, item_type, name, description, modif_at, modif_by) 
VALUES ('82bb29f4-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Znak', '',  now(), 'SYSTEM');
*/

exit
EOF

echo "--- SUCCESS ---"

exit