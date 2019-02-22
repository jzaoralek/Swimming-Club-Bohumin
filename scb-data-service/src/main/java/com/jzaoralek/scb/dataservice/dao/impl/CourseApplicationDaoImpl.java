package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.CourseApplicationDao;
import com.jzaoralek.scb.dataservice.dao.CourseDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.dao.ScbUserDao;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.CoursePaymentVO;
import com.jzaoralek.scb.dataservice.domain.ScbUser;

@Repository
public class CourseApplicationDaoImpl extends BaseJdbcDao implements CourseApplicationDao {

	@Autowired
	private CourseDao courseDao;

	private static final String COURSE_PARTICIPANT_UUID_PARAM = "COURSE_PARTICIPANT_UUID";
	private static final String PAYED_PARAM = "PAYED";

	private static final String INSERT = "INSERT INTO course_application (uuid, year_from, year_to, course_participant_uuid, user_uuid, modif_at, modif_by) values (:"+UUID_PARAM+",:"+YEAR_FROM_PARAM+",:"+YEAR_TO_PARAM+",:"+COURSE_PARTICIPANT_UUID_PARAM+",:"+USER_UUID_PARAM+",:"+MODIF_AT_PARAM+",:"+MODIF_BY_PARAM+")";
	private static final String SELECT_ALL = "select " +
					" con_part.firstname " +
					", con_part.surname " +
					", cp.uuid \"participant_uuid\" " +
					", cp.birthdate " +
					", cp.personal_number " +
					", con_repr.firstname \"representative_firstname\" " +
					", con_repr.surname \"representative_surname\" " +
					", con_repr.phone1 " +
					", con_repr.email1 " +
					", con_repr.phone2 " +
					", con_repr.email2 " +
					", usr.uuid  \"representative_uuid\" " +
					", ca.uuid " +
					", ca.modif_at " +
					", ca.modif_by " +
					", ca.payed " +
					", con_part.city " +
					", con_part.street " +
					", con_part.land_registry_number " +
					", con_part.house_number " +
					", con_part.zip_code " +
					", ca.year_from " +
					", ca.year_to " +
					", (select count(*) " +
					"		from course_application cain " +
					"		where cain.course_participant_uuid = ca.course_participant_uuid " +
					"			and cain.year_from = ca.year_from - 1) \"current_participant\" " +
					"from  " +
					"course_application ca " +
					", course_participant cp " +
					", contact con_part " +
					", contact con_repr " +
					", user usr " +
					"where " +
					"ca.course_participant_uuid = cp.uuid " +
					"and cp.contact_uuid = con_part.uuid " +
					"and ca.user_uuid = usr.uuid " +
					"and usr.contact_uuid = con_repr.uuid " +
					"AND ca.year_from = :"+YEAR_FROM_PARAM+" " +
					"AND ca.year_to = :"+YEAR_TO_PARAM+ " " +
					"order by ca.modif_at desc ";

	private static final String SELECT_UNREGISTERED_TO_CURRENT_YEAR = "select " +
			" con_part.firstname " +
			", con_part.surname " +
			", cp.uuid \"participant_uuid\" " +
			", cp.birthdate " +
			", cp.personal_number " +
			", con_repr.firstname \"representative_firstname\" " +
			", con_repr.surname \"representative_surname\" " +
			", con_repr.phone1 " +
			", con_repr.email1 " +
			", con_repr.phone2 " +
			", con_repr.email2 " +
			", usr.uuid  \"representative_uuid\" " +
			", ca.uuid " +
			", ca.modif_at " +
			", ca.modif_by " +
			", ca.payed " +
			", con_part.city " +
			", con_part.street " +
			", con_part.land_registry_number " +
			", con_part.house_number " +
			", con_part.zip_code " +
			", ca.year_from " +
			", ca.year_to " +
			", (select count(*) " +
			"		from course_application cain " +
			"		where cain.course_participant_uuid = ca.course_participant_uuid " +
			"			and cain.year_from = ca.year_from - 1) \"current_participant\" " +
			"from  " +
			"course_application ca " +
			", course_participant cp " +
			", contact con_part " +
			", contact con_repr " +
			", user usr " +
			"where " +
			"ca.course_participant_uuid = cp.uuid " +
			"and cp.contact_uuid = con_part.uuid " +
			"and ca.user_uuid = usr.uuid " +
			"and usr.contact_uuid = con_repr.uuid " +
			"AND ca.year_from = :"+YEAR_FROM_PARAM+"-1 " +
			"AND ca.year_to = :"+YEAR_TO_PARAM+ "-1 " +
			"AND cp.uuid NOT IN ( " +
			"		SELECT cp.uuid " +
			"		FROM  " +
			"			course_participant cp " +
			"			, course_application ca " +
			"		WHERE  " +
			"			cp.uuid = ca.course_participant_uuid " +
			"			AND ca.year_from = :"+YEAR_FROM_PARAM+" " +
			"			AND ca.year_to = :"+YEAR_TO_PARAM+") " +
			"order by ca.modif_at desc ";
	
