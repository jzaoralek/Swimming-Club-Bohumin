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
import com.jzaoralek.scb.dataservice.dao.CourseApplicationFileConfigDao;
import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;

@Repository
public class CourseApplicationFileConfigDaoImpl extends BaseJdbcDao implements CourseApplicationFileConfigDao {

	private static final String SELECT_FILE_BY_UUID = "select uuid, name, description, content, content_type from file where uuid=:" + UUID_PARAM;
	
	@Autowired
	protected CourseApplicationFileConfigDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<CourseApplicationFileConfig> getListForPage() {
		return null;
	}

	@Override
	public List<CourseApplicationFileConfig> getListForEmail() {
		return null;
	}

	@Override
	public Attachment getFileByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_FILE_BY_UUID, paramMap, new AttachmentRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public static final class AttachmentRowMapper implements RowMapper<Attachment> {

		@Override
		public Attachment mapRow(ResultSet rs, int rowNum) throws SQLException {
			Attachment ret = new Attachment();
			ret.setUuid(UUID.fromString(rs.getString(UUID_PARAM)));
			ret.setByteArray(rs.getBytes("content"));
			ret.setName(rs.getString("name"));
			ret.setDescription(rs.getString("description"));
			ret.setContentType(rs.getString("content_type"));

			return ret;
		}
	}

}
