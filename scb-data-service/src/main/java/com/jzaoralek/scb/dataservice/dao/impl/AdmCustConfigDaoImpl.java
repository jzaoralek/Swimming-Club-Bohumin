package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.jzaoralek.scb.dataservice.dao.AdmCustConfigDao;
import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.sportologic.common.model.domain.CustomerConfig;

@Repository
public class AdmCustConfigDaoImpl extends BaseJdbcDao implements AdmCustConfigDao {

	private static final String CUST_ID_PARAM = "cust_id";
	
	private static final String SELECT_ALL = "SELECT * FROM customer_config ORDER BY cust_name";
	private static final String SELECT_BY_UUID = "SELECT * FROM customer_config WHERE uuid=:" + UUID_PARAM;
	private static final String SELECT_BY_CUST_ID = "SELECT * FROM customer_config WHERE cust_id=:" + CUST_ID_PARAM;
	private static final String SELECT_DEFAULT_CUST = "SELECT * FROM customer_config WHERE cust_default=1";
	
	@Autowired
	protected AdmCustConfigDaoImpl(@Qualifier("adminDataSource")DataSource ds) {
		super(ds);
	}

	@Override
	public List<CustomerConfig> getAll() {
		return namedJdbcTemplate.query(SELECT_ALL, new AdmCustConfigRowMapper());
	}

	@Override
	public CustomerConfig getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new AdmCustConfigRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public CustomerConfig getByCustId(String custId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(CUST_ID_PARAM, custId);
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_CUST_ID, paramMap, new AdmCustConfigRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public CustomerConfig getDefault() {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		try {
			return namedJdbcTemplate.queryForObject(SELECT_DEFAULT_CUST, paramMap, new AdmCustConfigRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public static final class AdmCustConfigRowMapper implements RowMapper<CustomerConfig> {

		@Override
		public CustomerConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
			CustomerConfig ret = new CustomerConfig();
			fetchIdentEntity(rs, ret);
			
			ret.setCustId(rs.getString("cust_id"));
			ret.setCustName(rs.getString("cust_name"));
			ret.setCustDefault(fetchBoolean(rs.getInt("cust_default")));
			ret.setDbUrl(rs.getString("db_url"));
			ret.setDbUser(rs.getString("db_user"));
			ret.setDbPassword(rs.getString("db_password"));

			return ret;
		}
	}
}
