-- Konfiguracni konstanty pro odeslani instrukci k platbe po podani prihlasky do kurzu.
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499630', 'COURSE_APPLICATION_PAYMENT_INSTRUCS', 'Odeslání instrukcí k platbě v rámci vytvoření přihlášky',  'true', 'BOOLEAN', 'COURSE_APPLICATION', '0',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499631', 'COURSE_APPLICATION_PAYMENT_DEADLINE', 'Lhůta ve dnech pro platbu za kurz',  '5', 'INTEGER', 'COURSE_APPLICATION', '0',  now(), 'SYSTEM');
