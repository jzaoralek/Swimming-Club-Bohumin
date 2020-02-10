package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.ContactDao;
import com.jzaoralek.scb.dataservice.dao.CourseDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.domain.CourseCourseParticipantVO;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant.IscusRole;

@Repository
public class CourseParticipantDaoImpl extends BaseJdbcDao implements CourseParticipantDao {

	private static final String BIRTHDATE_PARAM = "birthdate";
	private static final String PERSONAL_NUMBER_PARAM = "personal_number";
	private static final String VARSYMBOL_CORE_PARAM = "varsymbol_core";
	private static final String HEALTH_INSURANCE = "health_insurance";
	private static final String CONTACT_PARAM = "contact_uuid";
	private static final String HEALTH_INFO_PARAM = "health_info";
	private static final String COURSE_PARTICIPANT_UUID_PARAM = "course_participant_uuid";
	private static final String LEARNING_LESSON_UUID_PARAM = "learning_lesson_uuid";
	private static final String ISCUS_ROLE_PARAM = "iscus_role";
	private static final String ISCUS_PARTIC_ID_PARAM = "iscus_partic_id";
	private static final String ISCUS_SYSTEM_ID_PARAM = "iscus_system_id";
	
	private static final String INSERT = "INSERT INTO course_participant " +
			"(uuid, birthdate, personal_number, health_insurance, contact_uuid, health_info, modif_at, modif_by, user_uuid, iscus_role, iscus_partic_id, iscus_system_id) " +
			" VALUES (:"+UUID_PARAM+", :"+BIRTHDATE_PARAM+", :"+PERSONAL_NUMBER_PARAM+", :"+HEALTH_INSURANCE+", :"+CONTACT_PARAM+", :"+HEALTH_INFO_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+", :"+USER_UUID_PARAM+", :"+ISCUS_ROLE_PARAM+", :"+ISCUS_PARTIC_ID_PARAM+", :"+ISCUS_SYSTEM_ID_PARAM+")";
	private static final String SELECT_BY_UUID = "SELECT uuid, birthdate, personal_number, health_insurance, contact_uuid, health_info, modif_at, modif_by, user_uuid, iscus_role, iscus_partic_id, iscus_system_id FROM course_participant WHERE uuid= :"+UUID_PARAM;

	private static final String SELECT_BY_COURSE_UUID = "SELECT cp.uuid, cp.birthdate, cp.personal_number, cp.health_insurance, cp.contact_uuid, cp.health_info, cp.modif_at, cp.modif_by, cp.user_uuid, cp.iscus_role, cp.iscus_partic_id, cp.iscus_system_id FROM course_participant cp, course_course_participant ccp, contact c "
			+ "WHERE cp.uuid = ccp.course_participant_uuid "
			+ "AND cp.contact_uuid = c.uuid "
			+ "AND ccp.course_uuid = :"+COURSE_UUID_PARAM+" "
			+ "AND ccp.course_partic_interrupted_at is null "
			+ "ORDER BY c.surname ";
	
	private static final String SELECT_BY_COURSE_UUID_INTERRUPED = "SELECT cp.uuid, cp.birthdate, cp.personal_number, cp.health_insurance, cp.contact_uuid, cp.health_info, cp.modif_at, cp.modif_by, cp.user_uuid, cp.iscus_role, cp.iscus_partic_id, cp.iscus_system_id FROM course_participant cp, course_course_participant ccp, contact c "
			+ "WHERE cp.uuid = ccp.course_participant_uuid "
			+ "AND cp.contact_uuid = c.uuid "
			+ "AND ccp.course_uuid = :"+COURSE_UUID_PARAM+" "
			+ "AND ccp.course_partic_interrupted_at is not null "
			+ "ORDER BY c.surname ";
	
	private static final String SELECT_BY_COURSE_INCLUDE_INTERRUPTED_UUID = "SELECT cp.uuid, cp.birthdate, cp.personal_number, cp.health_insurance, cp.contact_uuid, cp.health_info, cp.modif_at, cp.modif_by, cp.user_uuid, cp.iscus_role, cp.iscus_partic_id, cp.iscus_system_id FROM course_participant cp, course_course_participant ccp, contact c "
			+ "WHERE cp.uuid = ccp.course_participant_uuid "
			+ "AND cp.contact_uuid = c.uuid "
			+ "AND ccp.course_uuid = :"+COURSE_UUID_PARAM+" "
			+ "ORDER BY c.surname ";

