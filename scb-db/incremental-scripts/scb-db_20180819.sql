ALTER TABLE configuration ADD superadmin_config ENUM('0','1') NOT NULL DEFAULT '0';

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499620', 'CLUB_RULES_AGREEMENT', 'Text se souhlasem s pravidly klubu, zobrazen na přihlášce.', 'Souhlasím s pravidly klubu.', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499624', 'COURSE_APPL_EMAIL_SPEC_TEXT', 'Specifický text v emailu rodičům po podání přihlášky na kurz.', 'Přiložené dokumenty prosím přineste do konce září.', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499622', 'ORGANIZATION_CONTACT_PERSON', 'Jméno a příjmení kontaktní osoby klubu', 'Adrian Kuder', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by, superadmin_config) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499623', 'PAYMENTS_AVAILABLE', 'Dostupnost modulu platby', 'true','BOOLEAN', now(), 'SYSTEM','1');