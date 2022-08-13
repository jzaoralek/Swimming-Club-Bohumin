INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499632', 'BANK_AUTH_TOKEN', 'Autorizacni token pro parovani plateb',  '', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499633', 'SMTP_USER', 'Emailovy ucet - uzivatel',  'zralociznojmo@sportologic.cz', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499634', 'SMTP_PWD', 'Emailovy ucet - heslo',  'Zralociznojmo12342020*', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');

commit;