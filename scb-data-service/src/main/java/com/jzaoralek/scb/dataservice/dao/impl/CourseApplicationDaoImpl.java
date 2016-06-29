package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.jzaoralek.scb.dataservice.dao.CourseApplicationDao;

@Repository
public class CourseApplicationDaoImpl implements CourseApplicationDao {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	@Override
	public List<String> getAll() {
		String SQL = "select name from language";
		List<String> ret = (List<String>)jdbcTemplateObject.queryForList(SQL, String.class);
		return ret;
	}
}