	private static final String SELECT_BY_COURSE_PARTICIPANT_UUID = "select " +
			" con_part.firstname " +
			", con_part.surname " +
			", cp.uuid \"participant_uuid\" " +
			", cp.birthdate " +
			", cp.personal_number " +
			", con_repr.firstname \"representative_firstname\" " +
			", con_repr.surname \"representative_surname\" " +
			", con_repr.phone1 " +
			", con_repr.email1 " +
			", con_repr.phone2 " +
			", con_repr.email2 " +
			", usr.uuid  \"representative_uuid\" " +
			", ca.uuid " +
			", ca.modif_at " +
			", ca.modif_by " +
			", ca.payed " +
			", con_part.city " +
			", con_part.street " +
			", con_part.land_registry_number " +
			", con_part.house_number " +
			", con_part.zip_code " +
			", ca.year_from " +
			", ca.year_to " +
			", (select count(*) " +
			"		from course_application cain " +
			"		where cain.course_participant_uuid = ca.course_participant_uuid " +
			"			and cain.year_from = ca.year_from - 1) \"current_participant\" " +
			"from  " +
			"course_application ca " +
			", course_participant cp " +
			", contact con_part " +
			", contact con_repr " +
			", user usr " +
			"where " +
			"ca.course_participant_uuid = cp.uuid " +
			"and cp.contact_uuid = con_part.uuid " +
			"and ca.user_uuid = usr.uuid " +
			"and usr.contact_uuid = con_repr.uuid " +
			"AND ca.course_participant_uuid = :"+COURSE_PARTICIPANT_UUID_PARAM+" " +
			"order by ca.modif_at desc ";
	
	private static final String SELECT_NOT_IN_COURSE = "select " +
					" con_part.firstname " +
					", con_part.surname " +
					", cp.uuid \"participant_uuid\" " +
					", cp.birthdate " +
					", cp.personal_number " +
					", con_repr.firstname \"representative_firstname\" " +
					", con_repr.surname \"representative_surname\" " +
					", con_repr.phone1 " +
					", con_repr.email1 " +
					", con_repr.phone2 " +
					", con_repr.email2 " +
					", usr.uuid  \"representative_uuid\" " +
					", ca.uuid " +
					", ca.modif_at " +
					", ca.modif_by " +
					", ca.payed " +
					", con_part.city " +
					", con_part.street " +
					", con_part.land_registry_number " +
					", con_part.house_number " +
					", con_part.zip_code " +
					", ca.year_from " +
					", ca.year_to " +
					", (select count(*) " +
					"		from course_application cain " +
					"		where cain.course_participant_uuid = ca.course_participant_uuid " +
					"			and cain.year_from = ca.year_from - 1) \"current_participant\" " +
					"from  " +
					"course_application ca " +
					", course_participant cp " +
					", contact con_part " +
					", contact con_repr " +
					", user usr " +
					"where " +
					"ca.course_participant_uuid = cp.uuid " +
					"and cp.contact_uuid = con_part.uuid " +
					"and ca.user_uuid = usr.uuid " +
					"and usr.contact_uuid = con_repr.uuid " +
					"AND ca.year_from = :"+YEAR_FROM_PARAM+" " +
					"AND ca.year_to = :"+YEAR_TO_PARAM+ " " +
					"AND cp.uuid NOT IN (SELECT ccp.course_participant_uuid FROM course_course_participant ccp WHERE ccp.course_uuid = :"+COURSE_UUID_PARAM+") " +
					"order by ca.modif_at desc ";

