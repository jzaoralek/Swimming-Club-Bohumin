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
import com.jzaoralek.scb.dataservice.dao.CourseLocationDao;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;

@Repository
public class CourseLocationDaoImpl extends BaseJdbcDao implements CourseLocationDao {

	private static final String NAME_PARAM = "name";
	
	private static final String INSERT = "INSERT INTO course_location " +
			"(uuid, name, description) " +
			"VALUES (:"+UUID_PARAM+", :"+NAME_PARAM+", :"+DESCRIPTION_PARAM+")";

	private static final String UPDATE = "UPDATE course_location SET name = :"+NAME_PARAM+", description = :"+DESCRIPTION_PARAM+" WHERE uuid=:"+UUID_PARAM;
	private static final String DELETE = "DELETE FROM course_location where uuid = :" + UUID_PARAM;
	private static final String SELECT_BY_UUID = "SELECT uuid, name, description FROM course_location WHERE uuid=:" + UUID_PARAM;
	private static final String SELECT_ALL = "SELECT uuid, name, description FROM course_location";
	
	
	@Autowired
	public CourseLocationDaoImpl(DataSource ds) {
		super(ds);
	}
	
	@Override
	public CourseLocation getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new CourseLocationMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public List<CourseLocation> getAll() {
		return namedJdbcTemplate.query(SELECT_ALL, new CourseLocationMapper());
	}

	@Override
	public void insert(CourseLocation courseLocatiom) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue(UUID_PARAM, courseLocatiom.getUuid().toString());
		paramMap.addValue(NAME_PARAM, courseLocatiom.getName());
		paramMap.addValue(DESCRIPTION_PARAM, courseLocatiom.getDescription());
		
		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public void update(CourseLocation courseLocatiom) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue(UUID_PARAM, courseLocatiom.getUuid().toString());
		paramMap.addValue(NAME_PARAM, courseLocatiom.getName());
		paramMap.addValue(DESCRIPTION_PARAM, courseLocatiom.getDescription());
		
		namedJdbcTemplate.update(UPDATE, paramMap);
	}

	@Override
	public void delete(CourseLocation courseLocatiom) {
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, courseLocatiom.getUuid().toString()));
	}

	public static final class CourseLocationMapper implements RowMapper<CourseLocation> {
		
		@Override
		public CourseLocation mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseLocation ret = new CourseLocation();
			ret.setUuid(UUID.fromString(rs.getString(UUID_PARAM)));
			ret.setDescription(rs.getString("description"));
			ret.setName(rs.getString("name"));

			return ret;
		}
	}

}
