package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.CourseApplicationDao;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;

@Repository
public class CourseApplicationDaoImpl extends BaseJdbcDao implements CourseApplicationDao {

	private static final String UUID_PARAM = "UUID";
	private static final String YEAR_FROM_PARAM = "YEAR_FROM";
	private static final String YEAR_TO_PARAM = "YEAR_TO";

	private static final String INSERT = "INSERT INTO course_application (uuid, year_from, year_to) values (:"+UUID_PARAM+",:"+YEAR_FROM_PARAM+",:"+YEAR_TO_PARAM+")";

	@Autowired
	public CourseApplicationDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public void insert(CourseApplication courseApplication) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue(UUID_PARAM, courseApplication.getUuid().toString());
		paramMap.addValue(YEAR_FROM_PARAM, courseApplication.getYearFrom());
		paramMap.addValue(YEAR_TO_PARAM, courseApplication.getYearTo());

		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public void update(CourseApplication courseApplication) {
		// TODO Auto-generated method stub
	}

	@Override
	public void delete(CourseApplication courseApplication) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<CourseApplication> getAll(boolean deep) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CourseApplication getByUuid(UUID uuid, boolean deep) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public List<String> getAll() {
//		String SQL = "select name from language";
//		List<String> ret = jdbcTemplateObject.queryForList(SQL, String.class);
//		return ret;
//	}
}