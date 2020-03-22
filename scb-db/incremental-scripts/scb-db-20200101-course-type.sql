ALTER TABLE course ADD TYPE ENUM('STANDARD','TWO_SEMESTER') NULL DEFAULT 'TWO_SEMESTER';
ALTER TABLE course ADD active ENUM('0','1') NOT NULL DEFAULT '1';

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by)
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499628', 'ALLOW_CHECK_SUM_BIRTHNUM_VALIDATION', 'Povolení validace rodného čísla na kontrolní součet',  'true', 'BOOLEAN', now(), 'SYSTEM');

ALTER  TABLE payment MODIFY type ENUM('CASH','BANK_TRANS','DONATE','DISCOUNT','OTHER') NOT NULL;