	private static final String SELECT_IN_COURSE = "select " +
			" con_part.firstname " +
			", con_part.surname " +
			", cp.uuid \"participant_uuid\" " +
			", cp.birthdate " +
			", cp.personal_number " +
			", con_repr.firstname \"representative_firstname\" " +
			", con_repr.surname \"representative_surname\" " +
			", con_repr.phone1 " +
			", con_repr.email1 " +
			", con_repr.phone2 " +
			", con_repr.email2 " +
			", usr.uuid  \"representative_uuid\" " +
			", ca.uuid " +
			", ca.modif_at " +
			", ca.modif_by " +
			", ca.payed " +
			", con_part.city " +
			", con_part.street " +
			", con_part.land_registry_number " +
			", con_part.house_number " +
			", con_part.zip_code " +
			", ca.year_from " +
			", ca.year_to " +
			", (select count(*) " +
			"		from course_application cain " +
			"		where cain.course_participant_uuid = ca.course_participant_uuid " +
			"			and cain.year_from = ca.year_from - 1) \"current_participant\" " +
			"from  " +
			"course_application ca " +
			", course_participant cp " +
			", contact con_part " +
			", contact con_repr " +
			", user usr " +
			"where " +
			"ca.course_participant_uuid = cp.uuid " +
			"and cp.contact_uuid = con_part.uuid " +
			"and ca.user_uuid = usr.uuid " +
			"and usr.contact_uuid = con_repr.uuid " +
			"AND ca.year_from = :"+YEAR_FROM_PARAM+" " +
			"AND ca.year_to = :"+YEAR_TO_PARAM+ " " +
			"AND cp.uuid IN (SELECT ccp.course_participant_uuid FROM course_course_participant ccp WHERE ccp.course_uuid = :"+COURSE_UUID_PARAM+") " +
			"order by ca.modif_at desc ";

//	private static final String SELECT_ASSIGNED_TO_COURSE = "select distinct" +
//			" con_part.firstname " +
//			", con_part.surname " +
//			", cp.uuid \"participant_uuid\" " +
//			", cp.birthdate " +
//			", cp.personal_number " +
//			", con_repr.firstname \"representative_firstname\" " +
//			", con_repr.surname \"representative_surname\" " +
//			", con_repr.phone1 " +
//			", con_repr.email1 " +
//			", con_repr.phone2 " +
//			", con_repr.email2 " +
//			", usr.uuid  \"representative_uuid\" " +
//			", ca.uuid " +
//			", ca.modif_at " +
//			", ca.modif_by " +
//			", ca.payed " +
//			", con_part.city " +
//			", con_part.street " +
//			", con_part.land_registry_number " +
//			", con_part.house_number " +
//			", con_part.zip_code " +
//			", ca.year_from " +
//			", ca.year_to " +
//			", c.uuid \"COURSE_COURSE_PARTICIPANT_UUID\" " +
//			", c.name \"COURSE_NAME_COURSE_PARTICIPANT_UUID\" " +
//			", c.price_semester_1 \"COURSE_PRICE_SEMESTER_1\" " +
//			", c.price_semester_2 \"COURSE_PRICE_SEMESTER_2\" " +			
//			", (select sum(amount) from payment where payment.course_participant_uuid = cp.uuid and payment.course_uuid = c.uuid) \"PAYMENT_SUM\"" +
//			", (select count(*) " +
//			"		from course_application cain " +
//			"		where cain.course_participant_uuid = ca.course_participant_uuid " +
//			"			and cain.year_from = ca.year_from - 1) \"current_participant\" " +
//			"from  " +
//			"course_application ca " +
//			", course_participant cp " +
//			", contact con_part " +
//			", contact con_repr " +
//			", user usr " +
//			", course_course_participant ccp " +
//			", course c " +
//			"where " +
//			"ca.course_participant_uuid = cp.uuid " +
//			"and cp.contact_uuid = con_part.uuid " +
//			"and ca.user_uuid = usr.uuid " +
//			"and usr.contact_uuid = con_repr.uuid " +
//			"AND ca.year_from = :"+YEAR_FROM_PARAM+" " +
//			"AND ca.year_to = :"+YEAR_TO_PARAM+ " " +
//			"AND cp.uuid = ccp.course_participant_uuid " +
//			"AND c.uuid = ccp.course_uuid " +
//			"AND c.year_from = :"+YEAR_FROM_PARAM+" " +
//			"AND c.year_to = :"+YEAR_TO_PARAM+ " " +
//			"order by con_part.surname ";
	
//	private static final String SELECT_ASSIGNED_TO_COURSE_MIN = "select distinct" +
//			" con_part.firstname " +
//			", con_part.surname " +
//			", cp.uuid \"participant_uuid\" " +
//			", con_repr.email1 " +
//			", usr.uuid  \"representative_uuid\" " +
//			", ca.uuid " +
//			", ca.payed " +
//			", ca.year_from " +
//			", ca.year_to " +
//			", ca.modif_at " +
//			", ca.modif_by " +
//			", ccp.varsymbol_core " + 
//			", ccp.notified_semester_1_payment_at " + 
//			", ccp.notified_semester_2_payment_at " + 
//			", c.uuid \"COURSE_COURSE_PARTICIPANT_UUID\" " +
//			", c.name \"COURSE_NAME_COURSE_PARTICIPANT_UUID\" " +
//			", c.price_semester_1 \"COURSE_PRICE_SEMESTER_1\" " +
//			", c.price_semester_2 \"COURSE_PRICE_SEMESTER_2\" " +			
//			", (select sum(amount) from payment where payment.course_participant_uuid = cp.uuid and payment.course_uuid = c.uuid) \"PAYMENT_SUM\"" +
//			", (select count(*) " +
//			"		from course_application cain " +
//			"		where cain.course_participant_uuid = ca.course_participant_uuid " +
//			"			and cain.year_from = ca.year_from - 1) \"current_participant\" " +
//			"from  " +
//			"course_application ca " +
//			", course_participant cp " +
//			", contact con_part " +
//			", contact con_repr " +
//			", user usr " +
//			", course_course_participant ccp " +
//			", course c " +
//			"where " +
//			"ca.course_participant_uuid = cp.uuid " +
//			"and cp.contact_uuid = con_part.uuid " +
//			"and ca.user_uuid = usr.uuid " +
//			"and usr.contact_uuid = con_repr.uuid " +
//			"AND ca.year_from = :"+YEAR_FROM_PARAM+" " +
//			"AND ca.year_to = :"+YEAR_TO_PARAM+ " " +
//			"AND cp.uuid = ccp.course_participant_uuid " +
//			"AND c.uuid = ccp.course_uuid " +
//			"AND c.year_from = :"+YEAR_FROM_PARAM+" " +
//			"AND c.year_to = :"+YEAR_TO_PARAM+ " " +
//			"order by con_part.surname ";

	
	private static final String SELECT_COURSE_APPLICATION_BY_YEAR_FROM_TO_MIN = 
			"select " +
			"con_part.firstname " +
			", con_part.surname " +
			", con_part.city " +
			", con_part.street " +
			", con_part.land_registry_number " +
			", con_part.house_number " +
			", con_part.zip_code " +
			", cp.uuid \"participant_uuid\" " +
			", con_repr.phone1 " +
			", con_repr.email1 " + 
			", usr.uuid  \"representative_uuid\" " +
			", ca.uuid " +
			", ca.year_from " +
			", ca.year_to " +
			", ca.modif_at " +
			", ca.modif_by " +
			"from " +
			"course_application ca " +
			", course_participant cp " +
			", contact con_part " +
			", contact con_repr " +
			", user usr " +
			"where " +
			"ca.course_participant_uuid = cp.uuid " +
			"and cp.contact_uuid = con_part.uuid " +
			"and ca.user_uuid = usr.uuid " +
			"and usr.contact_uuid = con_repr.uuid " +
			"AND ca.year_from = :"+YEAR_FROM_PARAM+" " +
			"AND ca.year_to = :"+YEAR_TO_PARAM+ " " +
			"order by con_part.surname";
	
