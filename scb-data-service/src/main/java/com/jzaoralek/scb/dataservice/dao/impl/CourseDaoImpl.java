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
import com.jzaoralek.scb.dataservice.domain.Course;

@Repository
public class CourseDaoImpl extends BaseJdbcDao implements CourseDao {

	@Autowired
	private CourseParticipantDao courseParticipantDao;

	private static final String NAME_PARAM = "NAME";
	private static final String DESCRIPTION_PARAM = "DESCRIPTION";

	private static final String INSERT = "INSERT INTO course " +
			"(uuid, name, description, year_from, year_to, modif_at, modif_by) " +
			"VALUES (:"+UUID_PARAM+", :"+NAME_PARAM+", :"+DESCRIPTION_PARAM+", :"+YEAR_FROM_PARAM+",:"+YEAR_TO_PARAM+",:"+MODIF_AT_PARAM+",:"+MODIF_BY_PARAM+")";

	private static final String UPDATE = "UPDATE course SET uuid = :"+UUID_PARAM+" , name = :"+NAME_PARAM+", description = :"+DESCRIPTION_PARAM+", year_from = :"+YEAR_FROM_PARAM+", year_to = :"+YEAR_TO_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+" WHERE uuid=:"+UUID_PARAM;
	private static final String DELETE = "DELETE FROM course where uuid = :" + UUID_PARAM;
	private static final String SELECT_ALL = "SELECT uuid, name, description, year_from, year_to, modif_at, modif_by FROM course";
	private static final String SELECT_BY_UUID = "SELECT uuid, name, description, year_from, year_to, modif_at, modif_by FROM course WHERE uuid=:" + UUID_PARAM;

	@Autowired
	public CourseDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<Course> getAll() {
		return namedJdbcTemplate.query(SELECT_ALL, new CourseRowMapper(courseParticipantDao));
	}

	@Override
	public Course getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new CourseRowMapper(courseParticipantDao));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void delete(Course course) {
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

	public static final class CourseRowMapper implements RowMapper<Course> {
		private CourseParticipantDao courseParticipantDao;
//
		public CourseRowMapper(CourseParticipantDao courseParticipantDao) {
			this.courseParticipantDao = courseParticipantDao;
		}

		@Override
		public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
			Course ret = new Course();
			fetchIdentEntity(rs, ret);
			ret.setYearFrom(rs.getInt("year_from"));
			ret.setYearTo(rs.getInt("year_to"));
			ret.setDescription(rs.getString("description"));
			ret.setName(rs.getString("name"));
			ret.setLessonList(Collections.EMPTY_LIST);
			ret.setParticipantList(courseParticipantDao.getByCourseUuid(ret.getUuid()));

			return ret;
		}
	}
}