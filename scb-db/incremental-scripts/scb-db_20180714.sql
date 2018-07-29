-- 14.7.2018, nasazeno na pkbohumin.cz/demo
CREATE TABLE course_location (
	uuid varchar(36),
	name VARCHAR(240) CHARACTER SET utf8,
	description VARCHAR(1000) CHARACTER SET utf8,
	PRIMARY KEY (uuid)
);

ALTER TABLE course ADD course_location_uuid varchar(36) REFERENCES course_location(uuid);
ALTER TABLE course ADD max_participant_count INT;

ALTER TABLE configuration MODIFY name varchar(36) NOT NULL;

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499616', 'COURSE_APPL_SEL_REQ', 'Výběr kurzu v rámci přihlášky', 'true', 'BOOLEAN', now(), 'SYSTEM');

ALTER TABLE course MODIFY description VARCHAR(1000) CHARACTER SET utf8;
ALTER TABLE course_location MODIFY description VARCHAR(1000) CHARACTER SET utf8;

-- 29.7.2018
ALTER TABLE configuration MODIFY val varchar(1000) CHARACTER SET utf8;

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499617', 'BASE_URL', 'Základní url aplikace, NEMĚNIT!', 'https://www.pkbohumin.cz', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499618', 'HEALTH_AGREEMENT', 'Text souhlasem se zdravotní způsobilostí, zobrazen na přihlášce.', 'Souhlasím s kolektivním plaveckým výcvikem svého syna/dcery. Prohlašuji na základě lékařského posouzení zdravotního stavu, že můj syn/dcera je způsobilý/způsobilá absolvovat fyzickou zátěž sportovních tréninků a plaveckých závodů bez nebezpečí poškození jeho/jejího zdravotního stavu. V případě změny zdravotního stavu budu neprodleně informovat zástupce Plaveckého klubu Bohumín.', 'STRING', now(), 'SYSTEM');
INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-11e6-ae22-56b6b6499619', 'PERSONAL_DATA_PROCESS_AGREEMENT', 'Text souhlasem se zpracováním osobních údajů, zobrazen na přihlášce.', 'Souhlasím se zpracováním osobních údajů podle zákona č. 101/2000 Sb.Souhlasím s možností fotografování svého syna/dcery a s možností zveřejnění fotografií nebo videa v rámci propagace Plaveckého klubu Bohumín. Potvrzuji, že jsem se seznámil s Provozním řádem aquacentra Bohumín.', 'STRING', now(), 'SYSTEM');
