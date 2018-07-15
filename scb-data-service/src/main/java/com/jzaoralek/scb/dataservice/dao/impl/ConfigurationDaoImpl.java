package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.ConfigurationDao;
import com.jzaoralek.scb.dataservice.domain.Config;

@Repository
public class ConfigurationDaoImpl extends BaseJdbcDao implements ConfigurationDao {

	private static final String NAME_PARAM = "name";
	private static final String VALUE_PARAM = "value";
	private static final String TYPE_PARAM = "type";

	private static final String SELECT_ALL = "SELECT uuid, name, description, val, type, modif_at, modif_by FROM configuration ";
	private static final String SELECT_BY_NAME = "SELECT uuid, name, description, val, type, modif_at, modif_by FROM configuration WHERE name = :" + NAME_PARAM;
	private static final String UPDATE = "UPDATE configuration SET name = :"+NAME_PARAM+", description = :"+DESCRIPTION_PARAM+", val = :"+VALUE_PARAM+", type = :"+TYPE_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+" WHERE uuid = :"+UUID_PARAM+"";


	@Autowired
	public ConfigurationDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public Config getByName(String name) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(NAME_PARAM, name);
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_NAME, paramMap, new ConfigRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Config> getAll() {
		return namedJdbcTemplate.query(SELECT_ALL, new ConfigRowMapper());
	}

	@Override
	public void update(Config config) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(config, paramMap);
		paramMap.addValue(NAME_PARAM, config.getName());
		paramMap.addValue(DESCRIPTION_PARAM, config.getDescription());
		paramMap.addValue(VALUE_PARAM, config.getValue());
		paramMap.addValue(TYPE_PARAM, config.getType().name());

		namedJdbcTemplate.update(UPDATE, paramMap);
	}

	public static final class ConfigRowMapper implements RowMapper<Config> {
		@Override
		public Config mapRow(ResultSet rs, int rowNum) throws SQLException {
			Config ret = new Config();
			fetchIdentEntity(rs, ret);
			ret.setName(rs.getString("name"));
			ret.setDescription(rs.getString("description"));
			ret.setValue(rs.getString("val"));
			ret.setType(Config.ConfigType.valueOf(rs.getString("type")));

			return ret;
		}
	}
}
