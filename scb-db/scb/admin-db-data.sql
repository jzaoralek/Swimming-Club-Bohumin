DELETE FROM customer_config;

INSERT INTO customer_config (uuid,cust_id,cust_name,cust_default,db_url,db_user,db_password,modif_at,modif_by)
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499611', 'pkb', 'Plavecký klub Bohumín', '1', 'jdbc:mysql://51.75.253.168:3306/scb', 'mysql_ovh', 'E155_bb-98787bT-11nF9ONee8Qa', now(), 'SYSTEM');
INSERT INTO customer_config (uuid,cust_id,cust_name,cust_default,db_url,db_user,db_password,modif_at,modif_by)
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499612', 'test', 'Plavecký klub Bohumín - test', '0', 'jdbc:mysql://51.75.253.168:3306/scb_tst', 'scb_tst', 'scbTst1234*', now(), 'SYSTEM');

COMMIT;