	private static final String SELECT_COURSE_COURSE_PARTICIPANT_BY_YEAR_FROM_TO = 
			"select " +
			"ccp.course_participant_uuid \"participant_uuid\" " +
			", ccp.varsymbol_core  " +
			", ccp.notified_semester_1_payment_at  " +
			", ccp.notified_semester_2_payment_at " +
			", ccp.course_partic_interrupted_at " +
			", c.uuid \"COURSE_COURSE_PARTICIPANT_UUID\" " +
			", c.name \"COURSE_NAME_COURSE_PARTICIPANT_UUID\" " + 
			", c.price_semester_1 \"COURSE_PRICE_SEMESTER_1\"  " +
			", c.price_semester_2 \"COURSE_PRICE_SEMESTER_2\" " +
			", (select sum(amount) from payment where payment.course_participant_uuid = ccp.course_participant_uuid and payment.course_uuid = c.uuid) \"PAYMENT_SUM\" " +
			"from " +
			"course_course_participant ccp " +
			", course c " +
			"where " +
			"c.uuid = ccp.course_uuid " +
			"AND ccp.course_partic_interrupted_at is null " +
			"AND c.year_from = :"+YEAR_FROM_PARAM+" " +
			"AND c.year_to = :"+YEAR_TO_PARAM;
			
	private static final String SELECT_BY_UUID = "select uuid, year_from, year_to, course_participant_uuid, user_uuid, modif_at, modif_by, payed from course_application where uuid=:" + UUID_PARAM;
	private static final String DELETE = "DELETE FROM course_application where uuid = :" + UUID_PARAM;
	private static final String UPDATE = "UPDATE course_application SET year_from=:"+YEAR_FROM_PARAM+", year_to=:"+YEAR_TO_PARAM+", course_participant_uuid=:"+COURSE_PARTICIPANT_UUID_PARAM+", user_uuid=:"+USER_UUID_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+", payed = :"+PAYED_PARAM+" WHERE uuid=:"+UUID_PARAM;
	private static final String UPDATE_PAYED = "UPDATE course_application SET modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+", payed = :"+PAYED_PARAM+" WHERE uuid=:"+UUID_PARAM;
	private static final String UPDATE_NOTIFIED_PAYMENT_SEMESTER1 = "UPDATE course_course_participant SET notified_semester_1_payment_at = :notifiedAt where course_participant_uuid IN ( :uuids ) ";
	private static final String UPDATE_NOTIFIED_PAYMENT_SEMESTER2 = "UPDATE course_course_participant SET notified_semester_2_payment_at = :notifiedAt where course_participant_uuid IN ( :uuids ) ";
	private static final String UPDATE_COURSE_PARTIC_INTERRUPTED_AT = "UPDATE course_course_participant SET course_partic_interrupted_at = :course_partic_interrupted_at where uuid IN ( :uuids ) ";
	
	@Autowired
	private CourseParticipantDao courseParticipantDao;

	@Autowired
	private ScbUserDao scbUserDao;

