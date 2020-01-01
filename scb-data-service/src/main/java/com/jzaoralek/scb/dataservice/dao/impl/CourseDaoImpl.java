package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.CourseDao;
import com.jzaoralek.scb.dataservice.dao.CourseLocationDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.dao.LessonDao;
import com.jzaoralek.scb.dataservice.dao.impl.ContactDaoImpl.ContactRowMapper;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;

@Repository
public class CourseDaoImpl extends BaseJdbcDao implements CourseDao {

	@Autowired
	private CourseParticipantDao courseParticipantDao;

	@Autowired
	private LessonDao lessonDao;
	
	@Autowired
	private CourseLocationDao courseLocationDao;


	private static final String NAME_PARAM = "NAME";
	private static final String PRICE_SEMESTER_1_PARAM = "PRICE_SEMESTER_1";
	private static final String PRICE_SEMESTER_2_PARAM = "PRICE_SEMESTER_2";
	private static final String COURSE_LOCATION_UUID_PARAM = "COURSE_LOCATION_UUID";
	private static final String MAX_PARTIC_COUNT_PARAM = "MAX_PARTIC_COUNT";
	private static final String TYPE_PARAM = "TYPE";
	
	private static final String INSERT = "INSERT INTO course " +
			"(uuid, name, description, year_from, year_to, modif_at, modif_by, price_semester_1, price_semester_2, course_location_uuid, max_participant_count, type) " +
			"VALUES (:"+UUID_PARAM+", :"+NAME_PARAM+", :"+DESCRIPTION_PARAM+", :"+YEAR_FROM_PARAM+", :"+YEAR_TO_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+", :"+PRICE_SEMESTER_1_PARAM+", :"+PRICE_SEMESTER_2_PARAM+", :"+COURSE_LOCATION_UUID_PARAM+", :"+MAX_PARTIC_COUNT_PARAM+", :"+TYPE_PARAM+")";

	private static final String UPDATE = "UPDATE course SET uuid = :"+UUID_PARAM+" , name = :"+NAME_PARAM+", description = :"+DESCRIPTION_PARAM+", year_from = :"+YEAR_FROM_PARAM+", year_to = :"+YEAR_TO_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+", price_semester_1 = :"+PRICE_SEMESTER_1_PARAM+", price_semester_2 = :"+PRICE_SEMESTER_2_PARAM+", course_location_uuid = :"+COURSE_LOCATION_UUID_PARAM+", max_participant_count = :"+MAX_PARTIC_COUNT_PARAM+", type = :"+TYPE_PARAM+" WHERE uuid=:"+UUID_PARAM;
	private static final String DELETE = "DELETE FROM course where uuid = :" + UUID_PARAM;
	private static final String SELECT_ALL = "SELECT c.uuid, c.name, c.description, c.year_from, c.year_to, c.modif_at, c.modif_by, c.price_semester_1, c.price_semester_2, c.max_participant_count, c.type, "
			+ "cl.uuid \"course_location_uuid\" , cl.name \"course_location_name\", cl.description  \"course_location_description\", "
			+ "(select count(*) FROM course_course_participant ccp "
					+ "WHERE ccp.course_uuid = c.uuid AND ccp.course_partic_interrupted_at is null) \"participant_count\"  "
			+ "FROM course c "
			+ "LEFT JOIN course_location cl ON (c.course_location_uuid = cl.uuid) "
			+ "WHERE year_from = :"+YEAR_FROM_PARAM+" AND year_to = :"+YEAR_TO_PARAM 
			+ " ORDER BY c.name";
	
	private static final String SELECT_BY_TRAINER = "SELECT c.uuid, c.name, c.description, c.year_from, c.year_to, c.modif_at, c.modif_by, c.price_semester_1, c.price_semester_2, c.max_participant_count, c.type, "
			+ "cl.uuid \"course_location_uuid\" , cl.name \"course_location_name\", cl.description  \"course_location_description\", "
			+ "(select count(*) FROM course_course_participant ccp "
					+ "WHERE ccp.course_uuid = c.uuid AND ccp.course_partic_interrupted_at is null) \"participant_count\"  "
			+ "FROM course c "
			+ "LEFT JOIN course_location cl ON (c.course_location_uuid = cl.uuid) "
			+ "JOIN user_trainer_course utc ON c.uuid = utc.course_uuid "
			+ "WHERE year_from = :"+YEAR_FROM_PARAM+" AND year_to = :"+YEAR_TO_PARAM+" "
			+ "AND utc.user_trainer_uuid = :"+USER_UUID_PARAM+" "
			+ "ORDER BY c.name";
	
