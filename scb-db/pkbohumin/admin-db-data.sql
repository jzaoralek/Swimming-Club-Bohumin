DELETE FROM customer_config;

INSERT INTO customer_config (uuid,cust_id,cust_name,cust_default,db_url,db_user,db_password,modif_at,modif_by)
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499611', 'scb', 'Plavecký klub Bohumín', '1', 'jdbc:mysql://localhost:3306/scb', 'scb', 'scb', now(), 'SYSTEM');
INSERT INTO customer_config (uuid,cust_id,cust_name,cust_default,db_url,db_user,db_password,modif_at,modif_by)
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499612', 'kosatky', 'Kosatky Karviná', '0', 'jdbc:mysql://localhost:3306/kosatky', 'kosatky', 'kosatky', now(), 'SYSTEM');

COMMIT;