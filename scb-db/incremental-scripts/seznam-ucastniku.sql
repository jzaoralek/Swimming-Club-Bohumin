select distinct
 con_part.firstname 
, con_part.surname 
, cp.uuid
, con_repr.email1 
, usr.uuid 
, ca.uuid 
, ca.payed 
, ca.year_from 
, ca.year_to 
, ca.modif_at 
, ca.modif_by 
, ccp.varsymbol_core  
, c.uuid 
, c.name  
, c.price_semester_1  
, c.price_semester_2  			
, (select sum(amount) from payment where payment.course_participant_uuid = cp.uuid and payment.course_uuid = c.uuid) 
, (select count(*) 
		from course_application cain 
		where cain.course_participant_uuid = ca.course_participant_uuid 
			and cain.year_from = ca.year_from - 1) 
from  
course_application ca 
, course_participant cp 
, contact con_part 
, contact con_repr 
, user usr 
, course_course_participant ccp 
, course c 
where 
ca.course_participant_uuid = cp.uuid
and cp.contact_uuid = con_part.uuid 
and ca.user_uuid = usr.uuid 
and usr.contact_uuid = con_repr.uuid 
AND ca.year_from = 2017 
AND ca.year_to = 2018
AND cp.uuid = ccp.course_participant_uuid 
AND c.uuid = ccp.course_uuid 
AND c.year_from = 2017
AND c.year_to = 2018
order by con_part.surname