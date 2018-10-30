ALTER TABLE  course_course_participant ADD varsymbol_core INT NULL auto_increment UNIQUE;
ALTER TABLE  course_course_participant ADD individual_price_semester_1 INT NULL;
ALTER TABLE  course_course_participant ADD individual_price_semester_2 INT NULL;
ALTER TABLE  course_course_participant ADD notified_semester_1_payment_at TIMESTAMP NULL;
ALTER TABLE  course_course_participant ADD notified_semester_2_payment_at TIMESTAMP NULL;

INSERT INTO configuration (uuid, name, description, val, type, modif_at, modif_by) 
VALUES ('fd33a4d4-7e99-22e6-ae22-56b6b6499625', 'ORGANIZATION_BANK_ACCOUNT_NUMBER', 'Číslo bankovního účtu klubu.', '', 'STRING', now(), 'SYSTEM');
