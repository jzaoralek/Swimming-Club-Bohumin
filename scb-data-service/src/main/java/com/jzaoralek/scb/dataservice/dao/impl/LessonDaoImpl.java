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

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.CourseDao;
import com.jzaoralek.scb.dataservice.dao.LessonDao;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.domain.Lesson.DayOfWeek;

@Repository
public class LessonDaoImpl extends BaseJdbcDao implements LessonDao {

	private static final String TIME_FROM_PARAM = "DESCRIPTION";
	private static final String TIME_TO_PARAM = "DESCRIPTION";
	private static final String DAY_OF_WEEK_PARAM = "DESCRIPTION";

	private static final String SELECT_ALL = "SELECT uuid, time_from, time_to, day_of_week, course_uuid, modif_at, modif_by FROM lesson";
	private static final String SELECT_BY_COURSE = "SELECT uuid, time_from, time_to, day_of_week, course_uuid, modif_at, modif_by FROM lesson WHERE course_uuid = :"+COURSE_UUID_PARAM;
	private static final String SELECT_BY_UUID = "SELECT uuid, time_from, time_to, day_of_week, course_uuid, modif_at, modif_by FROM lesson WHERE uuid = :" + UUID_PARAM;
	private static final String INSERT = "INSERT INTO lesson (uuid, time_from, time_to, day_of_week, course_uuid, modif_at, modif_by) " +
			"VALUES (:"+UUID_PARAM+", :"+TIME_FROM_PARAM+", :"+TIME_TO_PARAM+", :"+DAY_OF_WEEK_PARAM+", :"+COURSE_UUID_PARAM+",:"+MODIF_AT_PARAM+",:"+MODIF_BY_PARAM+")";
	private static final String UPDATE = "UPDATE lesson SET uuid = :"+UUID_PARAM+" , time_from = :"+TIME_FROM_PARAM+", time_to = :"+TIME_TO_PARAM+", day_of_week = :"+DAY_OF_WEEK_PARAM+", course_uuid = :"+COURSE_UUID_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+" "
			+ "WHERE uuid = :"+UUID_PARAM+"";
	private static final String DELETE = "DELETE FROM lesson where uuid = :" + UUID_PARAM;


	@Autowired
	private CourseDao courseDao;

	@Autowired
	public LessonDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<Lesson> getAll() {
		return namedJdbcTemplate.query(SELECT_ALL, new LessonRowMapper());
	}

	@Override
	public List<Lesson> getByCourse(UUID courseUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_COURSE, paramMap, new LessonRowMapper());
	}

	@Override
	public Lesson getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new LessonRowMapperDetail(courseDao));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void insert(Lesson lesson) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(lesson, paramMap);
		paramMap.addValue(TIME_FROM_PARAM, lesson.getTimeFrom());
		paramMap.addValue(TIME_TO_PARAM, lesson.getTimeTo());
		paramMap.addValue(DAY_OF_WEEK_PARAM, lesson.getDayOfWeek().name());
		paramMap.addValue(COURSE_UUID_PARAM, lesson.getCourseUuid());

		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public void update(Lesson lesson) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(lesson, paramMap);
		paramMap.addValue(TIME_FROM_PARAM, lesson.getTimeFrom());
		paramMap.addValue(TIME_TO_PARAM, lesson.getTimeTo());
		paramMap.addValue(DAY_OF_WEEK_PARAM, lesson.getDayOfWeek().name());
		paramMap.addValue(COURSE_UUID_PARAM, lesson.getCourseUuid());

		namedJdbcTemplate.update(UPDATE, paramMap);

	}

	@Override
	public void delete(Lesson lesson) {
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, lesson.getUuid().toString()));
	}

	public static final class LessonRowMapper implements RowMapper<Lesson> {

		@Override
		public Lesson mapRow(ResultSet rs, int rowNum) throws SQLException {
			Lesson ret = new Lesson();
			fetchIdentEntity(rs, ret);
			ret.setCourseUuid(UUID.fromString(rs.getString("course_uuid")));
			ret.setDayOfWeek(DayOfWeek.fromString(rs.getString("day_of_week")));
			ret.setTimeFrom(rs.getTime("time_from"));
			ret.setTimeTo(rs.getTime("time_to"));

			return ret;
		}
	}

	public static final class LessonRowMapperDetail implements RowMapper<Lesson> {

		private final CourseDao courseDao;

		public LessonRowMapperDetail(CourseDao courseDao) {
			this.courseDao = courseDao;
		}

		@Override
		public Lesson mapRow(ResultSet rs, int rowNum) throws SQLException {
			Lesson ret = new Lesson();
			fetchIdentEntity(rs, ret);
			UUID courseUuid = UUID.fromString(rs.getString("course_uuid"));
			ret.setCourseUuid(courseUuid);
			ret.setDayOfWeek(DayOfWeek.fromString(rs.getString("day_of_week")));
			ret.setTimeFrom(rs.getTime("time_from"));
			ret.setTimeTo(rs.getTime("time_to"));
			ret.setCourse(courseDao.getByUuid(courseUuid));

			return ret;
		}
	}
}
