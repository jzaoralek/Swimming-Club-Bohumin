package com.jzaoralek.scb.dataservice.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.jzaoralek.scb.dataservice.domain.IdentEntity;

public abstract class BaseJdbcDao {

	protected static final String UUID_PARAM = "UUID";
	protected static final String MODIF_AT_PARAM = "MODIF_AT";
	protected static final String MODIF_BY_PARAM = "MODIF_BY";

	protected final JdbcTemplate jdbcTemplate;
	protected final NamedParameterJdbcTemplate namedJdbcTemplate;

	protected BaseJdbcDao(DataSource ds) {
		jdbcTemplate = new JdbcTemplate(ds);
		namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	}

	protected void fillIdentEntity(IdentEntity identEntity, MapSqlParameterSource paramMap) {
		paramMap.addValue(UUID_PARAM, identEntity.getUuid().toString());
		paramMap.addValue(MODIF_AT_PARAM, identEntity.getModifAt());
		paramMap.addValue(MODIF_BY_PARAM, identEntity.getModifBy());
	}
}