	private static final String SELECT_ALL_EXCEPT_COURSE = "SELECT uuid, name, description, year_from, year_to, modif_at, modif_by, price_semester_1, price_semester_2, course_location_uuid, max_participant_count, type FROM course where uuid != :"+COURSE_UUID_PARAM;
	private static final String SELECT_BY_UUID = "SELECT uuid, name, description, year_from, year_to, modif_at, modif_by, price_semester_1, price_semester_2, course_location_uuid, max_participant_count, type FROM course WHERE uuid=:" + UUID_PARAM;
	private static final String SELECT_BY_COURSE_PARTICIPANT = "SELECT c.uuid, c.name, c.description, c.year_from, c.year_to, c.modif_at, c.modif_by, c.price_semester_1, c.price_semester_2, course_location_uuid, max_participant_count, type, "
			+ "cl.uuid \"course_location_uuid\" , cl.name \"course_location_name\", cl.description  \"course_location_description\", "
			+ "(select count(*) FROM course_course_participant ccp "
					+ "WHERE ccp.course_uuid = c.uuid) \"participant_count\"  "
			+ "FROM course_course_participant ccp, course c "
			+ "LEFT JOIN course_location cl ON (c.course_location_uuid = cl.uuid) "
			+ "WHERE ccp.course_uuid = c.uuid "
			+ "AND ccp.course_participant_uuid = :" + UUID_PARAM + " AND c.year_from = :"+YEAR_FROM_PARAM+" AND c.year_to = :"+YEAR_TO_PARAM;
	private static final String SELECT_BY_COURSE_LOCATION_COUNT = "SELECT count(*) FROM course WHERE course_location_uuid = :"+COURSE_LOCATION_UUID_PARAM;
	
	private static final String SELECT_TRAINERS_BY_COURSE = "SELECT * FROM user_trainer_course utc "
			+ "JOIN user usr ON utc.user_trainer_uuid = usr.uuid "
			+ "JOIN contact con ON usr.contact_uuid = con.uuid "
			+ "WHERE usr.role != 'USER' "
			+ "AND utc.course_uuid = :"+COURSE_UUID_PARAM;
	private static final String INSERT_TRAINER_TO_COURSE = "INSERT INTO user_trainer_course " +
			"(course_uuid, user_trainer_uuid) " +
			"VALUES (:"+COURSE_UUID_PARAM+", :"+USER_UUID_PARAM+")";
	private static final String DELETE_TRAINER_FROM_COURSE = "DELETE FROM user_trainer_course "
			+ "WHERE course_uuid = :" + COURSE_UUID_PARAM + "  "
			+ "AND user_trainer_uuid = :" + USER_UUID_PARAM;
	private static final String DELETE_ALL_TRAINER_FROM_COURSE = "DELETE FROM user_trainer_course "
			+ "WHERE course_uuid = :" + COURSE_UUID_PARAM;
	
	@Autowired
	public CourseDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<Course> getAll(int yearFrom, int yearTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(YEAR_FROM_PARAM, yearFrom).addValue(YEAR_TO_PARAM, yearTo);
		return namedJdbcTemplate.query(SELECT_ALL, paramMap, new CourseRowMapper());
	}