	@Autowired
	public CourseApplicationDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public void insert(CourseApplication courseApplication) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(courseApplication, paramMap);
		paramMap.addValue(YEAR_FROM_PARAM, courseApplication.getYearFrom());
		paramMap.addValue(YEAR_TO_PARAM, courseApplication.getYearTo());
		paramMap.addValue(COURSE_PARTICIPANT_UUID_PARAM, courseApplication.getCourseParticipant().getUuid() != null ? courseApplication.getCourseParticipant().getUuid().toString() : "");
		paramMap.addValue(USER_UUID_PARAM, courseApplication.getCourseParticRepresentative().getUuid() != null ? courseApplication.getCourseParticRepresentative().getUuid().toString() : "");

		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public void update(CourseApplication courseApplication) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(courseApplication, paramMap);
		paramMap.addValue(YEAR_FROM_PARAM, courseApplication.getYearFrom());
		paramMap.addValue(YEAR_TO_PARAM, courseApplication.getYearTo());
		paramMap.addValue(COURSE_PARTICIPANT_UUID_PARAM, courseApplication.getCourseParticipant().getUuid() != null ? courseApplication.getCourseParticipant().getUuid().toString() : "");
		paramMap.addValue(USER_UUID_PARAM, courseApplication.getCourseParticRepresentative().getUuid() != null ? courseApplication.getCourseParticRepresentative().getUuid().toString() : "");
		paramMap.addValue(PAYED_PARAM, courseApplication.isPayed() ? "1" : "0");