	private static final String SELECT_BY_LEARNING_LESSON_UUID = "SELECT cp.uuid, cp.birthdate, cp.personal_number, cp.health_insurance, cp.contact_uuid, cp.health_info, cp.modif_at, cp.modif_by, cp.user_uuid, cp.iscus_role, cp.iscus_partic_id, cp.iscus_system_id FROM course_participant cp, participant_learning_lesson cpl "
			+ "WHERE cp.uuid = cpl.course_participant_uuid "
			+ "AND cpl.learning_lesson_uuid = :" + LEARNING_LESSON_UUID_PARAM;
	
	private static final String SELECT_BY_USERID = "SELECT cp.uuid, cp.birthdate, cp.personal_number, cp.health_insurance, cp.contact_uuid, cp.health_info, cp.modif_at, cp.modif_by, cp.user_uuid, cp.iscus_role, cp.iscus_partic_id, cp.iscus_system_id  "
			+ " FROM course_participant cp , contact c "
			+ " WHERE cp.contact_uuid = c.uuid AND cp.user_uuid=:" + USER_UUID_PARAM;
	
	private static final String SELECT_BY_PERSONAL_NUMBER = "SELECT cp.uuid, cp.birthdate, cp.personal_number, cp.health_insurance, cp.contact_uuid, cp.health_info, cp.modif_at, cp.modif_by, cp.user_uuid, cp.iscus_role, cp.iscus_partic_id, cp.iscus_system_id  "
			+ " FROM course_participant cp "
			+ " WHERE cp.personal_number=:" + PERSONAL_NUMBER_PARAM;
	
	private static final String INSERT_PARTIC_LEARNING_LESSON = "INSERT INTO participant_learning_lesson (course_participant_uuid, learning_lesson_uuid) VALUES (:"+COURSE_PARTICIPANT_UUID_PARAM+", :"+LEARNING_LESSON_UUID_PARAM+")";
	private static final String DELETE_ALL_FROM_LEARNING_LESSON = "DELETE FROM participant_learning_lesson WHERE learning_lesson_uuid = :"+LEARNING_LESSON_UUID_PARAM;

	private static final String DELETE_ALL_FROM_COURSE = "DELETE from course_course_participant WHERE course_uuid = :"+COURSE_UUID_PARAM;
	private static final String DELETE_PARTICIPANT_FROM_COURSE = "DELETE from course_course_participant WHERE course_uuid = :"+COURSE_UUID_PARAM+" AND course_participant_uuid = :"+COURSE_PARTICIPANT_UUID_PARAM;

	private static final String INSERT_COURSE_COURSE_PARTICIPANT = "INSERT INTO course_course_participant " +
			"(uuid, course_participant_uuid, course_uuid) " +
			" VALUES (:"+UUID_PARAM+", :"+COURSE_PARTICIPANT_UUID_PARAM+", :"+COURSE_UUID_PARAM+")";

	private static final String DELETE = "DELETE FROM course_participant where uuid = :" + UUID_PARAM;
	private static final String UPDATE = "UPDATE course_participant SET birthdate=:"+BIRTHDATE_PARAM+", personal_number=:"+PERSONAL_NUMBER_PARAM+", health_insurance=:"+HEALTH_INSURANCE+
			", contact_uuid=:"+CONTACT_PARAM+", health_info=:"+HEALTH_INFO_PARAM + ", iscus_role=:"+ISCUS_ROLE_PARAM + ", iscus_partic_id=:"+ISCUS_PARTIC_ID_PARAM + ", iscus_system_id=:"+ISCUS_SYSTEM_ID_PARAM +
			", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+", user_uuid=:"+USER_UUID_PARAM
			+" WHERE uuid=:"+UUID_PARAM;
	private static final String SELECT_COURSE_COURSE_PARTIC_BY_UUID = "select uuid, course_participant_uuid, course_uuid from course_course_participant where uuid = :"+UUID_PARAM;
	private static final String SELECT_COURSE_COURSE_PARTIC_BY_PARTIC_AND_COURSE_INTERRUPTED = "select * from course_course_participant where course_participant_uuid = :"+COURSE_PARTICIPANT_UUID_PARAM+" AND course_uuid = :"+COURSE_UUID_PARAM+" AND course_partic_interrupted_at IS NOT NULL";
	private static final String SELECT_COURSE_COURSE_PARTIC_BY_PARTIC_AND_COURSE_NOT_INTERRUPED = "select * from course_course_participant where course_participant_uuid = :"+COURSE_PARTICIPANT_UUID_PARAM+" AND course_uuid = :"+COURSE_UUID_PARAM+" AND course_partic_interrupted_at IS NULL";
	
