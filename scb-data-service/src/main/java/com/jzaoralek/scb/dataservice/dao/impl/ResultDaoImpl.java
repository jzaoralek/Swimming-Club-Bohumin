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
import com.jzaoralek.scb.dataservice.dao.ResultDao;
import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.Result;

@Repository
public class ResultDaoImpl extends BaseJdbcDao implements ResultDao {

	protected static final String COURSE_PARTICIPANT_UUID_PARAM = "COURSE_PARTICIPANT_UUID";
	protected static final String STYLE_UUID_PARAM = "STYLE_UUID";
	protected static final String RESULT_TIME_PARAM = "RESULT_TIME";
	protected static final String RESULT_DATE_PARAM = "RESULT_DATE";
	protected static final String DISTANCE_PARAM = "DISTANCE";
	private static final String DESCRIPTION_PARAM = "DESCRIPTION";

	private static final String SELECT_BY_COURSE_PARTICIPANT = "SELECT res.uuid, res.result_time, res.result_date, res.style_uuid, res.distance, res.description, res.course_participant_uuid, res.modif_at, res.modif_by "+
			",ci.uuid ci_uuid, ci.name ci_name, ci.description ci_description " +
			"FROM result res, codelist_item ci WHERE res.style_uuid = ci.uuid AND ci.item_type = '"+CodeListItem.CodeListType.SWIMMING_STYLE+"' AND res.course_participant_uuid = :" + COURSE_PARTICIPANT_UUID_PARAM + " ORDER BY res.result_date DESC ";
	private static final String SELECT_BY_COURSE_PARTICIPANT_AND_STYLE = "SELECT res.uuid, res.result_time, res.result_date, res.style_uuid, res.distance, res.description, res.course_participant_uuid, res.modif_at, res.modif_by "+
			",ci.uuid ci_uuid, ci.name ci_name, ci.description ci_description " +
			"FROM result res, codelist_item ci WHERE res.style_uuid = ci.uuid AND ci.item_type = '"+CodeListItem.CodeListType.SWIMMING_STYLE+"' AND res.course_participant_uuid = :" + COURSE_PARTICIPANT_UUID_PARAM + " AND res.style_uuid = :" + STYLE_UUID_PARAM + " ORDER BY res.result_date DESC ";
	private static final String SELECT_BY_UUID = "SELECT res.uuid, res.result_time, res.result_date, res.style_uuid, res.distance, res.description, res.course_participant_uuid, res.modif_at, res.modif_by "+
			",ci.uuid ci_uuid, ci.name ci_name, ci.description ci_description " +
			"FROM result res, codelist_item ci WHERE res.style_uuid = ci.uuid AND ci.item_type = '"+CodeListItem.CodeListType.SWIMMING_STYLE+"' AND res.uuid = :" + UUID_PARAM;
	private static final String DELETE = "DELETE FROM result where uuid = :" + UUID_PARAM;
	private static final String DELETE_BY_COURSE_PARTICIPANT = "DELETE FROM result where course_participant_uuid = :" + COURSE_PARTICIPANT_UUID_PARAM;
	private static final String INSERT = "INSERT INTO result (uuid, result_time, result_date, style_uuid, distance, description, course_participant_uuid, modif_at, modif_by) " +
			"VALUES (:"+UUID_PARAM+", :"+RESULT_TIME_PARAM+", :"+RESULT_DATE_PARAM+", :"+STYLE_UUID_PARAM+", :"+DISTANCE_PARAM+", :"+DESCRIPTION_PARAM+", :"+COURSE_PARTICIPANT_UUID_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+")";
	private static final String UPDATE = "UPDATE result SET result_time = :"+RESULT_TIME_PARAM+", result_date = :"+RESULT_DATE_PARAM+", style_uuid = :"+STYLE_UUID_PARAM+", distance = :"+DISTANCE_PARAM+", description = :"+DESCRIPTION_PARAM+", course_participant_uuid = :"+COURSE_PARTICIPANT_UUID_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+" " +
			"WHERE uuid = :"+UUID_PARAM;
	private static final String SELECT_COUNT_BY_STYLE = "SELECT COUNT(*) FROM result WHERE style_uuid = :" + STYLE_UUID_PARAM;

	@Autowired
	public ResultDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<Result> getByCourseParticipant(UUID courseParticUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_PARTICIPANT_UUID_PARAM, courseParticUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_COURSE_PARTICIPANT, paramMap, new ResultRowMapper());
	}

	@Override
	public List<Result> getByCourseParticipantAndStyle(UUID courseParticUuid, UUID styleUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_PARTICIPANT_UUID_PARAM, courseParticUuid.toString()).addValue(STYLE_UUID_PARAM, styleUuid.toString());
		return namedJdbcTemplate.query(SELECT_BY_COURSE_PARTICIPANT_AND_STYLE, paramMap, new ResultRowMapper());
	}

	@Override
	public Result getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new ResultRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void insert(Result result) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(result, paramMap);
		paramMap.addValue(STYLE_UUID_PARAM, result.getStyle().getUuid().toString());
		paramMap.addValue(RESULT_TIME_PARAM, result.getResultTime());
		paramMap.addValue(RESULT_DATE_PARAM, result.getResultDate());
		paramMap.addValue(DISTANCE_PARAM, result.getDistance());
		paramMap.addValue(COURSE_PARTICIPANT_UUID_PARAM, result.getCourseParticipantUuid().toString());
		paramMap.addValue(DESCRIPTION_PARAM, result.getDescription());

		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public void update(Result result) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(result, paramMap);
		paramMap.addValue(STYLE_UUID_PARAM, result.getStyle().getUuid().toString());
		paramMap.addValue(RESULT_TIME_PARAM, result.getResultTime());
		paramMap.addValue(RESULT_DATE_PARAM, result.getResultDate());
		paramMap.addValue(DISTANCE_PARAM, result.getDistance());
		paramMap.addValue(COURSE_PARTICIPANT_UUID_PARAM, result.getCourseParticipantUuid().toString());
		paramMap.addValue(DESCRIPTION_PARAM, result.getDescription());

		namedJdbcTemplate.update(UPDATE, paramMap);
	}

	@Override
	public void delete(UUID uuid) {
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString()));
	}

	@Override
	public void deleteByCourseParticipant(UUID courseParticUuid) {
		namedJdbcTemplate.update(DELETE_BY_COURSE_PARTICIPANT, new MapSqlParameterSource().addValue(COURSE_PARTICIPANT_UUID_PARAM, courseParticUuid.toString()));
	}

	@Override
	public boolean styleUsedInResult(UUID styleUuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(STYLE_UUID_PARAM, styleUuid.toString());
		Integer count = namedJdbcTemplate.queryForObject(SELECT_COUNT_BY_STYLE, paramMap, Integer.class);
		return count > 0;
	}

	public static final class ResultRowMapper implements RowMapper<Result> {

		@Override
		public Result mapRow(ResultSet rs, int rowNum) throws SQLException {
			Result ret = new Result();
			fetchIdentEntity(rs, ret);
			ret.setResultTime(rs.getLong("result_time"));
			ret.setResultDate(transDate(rs.getDate("result_date")));
			ret.setDescription(rs.getString("description"));
			ret.setCourseParticipantUuid(UUID.fromString(rs.getString("course_participant_uuid")));

			CodeListItem style = new CodeListItem();
			style.setUuid(UUID.fromString(rs.getString("style_uuid")));
			style.setName(rs.getString("ci_name"));
			style.setDescription(rs.getString("ci_description"));

			ret.setStyle(style);
			ret.setDistance(rs.getInt("distance"));

			return ret;
		}
	}
}
