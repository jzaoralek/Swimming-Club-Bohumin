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
import com.jzaoralek.scb.dataservice.dao.CourseApplDynAttrDao;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttr;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;

@Repository
public class CourseApplDynAttrDaoImpl extends BaseJdbcDao implements CourseApplDynAttrDao {

	private static final String COURSE_PARTIC_UUID_PARAM = "COURSE_PARTIC_UUID_PARAM";
	private static final String COURSE_PARTIC_CREATED_AT_PARAM = "COURSE_PARTIC_CREATED_AT_PARAM";
	private static final String COURSE_APPL_DYN_CONFIG_UUID_PARAM = "COURSE_APPL_DYN_CONFIG_UUID_PARAM";
	private static final String TEXT_VALUE_PARAM = "TEXT_VALUE_PARAM";
	private static final String DATE_VALUE_PARAM = "DATE_VALUE_PARAM";
	private static final String INT_VALUE_PARAM = "INT_VALUE_PARAM";
	private static final String DOUBLE_VALUE_PARAM = "DOUBLE_VALUE_PARAM";
	private static final String BOOLEAN_VALUE_PARAM = "BOOLEAN_VALUE_PARAM";
	
	private static final String SELECT_BY_COURSE_APPLICATION = "SELECT dynAttr.* " +
			"FROM course_application_dyn_attribute dynAttr, " +
				"course_application_dyn_attribute_config config " +
			"WHERE dynAttr.course_participant_uuid = :"+COURSE_PARTIC_UUID_PARAM + " " +
				"AND dynAttr.course_appl_dyn_attr_config_uuid = config.uuid " + " " +
				"AND :"+COURSE_PARTIC_CREATED_AT_PARAM + " BETWEEN config.created_at AND IFNULL(config.terminated_at, NOW())";
	
	private static final String SELECT_BY_UUID = "SELECT * FROM course_application_dyn_attribute "
			+ "WHERE uuid = :" + UUID_PARAM;
	
	private static final String INSERT = "INSERT INTO course_application_dyn_attribute " +
			"(uuid" +
			", course_participant_uuid  " +
			", course_appl_dyn_attr_config_uuid " +
			", text_value " +
			", date_value " +
			", int_value " +
			", double_value " +
			", boolean_value " +
			", modif_at " +
			", modif_by) " +
			"VALUES " +
			"(:"+UUID_PARAM +
			", :"+COURSE_PARTIC_UUID_PARAM +
			", :"+COURSE_APPL_DYN_CONFIG_UUID_PARAM +
			", :"+TEXT_VALUE_PARAM +
			", :"+DATE_VALUE_PARAM +
			", :"+INT_VALUE_PARAM +
			", :"+DOUBLE_VALUE_PARAM +
			", :"+BOOLEAN_VALUE_PARAM +
			", :"+MODIF_AT_PARAM +
			", :"+MODIF_BY_PARAM +")";
	
	private static final String UPDATE = "UPDATE course_application_dyn_attribute " +
			"SET " +
			" uuid = :"+UUID_PARAM +
			", course_participant_uuid = :"+COURSE_PARTIC_UUID_PARAM +
			", course_appl_dyn_attr_config_uuid = :"+COURSE_APPL_DYN_CONFIG_UUID_PARAM +
			", text_value = :"+TEXT_VALUE_PARAM +
			", date_value = :"+DATE_VALUE_PARAM +
			", int_value = :"+INT_VALUE_PARAM +
			", double_value = :"+DOUBLE_VALUE_PARAM +
			", boolean_value = :"+BOOLEAN_VALUE_PARAM +
			", modif_at = :"+MODIF_AT_PARAM +
			", modif_by = :"+MODIF_BY_PARAM +
			" WHERE uuid = :"+UUID_PARAM;
	
	private static final String DELETE_BY_COURSE_PARTIC_UUID = "DELETE FROM course_application_dyn_attribute "
			+ "WHERE course_participant_uuid = :"+COURSE_PARTIC_UUID_PARAM;
	
	@Autowired
	protected CourseApplDynAttrDaoImpl(DataSource ds) {
		super(ds);
	}
	
	@Override
	public List<CourseApplDynAttr> getByCoursePartic(CourseParticipant coursePartic) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue(COURSE_PARTIC_UUID_PARAM, coursePartic.getUuid().toString());
		/* Tohle je riziko, protoze po zmene prihlasky se mohou zobrazit/skryt dyn. atributy
		 * Resenim je pridat do course_application atribut createdAt.*/
		paramMap.addValue(COURSE_PARTIC_CREATED_AT_PARAM, coursePartic.getModifAt());
		
		return namedJdbcTemplate.query(SELECT_BY_COURSE_APPLICATION, 
				paramMap, 
				new CourseApplDynAttrRowMapper());
	}

	@Override
	public CourseApplDynAttr getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = 
				new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, 
					paramMap, 
					new CourseApplDynAttrRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void insert(CourseApplDynAttr dynAttr) {
		namedJdbcTemplate.update(INSERT, buildUpdateParamMap(dynAttr));
	}

	@Override
	public void update(CourseApplDynAttr dynAttr) {
		namedJdbcTemplate.update(UPDATE, buildUpdateParamMap(dynAttr));
	}

	@Override
	public void deleteByCoursePartic(UUID courseParticUuid) {
		namedJdbcTemplate.update(DELETE_BY_COURSE_PARTIC_UUID, 
				new MapSqlParameterSource().addValue(COURSE_PARTIC_UUID_PARAM, courseParticUuid.toString()));
	}
	
	private MapSqlParameterSource buildUpdateParamMap(CourseApplDynAttr dynAttr) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(dynAttr, paramMap);
		paramMap.addValue(COURSE_PARTIC_UUID_PARAM, dynAttr.getCourseParticUuid().toString());
		paramMap.addValue(COURSE_APPL_DYN_CONFIG_UUID_PARAM, dynAttr.getCourseApplDynConfig().getUuid().toString());
		paramMap.addValue(TEXT_VALUE_PARAM, dynAttr.getTextValue());
		paramMap.addValue(DATE_VALUE_PARAM, dynAttr.getDateValue());
		paramMap.addValue(INT_VALUE_PARAM, dynAttr.getIntValue());
		paramMap.addValue(DOUBLE_VALUE_PARAM, dynAttr.getDoubleValue());
		paramMap.addValue(BOOLEAN_VALUE_PARAM, dynAttr.isBooleanValue() ? "1" : "0");
		
		return paramMap;
	}
	
	public static final class CourseApplDynAttrRowMapper implements RowMapper<CourseApplDynAttr> {

		@Override
		public CourseApplDynAttr mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseApplDynAttr ret = new CourseApplDynAttr();
			fetchIdentEntity(rs, ret);
			
			ret.setCourseParticUuid(UUID.fromString(rs.getString("course_participant_uuid")));
			
			// naplnime jen UUID, uvidime jestli budou potreba i ostatni atributes
			CourseApplDynAttrConfig config = new CourseApplDynAttrConfig();
			config.setUuid(UUID.fromString(rs.getString("course_appl_dyn_attr_config_uuid")));
			ret.setCourseApplDynConfig(config);
			
			ret.setTextValue(rs.getString("text_value"));
			ret.setDateValue(rs.getTimestamp("date_value"));
			ret.setIntValue(rs.getInt("int_value"));
			ret.setDoubleValue(rs.getDouble("double_value"));
			ret.setBooleanValue(fetchBoolean(rs.getInt("boolean_value")));

			return ret;
		}
	}
}
