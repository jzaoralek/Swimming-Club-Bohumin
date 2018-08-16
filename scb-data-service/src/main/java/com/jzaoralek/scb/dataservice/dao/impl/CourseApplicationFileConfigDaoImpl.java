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
import com.jzaoralek.scb.dataservice.dao.impl.CourseApplicationDaoImpl.CourseApplicationRowMapper;
import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Config;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;

@Repository
public class CourseApplicationFileConfigDaoImpl extends BaseJdbcDao implements CourseApplicationFileConfigDao {

	private static final String SELECT_FILE_BY_UUID = "select uuid, name, description, content, content_type from file where uuid=:" + UUID_PARAM;
	private static final String SELECT_ALL_COLLUMNS_BASE = "SELECT uuid, type, file_uuid, page_text, page_attachment, email_attachment, description, modif_at, modif_by FROM course_application_file_config ";
	private static final String SELECT_FOR_PAGE = SELECT_ALL_COLLUMNS_BASE + " WHERE page_attachment = '1'";
	private static final String SELECT_FOR_EMAIL = SELECT_ALL_COLLUMNS_BASE + "WHERE email_attachment = '1'";
					
	
	@Autowired
	protected CourseApplicationFileConfigDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<CourseApplicationFileConfig> getListForPage() {
		return namedJdbcTemplate.query(SELECT_FOR_PAGE, new CourseApplicationFileConfigRowMapper(false, this));
	}

	@Override
	public List<CourseApplicationFileConfig> getListForEmail() {
		return namedJdbcTemplate.query(SELECT_FOR_EMAIL, new CourseApplicationFileConfigRowMapper(true, this));
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
	
	public static final class CourseApplicationFileConfigRowMapper implements RowMapper<CourseApplicationFileConfig> {
		private boolean loadAttachment;
		private CourseApplicationFileConfigDao dao;
		
		public CourseApplicationFileConfigRowMapper(boolean loadAttachment, CourseApplicationFileConfigDao dao) {
			super();
			this.loadAttachment = loadAttachment;
			this.dao = dao;
		}

		@Override
		public CourseApplicationFileConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseApplicationFileConfig ret = new CourseApplicationFileConfig();
			fetchIdentEntity(rs, ret);
			ret.setAttachmentUuid(UUID.fromString(rs.getString("file_uuid")));
			if (ret.getAttachmentUuid() != null && this.loadAttachment) {
				ret.setAttachment(dao.getFileByUuid(ret.getAttachmentUuid()));				
			}
			ret.setDescription(rs.getString("description"));
			ret.setEmailAttachment(rs.getInt("email_attachment") == 1);
			ret.setPageAttachment(rs.getInt("page_attachment") == 1);
			ret.setPageText(rs.getInt("page_text") == 1);
			ret.setType(CourseApplicationFileConfig.CourseApplicationFileType.valueOf(rs.getString("type")));

			return ret;
		}
	}
}
