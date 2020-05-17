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
import com.jzaoralek.scb.dataservice.dao.CourseApplDynAttrConfigDao;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig.CourseApplDynAttrConfigType;

@Repository
public class CourseApplDynAttrConfigDaoImpl extends BaseJdbcDao implements CourseApplDynAttrConfigDao {

	protected static final String NAME_PARAM = "NAME";
	protected static final String REQUIRED_PARAM = "REQUIRED";
	protected static final String CREATED_AT_PARAM = "CREATED_AT";
	protected static final String TERMINATED_AT_PARAM = "TERMINATED_AT";
	
	private static final String SELECT_ALL = "SELECT * FROM course_application_dyn_attribute_config ORDER BY name";
	private static final String SELECT_BY_UUID = "SELECT * FROM course_application_dyn_attribute_config WHERE uuid = :" + UUID_PARAM;
	private static final String SELECT_BY_NAME = "SELECT * FROM course_application_dyn_attribute_config WHERE name = :" + NAME_PARAM;
	private static final String INSERT = "INSERT INTO course_application_dyn_attribute_config  " +
											"(uuid,name,description,required,type,created_at,terminated_at,modif_at,modif_by) " +
										"VALUES (:"+UUID_PARAM+", :"+NAME_PARAM+", :"+DESCRIPTION_PARAM+", :"+REQUIRED_PARAM+", :"+TYPE_PARAM+", :"+CREATED_AT_PARAM+", :"+TERMINATED_AT_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+")";
	private static final String UPDATE = "UPDATE course_application_dyn_attribute_config " + 
										 "SET " +
										 	"name = :"+NAME_PARAM+", " +
										 	"description = :"+DESCRIPTION_PARAM+", " +
										 	"required = :"+REQUIRED_PARAM+", " +
										 	"type = :"+TYPE_PARAM+", " +
										 	"created_at = "+CREATED_AT_PARAM+", " +
										 	"terminated_at = :"+TERMINATED_AT_PARAM+", " +
										 	"modif_at = :"+MODIF_AT_PARAM+", " +
										 	"modif_by = :"+MODIF_BY_PARAM+" " +
										 	"WHERE uuid = :"+UUID_PARAM;
	private static final String DELETE = "DELETE FROM course_application_dyn_attribute_config WHERE uuid = :"+UUID_PARAM;
	
	private static final String TERMINATE = "UPDATE course_application_dyn_attribute_config " + 
										 "SET " +
										 	"terminated_at = :"+TERMINATED_AT_PARAM+", " +
										 	"modif_at = :"+MODIF_AT_PARAM+", " +
										 	"modif_by = :"+MODIF_BY_PARAM+" " +
										 	"WHERE uuid = :"+UUID_PARAM;
	
	
	@Autowired
	protected CourseApplDynAttrConfigDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<CourseApplDynAttrConfig> getAll() {
		return namedJdbcTemplate.query(SELECT_ALL, new CourseApplDynAttrConfigRowMapper());
	}
	
	@Override
	public List<CourseApplDynAttrConfig> getByName(String name) {
		MapSqlParameterSource paramMap = 
				new MapSqlParameterSource().addValue(NAME_PARAM, name);
		return namedJdbcTemplate.query(SELECT_BY_NAME, 
				paramMap, 
				new CourseApplDynAttrConfigRowMapper());
	}

	@Override
	public CourseApplDynAttrConfig getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = 
				new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, 
					paramMap, 
					new CourseApplDynAttrConfigRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void insert(CourseApplDynAttrConfig config) {
		namedJdbcTemplate.update(INSERT, buildUpdateParamMap(config));
	}

	@Override
	public void update(CourseApplDynAttrConfig config) {
		namedJdbcTemplate.update(UPDATE, buildUpdateParamMap(config));
		
	}

	@Override
	public void delete(UUID uuid) {
		namedJdbcTemplate.update(DELETE, 
				new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString()));
	}
	
	@Override
	public void terminate(CourseApplDynAttrConfig config) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(config, paramMap);
		paramMap.addValue(UUID_PARAM, config.getUuid().toString());
		paramMap.addValue(TERMINATED_AT_PARAM, config.getTerminatedAt());
		namedJdbcTemplate.update(TERMINATE, paramMap);
	}
	
	private MapSqlParameterSource buildUpdateParamMap(CourseApplDynAttrConfig config) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(config, paramMap);
		paramMap.addValue(NAME_PARAM, config.getName());
		paramMap.addValue(DESCRIPTION_PARAM, config.getDescription());
		paramMap.addValue(REQUIRED_PARAM, config.isRequired() ? "1" : "0");
		paramMap.addValue(CREATED_AT_PARAM, config.getCreatedAt());
		paramMap.addValue(TERMINATED_AT_PARAM, config.getTerminatedAt());
		paramMap.addValue(TYPE_PARAM, config.getType() != null ? config.getType().name() : CourseApplDynAttrConfigType.TEXT.name());
		
		return paramMap;
	}
	
	public static final class CourseApplDynAttrConfigRowMapper implements RowMapper<CourseApplDynAttrConfig> {

		@Override
		public CourseApplDynAttrConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseApplDynAttrConfig ret = new CourseApplDynAttrConfig();
			fetchIdentEntity(rs, ret);
			ret.setName(rs.getString("name"));
			ret.setDescription(rs.getString("description"));
			ret.setRequired(fetchBoolean(rs.getInt("required")));
			ret.setCreatedAt(rs.getTimestamp(CREATED_AT_PARAM));
			ret.setTerminatedAt(rs.getTimestamp(TERMINATED_AT_PARAM));
			ret.setType(CourseApplDynAttrConfigType.valueOf(rs.getString("type")));

			return ret;
		}
	}
}
