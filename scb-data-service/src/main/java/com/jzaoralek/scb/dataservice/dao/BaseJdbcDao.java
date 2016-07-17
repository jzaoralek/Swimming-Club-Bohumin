package com.jzaoralek.scb.dataservice.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class BaseJdbcDao {

	protected final JdbcTemplate jdbcTemplate;
	protected final NamedParameterJdbcTemplate namedJdbcTemplate;

	protected BaseJdbcDao(DataSource ds) {
		jdbcTemplate = new JdbcTemplate(ds);
		namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	}
}