ALTER TABLE  contact ADD region VARCHAR(240);
ALTER TABLE  contact ADD address_validation_status ENUM('VALID','INVALID','NOT_VERIFIED') NOT NULL DEFAULT 'NOT_VERIFIED';

UPDATE contact  SET address_validation_status='NOT_VERIFIED';
