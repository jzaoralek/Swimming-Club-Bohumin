ALTER TABLE COURSE_COURSE_PARTICIPANT ADD VARSYMBOL_CORE INT NULL auto_increment UNIQUE;
ALTER TABLE COURSE_COURSE_PARTICIPANT ADD individual_price_semester_1 INT NULL;
ALTER TABLE COURSE_COURSE_PARTICIPANT ADD individual_price_semester_2 INT NULL;

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499625', 'ORGANIZATION_BANK_ACCOUNT_NUMBER', 'Číslo bankovního účtu klubu.', '', 'STRING', now(), 'SYSTEM');