	@Override
	public List<Course> getAllExceptCourse(UUID courseUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString());
		return namedJdbcTemplate.query(SELECT_ALL_EXCEPT_COURSE, paramMap, new CourseDetailRowMapper(courseParticipantDao, lessonDao, courseLocationDao));
	}
	
	@Override
	public List<Course> getByTrainer(UUID userUuid, int yearFrom, int yearTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().
				addValue(YEAR_FROM_PARAM, yearFrom).
				addValue(YEAR_TO_PARAM, yearTo).
				addValue(USER_UUID_PARAM, userUuid.toString());
				
		return namedJdbcTemplate.query(SELECT_BY_TRAINER, paramMap, new CourseRowMapper());
	}

	@Override
	public Course getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new CourseDetailRowMapper(courseParticipantDao, lessonDao, courseLocationDao));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Course> getByCourseParticipantUuid(UUID courseParticipantUuid, int yearFrom, int yearTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, courseParticipantUuid.toString()).addValue(YEAR_FROM_PARAM, yearFrom).addValue(YEAR_TO_PARAM, yearTo);
		return namedJdbcTemplate.query(SELECT_BY_COURSE_PARTICIPANT, paramMap, new CourseRowMapper());
	}
	
	@Override
	public boolean existsByCourseLocation(UUID courseLocationUuid) {
		Long courseCount = namedJdbcTemplate.queryForObject(SELECT_BY_COURSE_LOCATION_COUNT, new MapSqlParameterSource().addValue(COURSE_LOCATION_UUID_PARAM, courseLocationUuid.toString()), Long.class);
		return courseCount  > 0;
	}
	
	@Override
	public Course getPlainByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new SimpleCourseRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void delete(Course course) {
		courseParticipantDao.deleteAllFromCourse(course.getUuid());
		removeAllTrainersFromCourse(course.getUuid());
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, course.getUuid().toString()));
	}

	@Override
	public void insert(Course course) {		
		namedJdbcTemplate.update(INSERT, buildUpdateParamMap(course));
	}

	@Override
	public void update(Course course) {
		namedJdbcTemplate.update(UPDATE, buildUpdateParamMap(course));
	}
	
	private MapSqlParameterSource buildUpdateParamMap(Course course) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(course, paramMap);
		paramMap.addValue(YEAR_FROM_PARAM, course.getYearFrom());
		paramMap.addValue(YEAR_TO_PARAM, course.getYearTo());
		paramMap.addValue(NAME_PARAM, course.getName());
		paramMap.addValue(DESCRIPTION_PARAM, course.getDescription());
		paramMap.addValue(PRICE_SEMESTER_1_PARAM, course.getPriceSemester1());
		paramMap.addValue(PRICE_SEMESTER_2_PARAM, course.getPriceSemester2());
		paramMap.addValue(MAX_PARTIC_COUNT_PARAM, course.getMaxParticipantCount());
		paramMap.addValue(TYPE_PARAM, course.getCourseType() != null ? course.getCourseType().name() : CourseType.STANDARD.name());
		
		if (course.getCourseLocation() != null) {
			paramMap.addValue(COURSE_LOCATION_UUID_PARAM, course.getCourseLocation().getUuid().toString());
		} else {
			paramMap.addValue(COURSE_LOCATION_UUID_PARAM, null);
		}
		
		return paramMap;
	}
	
	@Override
	public List<ScbUser> getTrainersByCourse(UUID courseUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString());
		return namedJdbcTemplate.query(SELECT_TRAINERS_BY_COURSE, paramMap, new CourseTrainerRowMapper());
	}

	@Override
	public void addTrainersToCourse(List<ScbUser> trainers, UUID courseUuid) {
		updateTrainersInCourse(INSERT_TRAINER_TO_COURSE, trainers, courseUuid);
	}

	@Override
	public void removeTrainersFromCourse(List<ScbUser> trainers, UUID courseUuid) {
		updateTrainersInCourse(DELETE_TRAINER_FROM_COURSE, trainers, courseUuid);
	}
	
	@Override
	public void removeAllTrainersFromCourse(UUID courseUuid) {		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue(COURSE_UUID_PARAM, courseUuid.toString());
		namedJdbcTemplate.update(DELETE_ALL_TRAINER_FROM_COURSE, paramMap);			
	}
	
	
	private void updateTrainersInCourse(String sql, List<ScbUser> trainers, UUID courseUuid) {
		MapSqlParameterSource paramMap = null;
		for (ScbUser user : trainers) {
			paramMap = new MapSqlParameterSource();
			paramMap.addValue(COURSE_UUID_PARAM, courseUuid.toString());
			paramMap.addValue(USER_UUID_PARAM, user.getUuid().toString());
			namedJdbcTemplate.update(sql, paramMap);			
		}
	}
	
	public static final class CourseTrainerRowMapper implements RowMapper<ScbUser> {

		@Override
		public ScbUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ScbUser ret = new ScbUser();
			fetchIdentEntity(rs, ret);
			ret.setRole(ScbUserRole.valueOf(rs.getString("role")));
			ret.setUsername(rs.getString("username"));
			ret.setContact(new ContactRowMapper().mapRow(rs, rowNum));

			return ret;
		}
	}

	public static final class SimpleCourseRowMapper implements RowMapper<Course> {

		@Override
		public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
			Course ret = new Course();
			fetchIdentEntity(rs, ret);
			ret.setYearFrom(rs.getInt("year_from"));
			ret.setYearTo(rs.getInt("year_to"));
			ret.setDescription(rs.getString("description"));
			ret.setName(rs.getString("name"));
			ret.setPriceSemester1(rs.getLong("price_semester_1"));
			ret.setPriceSemester2(rs.getLong("price_semester_2"));

			return ret;
		}
	}
	
	/**
	 * Row Mapper urceny pro prehled obsahujici krome zakladnich atributu jeste data o courseLocation a poctu ucatsniku.
	 *
	 */
	public static final class CourseRowMapper implements RowMapper<Course> {

		@Override
		public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
			Course ret = new Course();
			fetchIdentEntity(rs, ret);
			ret.setYearFrom(rs.getInt("year_from"));
			ret.setYearTo(rs.getInt("year_to"));
			ret.setDescription(rs.getString("description"));
			ret.setName(rs.getString("name"));
			ret.setPriceSemester1(rs.getLong("price_semester_1"));
			ret.setPriceSemester2(rs.getLong("price_semester_2"));
			ret.setParticipantCount(rs.getInt("participant_count"));
			ret.setMaxParticipantCount(rs.getInt("max_participant_count"));
			ret.setCourseType(CourseType.valueOf(rs.getString("type")));

			CourseLocation courseLocation;
			String courseLocationUuid = rs.getString("course_location_uuid");
			if (StringUtils.hasText(courseLocationUuid)) {
				courseLocation = new CourseLocation();
				courseLocation.setUuid(UUID.fromString(courseLocationUuid));
				courseLocation.setDescription(rs.getString("course_location_description"));
				courseLocation.setName(rs.getString("course_location_name"));
				ret.setCourseLocation(courseLocation);
			}
			
			
			return ret;
		}
	}

	public static final class CourseDetailRowMapper implements RowMapper<Course> {
		private CourseParticipantDao courseParticipantDao;
		private LessonDao lessonDao;
		private CourseLocationDao courseLocationDao;

		public CourseDetailRowMapper(CourseParticipantDao courseParticipantDao, LessonDao lessonDao, CourseLocationDao courseLocationDao) {
			this.courseParticipantDao = courseParticipantDao;
			this.lessonDao = lessonDao;
			this.courseLocationDao = courseLocationDao;
		}

		@Override
		public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
			Course ret = new Course();
			fetchIdentEntity(rs, ret);
			ret.setYearFrom(rs.getInt("year_from"));
			ret.setYearTo(rs.getInt("year_to"));
			ret.setDescription(rs.getString("description"));
			ret.setName(rs.getString("name"));
			ret.setPriceSemester1(rs.getLong("price_semester_1"));
			ret.setPriceSemester2(rs.getLong("price_semester_2"));
			ret.setLessonList(lessonDao.getByCourse(ret.getUuid()));
			Collections.sort(ret.getLessonList(), Lesson.DAY_OF_WEEK_COMP);
			ret.setParticipantList(courseParticipantDao.getByCourseUuid(ret.getUuid()));
			ret.setMaxParticipantCount(rs.getInt("max_participant_count"));
			ret.setCourseType(CourseType.valueOf(rs.getString("type")));
			
			String courseLocUuidString = rs.getString("course_location_uuid");
			if (StringUtils.hasText(courseLocUuidString)) {
				UUID courseLocationUuid = UUID.fromString(courseLocUuidString);
				if (courseLocationUuid != null) {
					ret.setCourseLocation(courseLocationDao.getByUuid(courseLocationUuid));				
				}				
			}

			return ret;
		}
	}
}