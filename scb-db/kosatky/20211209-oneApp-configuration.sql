INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499632', 'BANK_AUTH_TOKEN', 'Autorizacni token pro parovani plateb',  'jl0xfU0mP0zfBzP6ckaPRUW2RWwCQMwHAKxl9wbLSHASXLL5x15Suft28QdVIq6q', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499633', 'SMTP_USER', 'Emailovy ucet - uzivatel',  'kosatkykarvina@sportologic.cz', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499634', 'SMTP_PWD', 'Emailovy ucet - heslo',  'KosatkyKarvina2018*', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');

commit;