		namedJdbcTemplate.update(UPDATE, paramMap);
	}

	@Override
	public void updateNotifiedPayment(List<UUID> courseParticUuidList, Date notifiedAt, boolean firstSemester) {
		List<String> uuidList = new ArrayList<>();
		for (UUID item : courseParticUuidList) {
			uuidList.add(item.toString());
		}
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("uuids", uuidList);
		paramMap.addValue("notifiedAt", notifiedAt);
		
		if (firstSemester) {
			namedJdbcTemplate.update(UPDATE_NOTIFIED_PAYMENT_SEMESTER1, paramMap);			
		} else  {
			namedJdbcTemplate.update(UPDATE_NOTIFIED_PAYMENT_SEMESTER2, paramMap);			
		}
	}
	
	@Override
	public void updateCourseParticInterruption(List<UUID> courseCourseParticUuidList, Date interrupetdAt) {
		List<String> uuidList = new ArrayList<>();
		for (UUID item : courseCourseParticUuidList) {
			uuidList.add(item.toString());
		}
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("uuids", uuidList);
		paramMap.addValue("course_partic_interrupted_at", interrupetdAt);
		namedJdbcTemplate.update(UPDATE_COURSE_PARTIC_INTERRUPTED_AT, paramMap);
	}
	
	@Override
	public void updatePayed(CourseApplication courseApplication, boolean payed) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(courseApplication, paramMap);

		paramMap.addValue(COURSE_PARTICIPANT_UUID_PARAM, courseApplication.getCourseParticipant().getUuid() != null ? courseApplication.getCourseParticipant().getUuid().toString() : "");
		paramMap.addValue(PAYED_PARAM, payed ? "1" : "0");

		namedJdbcTemplate.update(UPDATE_PAYED, paramMap);
	}

	@Override
	public void delete(CourseApplication courseApplication) {
		courseParticipantDao.delete(courseApplication.getCourseParticipant());
		//scbUserDao.delete(courseApplication.getCourseParticRepresentative());
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, courseApplication.getUuid().toString()));
	}

	@Override
	public List<CourseApplication> getAll(int yearFrom, int yearTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(YEAR_FROM_PARAM, yearFrom).addValue(YEAR_TO_PARAM, yearTo);
		return namedJdbcTemplate.query(SELECT_ALL, paramMap, new CourseApplicationRowMapper(courseDao, false));
	}
	
	@Override
	public List<CourseApplication> getUnregisteredToCurrYear(int yearFromPrev, int yearToPrev) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(YEAR_FROM_PARAM, yearFromPrev).addValue(YEAR_TO_PARAM, yearToPrev);
		return namedJdbcTemplate.query(SELECT_UNREGISTERED_TO_CURRENT_YEAR, paramMap, new CourseApplicationRowMapper(courseDao, false));
	}

	@Override
	public List<CourseApplication> getNotInCourse(UUID courseUuid, int yearFrom, int yearTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString()).addValue(YEAR_FROM_PARAM, yearFrom).addValue(YEAR_TO_PARAM, yearTo);
		return namedJdbcTemplate.query(SELECT_NOT_IN_COURSE, paramMap, new CourseApplicationRowMapper(courseDao, false));
	}

	@Override
	public List<CourseApplication> getInCourse(UUID courseUuid, int yearFrom, int yearTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString()).addValue(YEAR_FROM_PARAM, yearFrom).addValue(YEAR_TO_PARAM, yearTo);
		return namedJdbcTemplate.query(SELECT_IN_COURSE, paramMap, new CourseApplicationRowMapper(courseDao, false));
	}

	@Override
	public List<CourseApplication> getAssignedToCourse(int yearFrom, int yearTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(YEAR_FROM_PARAM, yearFrom).addValue(YEAR_TO_PARAM, yearTo);
		
		// vsechny prihlasky za dany rok
		List<CourseApplication> courseApplicationAllByYear = namedJdbcTemplate.query(SELECT_COURSE_APPLICATION_BY_YEAR_FROM_TO_MIN, paramMap, new CourseApplicationMinRowMapper());
		
		if (CollectionUtils.isEmpty(courseApplicationAllByYear)) {
			return Collections.emptyList();
		}
		
		// data z course_course_particiopant a payment za dany rok
		List<CourseParticipant> courseCourseParticipantAllByYear = namedJdbcTemplate.query(SELECT_COURSE_COURSE_PARTICIPANT_BY_YEAR_FROM_TO, paramMap, new CourseParticipantRowMapper());
		
		if (CollectionUtils.isEmpty(courseCourseParticipantAllByYear)) {
			return Collections.emptyList();
		}
		
		// spojit do vysledneho seznamu obsahujiciho ucastniky zarazene do kurzu v danem roce
		List<CourseApplication> ret = new  ArrayList<>();
//		List<CourseParticipant> courseParticipantInCourseList = null;
//		CourseParticipant courseParticipant = null;
		
		CourseApplication courseApplication = null;
		for (CourseParticipant courseParticInCourse : courseCourseParticipantAllByYear) {
			courseApplication = findByCourseParticipantUuid(courseParticInCourse.getUuid(), courseApplicationAllByYear);
			if (courseApplication != null) {
				courseParticInCourse.setContact(courseApplication.getCourseParticipant().getContact());
				courseApplication.setCourseParticipant(courseParticInCourse);
				ret.add(courseApplication);
			}
		}
		
//		for (CourseApplication item : courseApplicationAllByYear) {
//			courseParticipantInCourseList = findCourseParticipantByUuid(item.getCourseParticipant().getUuid(), courseCourseParticipantAllByYear);
//			if (!CollectionUtils.isEmpty(courseParticipantInCourseList)) {
//				for (CourseParticipant courseParticInCourse : courseParticipantInCourseList) {
//					courseParticipant = new CourseParticipant();
//					// spojeni dat z item a courseParticInCourse
//					courseParticipant.setContact(item.getCourseParticipant().getContact());
//					courseParticipant.setUuid(courseParticInCourse.getUuid());
//					courseParticipant.setVarsymbolCore(courseParticInCourse.getVarsymbolCore());
//					courseParticipant.setNotifiedSemester1PaymentAt(courseParticInCourse.getNotifiedSemester1PaymentAt());
//					courseParticipant.setNotifiedSemester2PaymentAt(courseParticInCourse.getNotifiedSemester2PaymentAt());
//					courseParticipant.setCourseUuid(courseParticInCourse.getCourseUuid()); 
//					courseParticipant.setCourseName(courseParticInCourse.getCourseName()); 
//					
//					// payment sum
//					if (courseParticInCourse.getCoursePaymentVO() != null) {
//						courseParticipant.setCoursePaymentVO(new CoursePaymentVO(courseParticInCourse.getCoursePaymentVO().getPaymentSum(), courseParticInCourse.getCoursePaymentVO().getPriceFirstSemester(), courseParticInCourse.getCoursePaymentVO().getPriceSecondSemester()));
//					}
//					
//					item.setCourseParticipant(courseParticipant);
//					ret.add(item);					
//				}
//			}
//		}
		
		return ret;		
	}
	
	/**
	 * Vrati ucastnika zarazeho do kurzu, vraci list, protoze jeden muze byt ve vice kurzech.
	 * @param uuid
	 * @param courseParticipantList
	 * @return
	 */
	private CourseApplication findByCourseParticipantUuid(UUID courseParticipantUuid, List<CourseApplication> courseApplicationList) {
		if (CollectionUtils.isEmpty(courseApplicationList)) {
			return null;
		}
		
		for (CourseApplication item : courseApplicationList) {
			if (item.getCourseParticipant().getUuid().toString().equals(courseParticipantUuid.toString())) {
				CourseApplication ret = new CourseApplication();
				ret.setUuid(item.getUuid());
				ret.setModifAt(item.getModifAt());
				ret.setModifBy(item.getModifBy());
				ret.setYearFrom(item.getYearFrom());
				ret.setYearTo(item.getYearTo());
				ret.setCourseParticipant(item.getCourseParticipant());
				ret.setCourseParticRepresentative(item.getCourseParticRepresentative());
				return ret;
			}
		}
		
		return null;
	}
	
