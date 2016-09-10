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
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.CourseDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.dao.LessonDao;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.Lesson;

@Repository
public class CourseDaoImpl extends BaseJdbcDao implements CourseDao {

	@Autowired
	private CourseParticipantDao courseParticipantDao;

	@Autowired
	private LessonDao lessonDao;

	private static final String NAME_PARAM = "NAME";
	private static final String DESCRIPTION_PARAM = "DESCRIPTION";

	private static final String INSERT = "INSERT INTO course " +
			"(uuid, name, description, year_from, year_to, modif_at, modif_by) " +
			"VALUES (:"+UUID_PARAM+", :"+NAME_PARAM+", :"+DESCRIPTION_PARAM+", :"+YEAR_FROM_PARAM+",:"+YEAR_TO_PARAM+",:"+MODIF_AT_PARAM+",:"+MODIF_BY_PARAM+")";

	private static final String UPDATE = "UPDATE course SET uuid = :"+UUID_PARAM+" , name = :"+NAME_PARAM+", description = :"+DESCRIPTION_PARAM+", year_from = :"+YEAR_FROM_PARAM+", year_to = :"+YEAR_TO_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+" WHERE uuid=:"+UUID_PARAM;
	private static final String DELETE = "DELETE FROM course where uuid = :" + UUID_PARAM;
	private static final String SELECT_ALL = "SELECT uuid, name, description, year_from, year_to, modif_at, modif_by FROM course";
	private static final String SELECT_ALL_EXCEPT_COURSE = "SELECT uuid, name, description, year_from, year_to, modif_at, modif_by FROM course where uuid != :"+COURSE_UUID_PARAM;
	private static final String SELECT_BY_UUID = "SELECT uuid, name, description, year_from, year_to, modif_at, modif_by FROM course WHERE uuid=:" + UUID_PARAM;
	private static final String SELECT_BY_COURSE_PARTICIPANT = "SELECT c.uuid, c.name, c.description, c.year_from, c.year_to, c.modif_at, c.modif_by FROM course_course_participant ccp, course c WHERE ccp.course_uuid = c.uuid AND ccp.course_participant_uuid = :" + UUID_PARAM;

	@Autowired
	public CourseDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<Course> getAll() {
		return namedJdbcTemplate.query(SELECT_ALL, new CourseRowMapper(courseParticipantDao, lessonDao));
	}

	@Override
	public List<Course> getAllExceptCourse(UUID courseUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString());
		return namedJdbcTemplate.query(SELECT_ALL_EXCEPT_COURSE, paramMap, new CourseRowMapper(courseParticipantDao, lessonDao));
	}

	@Override
	public Course getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new CourseRowMapper(courseParticipantDao, lessonDao));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Course> getByCourseParticipantUuid(UUID courseParticipantUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, courseParticipantUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_COURSE_PARTICIPANT, paramMap, new SimpleCourseRowMapper());
	}

	@Override
	public void delete(Course course) {
		courseParticipantDao.deleteAllFromCourse(course.getUuid());
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, course.getUuid().toString()));
	}

	@Override
	public void insert(Course course) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(course, paramMap);
		paramMap.addValue(YEAR_FROM_PARAM, course.getYearFrom());
		paramMap.addValue(YEAR_TO_PARAM, course.getYearTo());
		paramMap.addValue(NAME_PARAM, course.getName());
		paramMap.addValue(DESCRIPTION_PARAM, course.getDescription());

		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public void update(Course course) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(course, paramMap);
		paramMap.addValue(YEAR_FROM_PARAM, course.getYearFrom());
		paramMap.addValue(YEAR_TO_PARAM, course.getYearTo());
		paramMap.addValue(NAME_PARAM, course.getName());
		paramMap.addValue(DESCRIPTION_PARAM, course.getDescription());

		courseParticipantDao.deleteAllFromCourse(course.getUuid());
		if (!CollectionUtils.isEmpty(course.getParticipantList())) {
			courseParticipantDao.insetToCourse(course.getParticipantList(), course.getUuid());
		}

		namedJdbcTemplate.update(UPDATE, paramMap);

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

			return ret;
		}
	}

	public static final class CourseRowMapper implements RowMapper<Course> {
		private CourseParticipantDao courseParticipantDao;
		private LessonDao lessonDao;

		public CourseRowMapper(CourseParticipantDao courseParticipantDao, LessonDao lessonDao) {
			this.courseParticipantDao = courseParticipantDao;
			this.lessonDao = lessonDao;
		}

		@Override
		public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
			Course ret = new Course();
			fetchIdentEntity(rs, ret);
			ret.setYearFrom(rs.getInt("year_from"));
			ret.setYearTo(rs.getInt("year_to"));
			ret.setDescription(rs.getString("description"));
			ret.setName(rs.getString("name"));
			ret.setLessonList(lessonDao.getByCourse(ret.getUuid()));
			Collections.sort(ret.getLessonList(), Lesson.DAY_OF_WEEK_COMP);
			ret.setParticipantList(courseParticipantDao.getByCourseUuid(ret.getUuid()));

			return ret;
		}
	}
}