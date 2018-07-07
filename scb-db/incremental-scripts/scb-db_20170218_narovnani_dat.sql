-- najit vsechny uzivatle, kteri se opakuji
-- select username, count(lower(username)) from user group by username having count(lower(username)) > 1

select
ca.uuid
,ca.user_uuid "COURSE_APPLICATION.USER_UUID"
,u.uuid
,u.username
from course_application ca, user u
where ca.user_uuid = u.uuid
and u.username IN 
(
'Palkova.hanka@seznam.cz',
'Radka248@seznam.cz',
'SANCEZ3@SEZNAM.CZ',
'agrycova@seznam.cz',
'b.sovova@atlas.cz',
'bobini@volny.cz',
'dedochova@centrum.cz',
'gabriela.vondrakova@centrum.cz',
'hana.hanusek@seznam.cz',
'ingrid.gessle@seznam.cz',
'jana.kasanova@seznam.cz',
'jana.kuku@seznam.cz',
'karolina.aradska@seznam.cz',
'kocarek@email.cz',
'kolakows@centrum.cz',
'ksalawova@gmail.com',
'l.kacani@seznam.cz',
'lenkakol@volny.cz',
'lucieadamcikova@email.cz',
'marie.kovalska@centrum.cz',
'marketaplutova@seznam.cz',
'mjanosova@novabela.ostrava.cz',
'mteichmannova@ms-us.cz',
'pavlina.blazkovic@seznam.cz',
'pensimusova.barbora@seznam.cz',
'petralali@seznam.cz',
'petrj24@email.cz',
'plavanibohumin@seznam.cz',
'standa.bartl@seznam.cz',
'tomtar@volny.cz',
'tosta.jaros@centrum.cz',
'vaskova-jana@seznam.cz',
'vita.rumpel@gmail.com',
'z.sternadlova@seznam.cz'
)
order by u.username

UPDATE course_application SET user_uuid='61268981-84a8-4f62-88ba-87e221788c06' WHERE uuid = '73572cbd-208e-4d16-9a88-8821e8a15173';
delete from contact where uuid = (select contact_uuid from user where uuid = '1471109a-a1c8-4b7f-890e-75f617d7b117');
delete from user where uuid = '1471109a-a1c8-4b7f-890e-75f617d7b117';