//	/**
//	 * Vrati ucastnika zarazeho do kurzu, vraci list, protoze jeden muze byt ve vice kurzech.
//	 * @param uuid
//	 * @param courseParticipantList
//	 * @return
//	 */
//	private List<CourseParticipant> findCourseParticipantByUuid(UUID uuid, List<CourseParticipant> courseParticipantList) {
//		if (uuid == null || CollectionUtils.isEmpty(courseParticipantList)) {
//			return Collections.emptyList();
//		}
//		
//		List<CourseParticipant> ret = new ArrayList<>();
//		for (CourseParticipant item : courseParticipantList) {
//			if (item.getUuid().toString().equals(uuid.toString())) {
//				ret.add(item);
//			}
//		}
//		
//		return ret;
//	}
	
	@Override
	public List<CourseApplication> getByCourseParticipantUuid(UUID courseParticipantUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_PARTICIPANT_UUID_PARAM, courseParticipantUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_COURSE_PARTICIPANT_UUID, paramMap, new CourseApplicationRowMapper(courseDao, false));
	}

	@Override
	public CourseApplication getByUuid(UUID uuid, boolean deep) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new CourseApplicationRowMapperDetail(courseParticipantDao, scbUserDao));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public static final class CourseApplicationMinRowMapper implements RowMapper<CourseApplication> {

		@Override
		public CourseApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseApplication ret = new CourseApplication();
			fetchIdentEntity(rs, ret);
			
			ret.setYearFrom(rs.getInt("year_from"));
			ret.setYearTo(rs.getInt("year_to"));
			
			CourseParticipant courseParticipant = new CourseParticipant();
			courseParticipant.setUuid(UUID.fromString(rs.getString("participant_uuid")));

			Contact courseParticipantContact = new Contact();
			courseParticipantContact.setFirstname(rs.getString("firstname"));
			courseParticipantContact.setSurname(rs.getString("surname"));
			courseParticipantContact.setCity(rs.getString("city"));
			courseParticipantContact.setStreet(rs.getString("street"));
			courseParticipantContact.setLandRegistryNumber(rs.getLong("land_registry_number"));
			courseParticipantContact.setHouseNumber(rs.getShort("house_number"));
			courseParticipantContact.setZipCode(rs.getString("zip_code"));
			courseParticipant.setContact(courseParticipantContact);

			ret.setCourseParticipant(courseParticipant);

			ScbUser courseParticRepresentative = new ScbUser();
			Contact courseParticRepresentativeContact = new Contact();
			courseParticRepresentativeContact.setEmail1(rs.getString("email1"));
			courseParticRepresentativeContact.setPhone1(rs.getString("phone1"));
			courseParticRepresentative.setContact(courseParticRepresentativeContact);
			courseParticRepresentative.setUuid(UUID.fromString(rs.getString("representative_uuid")));
			ret.setCourseParticRepresentative(courseParticRepresentative);
			
			return ret;
		}
	}
	
	public static final class CourseParticipantRowMapper implements RowMapper<CourseParticipant> {

		@Override
		public CourseParticipant mapRow(ResultSet rs, int rowNum) throws SQLException {			
			
			CourseParticipant courseParticipant = new CourseParticipant();
			courseParticipant.setUuid(UUID.fromString(rs.getString("participant_uuid")));
			
			courseParticipant.setVarsymbolCore(rs.getInt("varsymbol_core"));
			courseParticipant.setNotifiedSemester1PaymentAt(rs.getTimestamp("notified_semester_1_payment_at"));
			courseParticipant.setNotifiedSemester2PaymentAt(rs.getTimestamp("notified_semester_2_payment_at"));
			courseParticipant.setCourseParticipationInterruptedAt(rs.getTimestamp("course_partic_interrupted_at"));
			 String courseCourseParticipantUuid = rs.getString("COURSE_COURSE_PARTICIPANT_UUID");
			 String courseNameCourseParticipantUuid = rs.getString("COURSE_NAME_COURSE_PARTICIPANT_UUID");				
			 if (StringUtils.hasText(courseCourseParticipantUuid)) {
				courseParticipant.setCourseUuid(UUID.fromString(courseCourseParticipantUuid)); 
			}
			if (StringUtils.hasText(courseNameCourseParticipantUuid)) {
				courseParticipant.setCourseName(courseNameCourseParticipantUuid); 
			}
			
			// payment sum
			long paymentSum = rs.getLong("PAYMENT_SUM");
			long priceSemester1 = rs.getLong("COURSE_PRICE_SEMESTER_1");
			long priceSemester2 = rs.getLong("COURSE_PRICE_SEMESTER_2");
			courseParticipant.setCoursePaymentVO(new CoursePaymentVO(paymentSum, priceSemester1, priceSemester2));

			
			return courseParticipant;
		}
	}
	
	public static final class CourseApplicationRowMapper implements RowMapper<CourseApplication> {
		private CourseDao courseDao;
		private boolean extended;

		public CourseApplicationRowMapper(CourseDao courseDao, boolean extended) {
			this.courseDao = courseDao;
			this.extended = extended;
		}

		@Override
		public CourseApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseApplication ret = new CourseApplication();
			fetchIdentEntity(rs, ret);
			
			ret.setYearFrom(rs.getInt("year_from"));
			ret.setYearTo(rs.getInt("year_to"));
			
			CourseParticipant courseParticipant = new CourseParticipant();
			courseParticipant.setUuid(UUID.fromString(rs.getString("participant_uuid")));
			courseParticipant.setBirthdate(rs.getDate("birthdate"));
			courseParticipant.setPersonalNo(rs.getString("personal_number"));
			courseParticipant.setCourseList(courseDao.getByCourseParticipantUuid(courseParticipant.getUuid(), ret.getYearFrom(), ret.getYearTo()));
			if (this.extended) {
				String courseCourseParticipantUuid = rs.getString("COURSE_COURSE_PARTICIPANT_UUID");
				String courseNameCourseParticipantUuid = rs.getString("COURSE_NAME_COURSE_PARTICIPANT_UUID");				
				if (StringUtils.hasText(courseCourseParticipantUuid)) {
					courseParticipant.setCourseUuid(UUID.fromString(courseCourseParticipantUuid)); 
				}
				if (StringUtils.hasText(courseNameCourseParticipantUuid)) {
					courseParticipant.setCourseName(courseNameCourseParticipantUuid); 
				}
				// payment sum
				long paymentSum = rs.getLong("PAYMENT_SUM");
				long priceSemester1 = rs.getLong("COURSE_PRICE_SEMESTER_1");
				long priceSemester2 = rs.getLong("COURSE_PRICE_SEMESTER_2");
				courseParticipant.setCoursePaymentVO(new CoursePaymentVO(paymentSum, priceSemester1, priceSemester2));
			}

			Contact courseParticipantContact = new Contact();
			courseParticipantContact.setFirstname(rs.getString("firstname"));
			courseParticipantContact.setSurname(rs.getString("surname"));
			courseParticipantContact.setCity(rs.getString("city"));
			courseParticipantContact.setStreet(rs.getString("street"));
			courseParticipantContact.setLandRegistryNumber(rs.getLong("land_registry_number"));
			courseParticipantContact.setHouseNumber(rs.getShort("house_number"));
			courseParticipantContact.setZipCode(rs.getString("zip_code"));
			courseParticipant.setContact(courseParticipantContact);

			ret.setCourseParticipant(courseParticipant);

			ScbUser courseParticRepresentative = new ScbUser();
			Contact courseParticRepresentativeContact = new Contact();
			courseParticRepresentativeContact.setFirstname(rs.getString("representative_firstname"));
			courseParticRepresentativeContact.setSurname(rs.getString("representative_surname"));
			courseParticRepresentativeContact.setPhone1(rs.getString("phone1"));
			courseParticRepresentativeContact.setEmail1(rs.getString("email1"));
			courseParticRepresentativeContact.setPhone2(rs.getString("phone2"));
			courseParticRepresentativeContact.setEmail2(rs.getString("email2"));
			courseParticRepresentative.setContact(courseParticRepresentativeContact);
			courseParticRepresentative.setUuid(UUID.fromString(rs.getString("representative_uuid")));

			ret.setCourseParticRepresentative(courseParticRepresentative);

			ret.setPayed(rs.getInt("payed") == 1);
			ret.setCurrentParticipant(rs.getInt("current_participant") == 1);
			
			return ret;
		}
	}

	public static final class CourseApplicationRowMapperDetail implements RowMapper<CourseApplication> {
		private CourseParticipantDao courseParticipantDao;
		private ScbUserDao scbUserDao;

		public CourseApplicationRowMapperDetail(CourseParticipantDao courseParticipantDao, ScbUserDao scbUserDao) {
			this.courseParticipantDao = courseParticipantDao;
			this.scbUserDao = scbUserDao;
		}

		@Override
		public CourseApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseApplication ret = new CourseApplication();
			fetchIdentEntity(rs, ret);
			ret.setYearFrom(rs.getInt("year_from"));
			ret.setYearTo(rs.getInt("year_to"));

			UUID courseParticipantUuid = rs.getString("course_participant_uuid") != null ? UUID.fromString(rs.getString("course_participant_uuid")) : null;
			if (courseParticipantUuid != null) {
				ret.setCourseParticipant(courseParticipantDao.getByUuid(courseParticipantUuid, true));
			}

			new ScbUser();
			UUID courseParticipantrepresentativeUuid = rs.getString("user_uuid") != null ? UUID.fromString(rs.getString("user_uuid")) : null;
			if (courseParticipantrepresentativeUuid != null) {
				ret.setCourseParticRepresentative(scbUserDao.getByUuid(courseParticipantrepresentativeUuid));
			}
			ret.setPayed(rs.getInt("payed") == 1);

			return ret;
		}
	}
}