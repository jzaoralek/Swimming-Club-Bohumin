ALTER TABLE contact ADD region VARCHAR(240);
ALTER TABLE contact ADD address_validation_status ENUM('VALID','INVALID','NOT_VERIFIED') NOT NULL DEFAULT 'NOT_VERIFIED';
ALTER TABLE contact ADD foreign_address VARCHAR(1000) CHARACTER SET utf8;
ALTER TABLE contact ADD evidence_number VARCHAR(32);

ALTER TABLE contact ADD citizenship VARCHAR(3) NOT NULL DEFAULT 'CZE';
ALTER TABLE contact ADD sex_male ENUM('0','1') NOT NULL DEFAULT '1';

ALTER TABLE contact MODIFY house_number VARCHAR(32);

UPDATE contact  SET address_validation_status='NOT_VERIFIED';
UPDATE contact  SET citizenship='CZE';

