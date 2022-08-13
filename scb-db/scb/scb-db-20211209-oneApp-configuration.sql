INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499632', 'BANK_AUTH_TOKEN', 'Autorizacni token pro parovani plateb',  '895qPPEdiFhbGpfqPq3Srp57FJjIixZjvbCRnyeCWRzhH9aZ51tnUxv9CXJ5yr2U', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499633', 'SMTP_USER', 'Emailovy ucet - uzivatel',  'pkbohumin@sportologic.cz', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499634', 'SMTP_PWD', 'Emailovy ucet - heslo',  '9fRnYsWgqd4PGVUL', 'STRING', 'BASIC', '1',  now(), 'SYSTEM');

commit;