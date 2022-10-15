-- Konfiguracni konstanty pro potvrzeni o platbe.
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499635', 'ORGANIZATION_ADDRESS', 'Adresa klubu (ulice, město, PSČ), použití na potvrzení o platbě',  'Na Koutě 400, Bohumín, 735 81', 'STRING', 'BASIC', '0',  now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, category, spec, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499636', 'ORGANIZATION_IDENT_NO', 'IČO klubu, použití na potvrzení o platbě',  '26993660', 'STRING', 'BASIC', '0',  now(), 'SYSTEM');