	private static final String SELECT_BY_PERSONAL_NO_AND_INTERVAL = "SELECT cp.*, ccp.* FROM course_participant cp, course_course_participant ccp, course c " + 
			" WHERE cp.uuid = ccp.course_participant_uuid AND ccp.course_uuid = c.uuid " +
//			" AND TRIM(LEADING '0' FROM REPLACE(cp.personal_number,'/','')) =:" + PERSONAL_NUMBER_PARAM +
			" AND TRIM(LEADING '0' FROM ccp.varsymbol_core) =:" + VARSYMBOL_CORE_PARAM +
			" AND c.year_from = :"+DATE_FROM_PARAM+" AND c.year_to =:"+DATE_TO_PARAM;
	
	@Autowired
	private ContactDao contactDao;

	@Autowired
	private CourseDao courseDao;
	
	@Autowired
	public CourseParticipantDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public CourseParticipant getByUuid(UUID uuid, boolean deep) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new CourseParticipantRowMapper(contactDao, false));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public CourseParticipant getCourseParticInOneCourse(UUID courseCourseParticUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, courseCourseParticUuid.toString());
		try {
			Pair<UUID, UUID> courseParticCoursePair = namedJdbcTemplate.queryForObject(SELECT_COURSE_COURSE_PARTIC_BY_UUID, paramMap, new CourseCourseParticiRowMapper());
			CourseParticipant coursePartic = getByUuid(courseParticCoursePair.getValue0(), false);
			coursePartic.setCourseList(Arrays.asList(courseDao.getPlainByUuid(courseParticCoursePair.getValue1())));
			return coursePartic;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public boolean existsByPersonalNumber(String personalNumber) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(PERSONAL_NUMBER_PARAM, personalNumber);
		List<CourseParticipant> courseParticipantList = namedJdbcTemplate.query(SELECT_BY_PERSONAL_NUMBER, paramMap, new CourseParticipantRowMapper(contactDao, false));
		return !courseParticipantList.isEmpty();
	}
	
	@Override
	public CourseParticipant getByVarsymbolAndInterval(String varsymbolCore, int yearFrom, int yearTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(VARSYMBOL_CORE_PARAM, varsymbolCore)
				.addValue(DATE_FROM_PARAM, yearFrom)
				.addValue(DATE_TO_PARAM, yearTo);
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_PERSONAL_NO_AND_INTERVAL, paramMap, new CourseParticipantRowMapper(contactDao, true));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void insert(CourseParticipant courseParticipant) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(courseParticipant, paramMap);
		paramMap.addValue(BIRTHDATE_PARAM, courseParticipant.getBirthdate());
		paramMap.addValue(PERSONAL_NUMBER_PARAM, courseParticipant.getPersonalNo());
		paramMap.addValue(HEALTH_INSURANCE, courseParticipant.getHealthInsurance());
		paramMap.addValue(CONTACT_PARAM, courseParticipant.getContact() != null ? courseParticipant.getContact().getUuid().toString() : null);
		paramMap.addValue(HEALTH_INFO_PARAM, courseParticipant.getHealthInfo());
		paramMap.addValue(USER_UUID_PARAM, courseParticipant.getRepresentativeUuid() != null ? courseParticipant.getRepresentativeUuid().toString() : null);
		paramMap.addValue(ISCUS_PARTIC_ID_PARAM, courseParticipant.getIscusParticId());
		paramMap.addValue(ISCUS_SYSTEM_ID_PARAM, courseParticipant.getIscusSystemId());
		paramMap.addValue(ISCUS_ROLE_PARAM, courseParticipant.getIscusRole() != null ? courseParticipant.getIscusRole().name() : null);
		
		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public void update(CourseParticipant courseParticipant) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(courseParticipant, paramMap);
		paramMap.addValue(BIRTHDATE_PARAM, courseParticipant.getBirthdate());
		paramMap.addValue(PERSONAL_NUMBER_PARAM, courseParticipant.getPersonalNo());
		paramMap.addValue(HEALTH_INSURANCE, courseParticipant.getHealthInsurance());
		paramMap.addValue(CONTACT_PARAM, courseParticipant.getContact() != null ? courseParticipant.getContact().getUuid().toString() : null);
		paramMap.addValue(HEALTH_INFO_PARAM, courseParticipant.getHealthInfo());
		paramMap.addValue(USER_UUID_PARAM, courseParticipant.getRepresentativeUuid() != null ? courseParticipant.getRepresentativeUuid().toString() : null);
		paramMap.addValue(ISCUS_PARTIC_ID_PARAM, courseParticipant.getIscusParticId());
		paramMap.addValue(ISCUS_SYSTEM_ID_PARAM, courseParticipant.getIscusSystemId());
		paramMap.addValue(ISCUS_ROLE_PARAM, courseParticipant.getIscusRole() != null ? courseParticipant.getIscusRole().name() : null);
		namedJdbcTemplate.update(UPDATE, paramMap);
	}

	@Override
	public void delete(CourseParticipant courseParticipant) {
		contactDao.delete(courseParticipant.getContact());
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, courseParticipant.getUuid().toString()));

	}

	@Override
	public void deleteAllFromCourse(UUID courseUuid) {
		namedJdbcTemplate.update(DELETE_ALL_FROM_COURSE, new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString()));
	}

	@Override
	public void deleteParticipantFromCourse(UUID participantUuid, UUID courseUuid) {
		namedJdbcTemplate.update(DELETE_PARTICIPANT_FROM_COURSE, new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString()).addValue(COURSE_PARTICIPANT_UUID_PARAM, participantUuid.toString()));
	}

	@Override
	public void insetToCourse(List<CourseParticipant> courseParticipantList, UUID courseUuid) {
		MapSqlParameterSource paramMap = null;
		for (CourseParticipant item : courseParticipantList) {
			paramMap = new MapSqlParameterSource();
			paramMap.addValue(UUID_PARAM, UUID.randomUUID().toString());
			paramMap.addValue(COURSE_UUID_PARAM, courseUuid.toString());
			paramMap.addValue(COURSE_PARTICIPANT_UUID_PARAM, item.getUuid().toString());
			namedJdbcTemplate.update(INSERT_COURSE_COURSE_PARTICIPANT, paramMap);
		}
	}
	
	@Override
	public CourseCourseParticipantVO getCourseCourseParticipantVO(UUID courseParticUuid, UUID courseUuid, boolean interruped) {
		try {
			MapSqlParameterSource paramMap = new MapSqlParameterSource();
			paramMap.addValue(COURSE_PARTICIPANT_UUID_PARAM, courseParticUuid.toString());
			paramMap.addValue(COURSE_UUID_PARAM, courseUuid.toString());
			String sql = interruped ? SELECT_COURSE_COURSE_PARTIC_BY_PARTIC_AND_COURSE_INTERRUPTED : SELECT_COURSE_COURSE_PARTIC_BY_PARTIC_AND_COURSE_NOT_INTERRUPED;
			
			return namedJdbcTemplate.queryForObject(sql, paramMap, new CourseCourseParticipantRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<CourseParticipant> getByCourseUuid(UUID courseUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_COURSE_UUID, paramMap, new CourseParticipantRowMapper(contactDao, false));
	}
	
	@Override
	public List<CourseParticipant> getByCourseUuidInterrupted(UUID courseUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_COURSE_UUID_INTERRUPED, paramMap, new CourseParticipantRowMapper(contactDao, false));
	}
	
	@Override
	public List<CourseParticipant> getByCourseIncInterruptedUuid(UUID courseUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_COURSE_INCLUDE_INTERRUPTED_UUID, paramMap, new CourseParticipantRowMapper(contactDao, false));
	}

	@Override
	public List<CourseParticipant> getByLearningLessonUuid(UUID learningLessonUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(LEARNING_LESSON_UUID_PARAM, learningLessonUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_LEARNING_LESSON_UUID, paramMap, new CourseParticipantRowMapper(contactDao, false));
	}
	
	@Override
	public List<CourseParticipant> getByUserUuid(UUID userUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(USER_UUID_PARAM, userUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_USERID, paramMap, new CourseParticipantRowMapper(contactDao, false));
	}

	@Override
	public void insertToLearningLesson(UUID learningLessonUuid, List<CourseParticipant> participantList) {
		if (CollectionUtils.isEmpty(participantList)) {
			return;
		}
		MapSqlParameterSource paramMap;
		for (CourseParticipant item : participantList) {
			paramMap = new MapSqlParameterSource();
			paramMap.addValue(LEARNING_LESSON_UUID_PARAM, learningLessonUuid.toString());
			paramMap.addValue(COURSE_PARTICIPANT_UUID_PARAM, item.getUuid().toString());
			namedJdbcTemplate.update(INSERT_PARTIC_LEARNING_LESSON, paramMap);
		}
	}

	@Override
	public void deleteAllFromLearningLesson(UUID learningLessonUuid) {
		namedJdbcTemplate.update(DELETE_ALL_FROM_LEARNING_LESSON, new MapSqlParameterSource().addValue(LEARNING_LESSON_UUID_PARAM, learningLessonUuid.toString()));
	}

	public static final class CourseParticipantRowMapper implements RowMapper<CourseParticipant> {
		private ContactDao contactDao;
		/** Indikator urcujici zda-li naplnit atributy z COURSE_COURSE_PARTICIPANT, potreba aby byly atributy soucastni dotazu. */
		private boolean includeCourseCoursePartic;

		public CourseParticipantRowMapper(ContactDao contactDao, boolean includeCourseCoursePartic) {
			this.contactDao = contactDao;
			this.includeCourseCoursePartic = includeCourseCoursePartic;
		}

		@Override
		public CourseParticipant mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseParticipant ret = new CourseParticipant();
			fetchIdentEntity(rs, ret);
			ret.setBirthdate(transDate(rs.getDate("birthdate")));
			ret.setPersonalNo(rs.getString("personal_number"));
			ret.setHealthInsurance(rs.getString("health_insurance"));
			ret.setHealthInfo(rs.getString("health_info"));
			
			String iscusRoleStr = rs.getString("iscus_role");
			if (StringUtils.hasText(iscusRoleStr)) {
				ret.setIscusRole(IscusRole.valueOf(iscusRoleStr));				
			}
			ret.setIscusParticId(rs.getString("iscus_partic_id"));
			ret.setIscusSystemId(rs.getString("iscus_system_id"));
			
			UUID contactUuid = rs.getString("contact_uuid") != null ? UUID.fromString(rs.getString("contact_uuid")) : null;
			if (contactUuid != null) {
				ret.setContact(contactDao.getByUuid(contactUuid));
			}

			ret.setRepresentativeUuid(rs.getString("user_uuid") != null ? UUID.fromString(rs.getString("user_uuid")) : null);
			
			if (includeCourseCoursePartic) {
				ret.setCourseUuid(UUID.fromString(rs.getString("course_uuid")));
				ret.setVarsymbolCore(rs.getInt("varsymbol_core"));
				ret.setNotifiedSemester1PaymentAt(rs.getTimestamp("notified_semester_1_payment_at"));
				ret.setNotifiedSemester2PaymentAt(rs.getTimestamp("notified_semester_2_payment_at"));
				ret.setCourseParticipationInterruptedAt(rs.getTimestamp("course_partic_interrupted_at"));				
			}
			
			return ret;
		}
	}
	
	public static final class CourseCourseParticiRowMapper implements RowMapper<Pair<UUID, UUID>> {

		@Override
		public Pair<UUID, UUID> mapRow(ResultSet rs, int rowNum) throws SQLException {
			UUID courseParticUuid = UUID.fromString(rs.getString("course_participant_uuid"));
			UUID courseUuid = UUID.fromString(rs.getString("course_uuid"));
			
			return new Pair<>(courseParticUuid, courseUuid);
		}
	}
	
	public static final class CourseCourseParticipantRowMapper implements RowMapper<CourseCourseParticipantVO> {

		@Override
		public CourseCourseParticipantVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseCourseParticipantVO ret = new CourseCourseParticipantVO();
			ret.setUuid(UUID.fromString(rs.getString("uuid")));
			ret.setCourseParticUuid(UUID.fromString(rs.getString("course_participant_uuid")));
			ret.setCourseUuid(UUID.fromString(rs.getString("course_uuid")));
			ret.setVarsymbolCore(rs.getInt("varsymbol_core"));
			ret.setNotifiedSemester1PaymentAt(rs.getTimestamp("notified_semester_1_payment_at"));
			ret.setNotifiedSemester2PaymentAt(rs.getTimestamp("notified_semester_2_payment_at"));
			ret.setCourseParticipationInterruptedAt(rs.getTimestamp("course_partic_interrupted_at"));
			
			return ret;
		}
	}
}
