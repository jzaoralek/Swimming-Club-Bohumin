package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.jzaoralek.scb.dataservice.dao.ContactDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;

@Repository
public class CourseParticipantDaoImpl extends BaseJdbcDao implements CourseParticipantDao {

	private static final String BIRTHDATE_PARAM = "birthdate";
	private static final String PERSONAL_NUMBER_PARAM = "personal_number";
	private static final String HEALTH_INSURANCE = "health_insurance";
	private static final String CONTACT_PARAM = "contact_uuid";
	private static final String HEALTH_INFO_PARAM = "health_info";
	private static final String COURSE_PARTICIPANT_UUID_PARAM = "course_participant_uuid";
	private static final String COURSE_UUID_PARAM = "course_uuid";
	private static final String LEARNING_LESSON_UUID_PARAM = "learning_lesson_uuid";

	private static final String INSERT = "INSERT INTO course_participant " +
			"(uuid, birthdate, personal_number, health_insurance, contact_uuid, health_info, modif_at, modif_by, user_uuid) " +
			" VALUES (:"+UUID_PARAM+", :"+BIRTHDATE_PARAM+", :"+PERSONAL_NUMBER_PARAM+", :"+HEALTH_INSURANCE+", :"+CONTACT_PARAM+", :"+HEALTH_INFO_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+", :"+USER_UUID_PARAM+")";
	private static final String SELECT_BY_UUID = "SELECT uuid, birthdate, personal_number, health_insurance, contact_uuid, health_info, modif_at, modif_by, user_uuid FROM course_participant WHERE uuid= :"+UUID_PARAM;

	private static final String SELECT_BY_COURSE_UUID = "SELECT cp.uuid, cp.birthdate, cp.personal_number, cp.health_insurance, cp.contact_uuid, cp.health_info, cp.modif_at, cp.modif_by, cp.user_uuid, ccp.uuid \"COURSE_COURSE_PARTICIPANT_UUID\" FROM course_participant cp, course_course_participant ccp, contact c "
			+ "WHERE cp.uuid = ccp.course_participant_uuid "
			+ "AND cp.contact_uuid = c.uuid "
			+ "AND ccp.course_uuid = :"+COURSE_UUID_PARAM+" "
			+ "ORDER BY c.surname ";

	private static final String SELECT_BY_LEARNING_LESSON_UUID = "SELECT cp.uuid, cp.birthdate, cp.personal_number, cp.health_insurance, cp.contact_uuid, cp.health_info, cp.modif_at, cp.modif_by, cp.user_uuid FROM course_participant cp, participant_learning_lesson cpl "
			+ "WHERE cp.uuid = cpl.course_participant_uuid "
			+ "AND cpl.learning_lesson_uuid = :" + LEARNING_LESSON_UUID_PARAM;
	
	private static final String SELECT_BY_USERID = "SELECT cp.uuid, cp.birthdate, cp.personal_number, cp.health_insurance, cp.contact_uuid, cp.health_info, cp.modif_at, cp.modif_by, cp.user_uuid  "
			+ " FROM course_participant cp , contact c "
			+ " WHERE cp.contact_uuid = c.uuid AND cp.user_uuid=:" + USER_UUID_PARAM;
	
	private static final String SELECT_BY_PERSONAL_NUMBER = "SELECT cp.uuid, cp.birthdate, cp.personal_number, cp.health_insurance, cp.contact_uuid, cp.health_info, cp.modif_at, cp.modif_by, cp.user_uuid  "
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
	private static final String UPDATE = "UPDATE course_participant SET birthdate=:"+BIRTHDATE_PARAM+", personal_number=:"+PERSONAL_NUMBER_PARAM+", health_insurance=:"+HEALTH_INSURANCE+", contact_uuid=:"+CONTACT_PARAM+", health_info=:"+HEALTH_INFO_PARAM + ", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+", user_uuid=:"+USER_UUID_PARAM+" WHERE uuid=:"+UUID_PARAM;


	@Autowired
	private ContactDao contactDao;

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
	public boolean existsByPersonalNumber(String personalNumber) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(PERSONAL_NUMBER_PARAM, personalNumber);
		List<CourseParticipant> courseParticipantList = namedJdbcTemplate.query(SELECT_BY_PERSONAL_NUMBER, paramMap, new CourseParticipantRowMapper(contactDao, false));
		return !courseParticipantList.isEmpty();
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
	public List<CourseParticipant> getByCourseUuid(UUID courseUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_COURSE_UUID, paramMap, new CourseParticipantRowMapper(contactDao, true));
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
		// additional columns which are not included in all selects
		private boolean extended;

		public CourseParticipantRowMapper(ContactDao contactDao, boolean extended) {
			this.contactDao = contactDao;
			this.extended = extended;
		}

		@Override
		public CourseParticipant mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseParticipant ret = new CourseParticipant();
			fetchIdentEntity(rs, ret);
			ret.setBirthdate(transDate(rs.getDate("birthdate")));
			ret.setPersonalNo(rs.getString("personal_number"));
			ret.setHealthInsurance(rs.getString("health_insurance"));
			ret.setHealthInfo(rs.getString("health_info"));
			if (extended) {
				String courseCourseParticipantUuid = rs.getString("COURSE_COURSE_PARTICIPANT_UUID");
				if (StringUtils.hasText(courseCourseParticipantUuid)) {
					ret.setCourseCourseParticipantUuid(UUID.fromString(courseCourseParticipantUuid));				
				}				
			}

			UUID contactUuid = rs.getString("contact_uuid") != null ? UUID.fromString(rs.getString("contact_uuid")) : null;
			if (contactUuid != null) {
				ret.setContact(contactDao.getByUuid(contactUuid));
			}

			ret.setRepresentativeUuid(rs.getString("user_uuid") != null ? UUID.fromString(rs.getString("user_uuid")) : null);
			
			return ret;
		}
	}
}
