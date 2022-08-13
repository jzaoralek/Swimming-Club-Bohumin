DELETE FROM customer_config;

-- Kosatky
INSERT INTO customer_config (uuid,cust_id,cust_name,cust_default,db_url,db_user,db_password,modif_at,modif_by)
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499611', 'kosatkykarvina', 'Kosatky Karviná', '1', 'jdbc:mysql://51.75.253.168:3306/kosatkykarvina', 'kosatkykarvina', 'Kosatkykarvina2018*', now(), 'SYSTEM');

-- Plavani FM
INSERT INTO customer_config (uuid,cust_id,cust_name,cust_default,db_url,db_user,db_password,modif_at,modif_by)
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499612', 'plavanifm', 'Plavecký oddíl Frýdek-Místek, z.s.', '0', 'jdbc:mysql://51.75.253.168:3306/plavanifm', 'plavanifm', 'Plavanifm20191234*', now(), 'SYSTEM');

-- plaveckykemp
INSERT INTO customer_config (uuid,cust_id,cust_name,cust_default,db_url,db_user,db_password,modif_at,modif_by)
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499613', 'plaveckykemp', 'Plavecký kemp', '0', 'jdbc:mysql://51.75.253.168:3306/plaveckykemp', 'plaveckykemp', 'Plaveckykemp2020*', now(), 'SYSTEM');

-- zralociznojmo
INSERT INTO customer_config (uuid,cust_id,cust_name,cust_default,db_url,db_user,db_password,modif_at,modif_by)
VALUES ('61c867ae-7e9a-11e6-ae22-56b6b6499614', 'zralociznojmo', 'Žraloci Znojmo', '0', 'jdbc:mysql://51.75.253.168:3306/sportologic_demo', 'sportologic_demo', 'Sportologic_demo2018*', now(), 'SYSTEM');

COMMIT;