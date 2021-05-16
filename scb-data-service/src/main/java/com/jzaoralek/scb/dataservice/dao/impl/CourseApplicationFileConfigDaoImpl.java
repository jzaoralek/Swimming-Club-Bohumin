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

	private static final String TYPE_PARAM = "type";
	private static final String FILE_UUID_PARAM = "file_uuid";
	private static final String PAGE_TEXT_PARAM = "page_text";
	private static final String PAGE_ATTACH_PARAM = "page_attachment";
	private static final String EMAIl_ATTACH_PARAM = "email_attachment";
	private static final String DESCRIPTION_PARAM = "description";
	
	private static final String NAME_PARAM = "name";
	private static final String CONTENT_PARAM = "content";
	private static final String CONTENT_TYPE_PARAM = "content_type";
	
	private static final String SELECT_FILE_BY_UUID = "select uuid, name, description, content, content_type from file where uuid=:" + UUID_PARAM;
	private static final String SELECT_ALL_COLLUMNS_BASE = "SELECT uuid, type, file_uuid, page_text, page_attachment, email_attachment, description, modif_at, modif_by FROM course_application_file_config ";
	private static final String SELECT_FOR_PAGE = SELECT_ALL_COLLUMNS_BASE + " WHERE page_text = '1'";
	private static final String SELECT_FOR_EMAIL = SELECT_ALL_COLLUMNS_BASE + "WHERE email_attachment = '1'";
	private static final String SELECT_ALL = SELECT_ALL_COLLUMNS_BASE + " ORDER BY type";
	private static final String SELECT_BY_UUID = SELECT_ALL_COLLUMNS_BASE + " where uuid=:" + UUID_PARAM;
	
	private static final String INSERT_COURSE_APPL_FILE_CONFIG = "INSERT INTO course_application_file_config"
			+ "(uuid,type,file_uuid,page_text,page_attachment,email_attachment,description,modif_at,modif_by)"
			+ "VALUES"
			+ "(:"+UUID_PARAM+",:"+TYPE_PARAM+",:"+FILE_UUID_PARAM+",:"+PAGE_TEXT_PARAM+",:"+PAGE_ATTACH_PARAM+",:"+EMAIl_ATTACH_PARAM+",:"+DESCRIPTION_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+")";
	private static final String INSERT_FILE = "INSERT INTO file (uuid,name,description,content,content_type)\n"
			+ "VALUES\n"
			+ "(:"+UUID_PARAM+",:"+NAME_PARAM+",:"+DESCRIPTION_PARAM+",:"+CONTENT_PARAM+",:"+CONTENT_TYPE_PARAM+")";
	
	private static final String UPDATE_COURSE_APPL_FILE_CONFIG = "UPDATE course_application_file_config SET "
			+ "uuid = :"+UUID_PARAM+",type = :"+TYPE_PARAM+",file_uuid = :"+FILE_UUID_PARAM+", "
			+ "page_text = :"+PAGE_TEXT_PARAM+",page_attachment = :"+PAGE_ATTACH_PARAM+","
			+ "email_attachment = :"+EMAIl_ATTACH_PARAM+",description = :"+DESCRIPTION_PARAM+","
			+ "modif_at = :"+MODIF_AT_PARAM+",modif_by = :"+MODIF_BY_PARAM+" WHERE uuid = :"+UUID_PARAM;
	
	private static final String DELETE_COURSE_APPL_FILE_CONFIG = "DELETE FROM course_application_file_config WHERE uuid = :" + UUID_PARAM;
	private static final String DELETE_FILE = "DELETE FROM file WHERE uuid = :" + UUID_PARAM;
	
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
	
	@Override
	public List<CourseApplicationFileConfig> getAll() {
		return namedJdbcTemplate.query(SELECT_ALL, new CourseApplicationFileConfigRowMapper(false, this));
	}
	
	@Override
	public CourseApplicationFileConfig getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new CourseApplicationFileConfigRowMapper(true, this));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void insert(CourseApplicationFileConfig config) {
		namedJdbcTemplate.update(INSERT_COURSE_APPL_FILE_CONFIG, buildCourseApplFileConfigParamMap(config));
		insertFile(config.getAttachment());
	}

	@Override
	public void update(CourseApplicationFileConfig config) {
		namedJdbcTemplate.update(UPDATE_COURSE_APPL_FILE_CONFIG, buildCourseApplFileConfigParamMap(config));
		deleteFile(config.getAttachmentUuid());
		insertFile(config.getAttachment());
	}
	
	private MapSqlParameterSource buildCourseApplFileConfigParamMap(CourseApplicationFileConfig config) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(config, paramMap);
		paramMap.addValue(TYPE_PARAM, config.getType().name());
		paramMap.addValue(FILE_UUID_PARAM, config.getAttachmentUuid() != null 
													? config.getAttachmentUuid().toString() 
													: null);
		paramMap.addValue(PAGE_TEXT_PARAM, config.isPageText() ? "1" : "0");
		paramMap.addValue(PAGE_ATTACH_PARAM, config.isPageAttachment() ? "1" : "0");
		paramMap.addValue(EMAIl_ATTACH_PARAM, config.isEmailAttachment() ? "1" : "0");
		paramMap.addValue(DESCRIPTION_PARAM, config.getDescription());
		
		return paramMap;
	}
	
	private MapSqlParameterSource buildFileParamMap(Attachment file) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue(UUID_PARAM, file.getUuid().toString());
		paramMap.addValue(NAME_PARAM, file.getName());
		paramMap.addValue(DESCRIPTION_PARAM, file.getDescription());
		paramMap.addValue(CONTENT_PARAM, file.getByteArray());
		paramMap.addValue(CONTENT_TYPE_PARAM, file.getContentType());
		
		return paramMap;
	}

	@Override
	public void delete(CourseApplicationFileConfig config) {
		deleteFile(config.getAttachmentUuid());
		namedJdbcTemplate.update(DELETE_COURSE_APPL_FILE_CONFIG, new MapSqlParameterSource().addValue(UUID_PARAM, config.getUuid().toString()));
	}
	
	private void deleteFile(UUID attachmentUuid) {
		namedJdbcTemplate.update(DELETE_FILE, new MapSqlParameterSource().addValue(UUID_PARAM, attachmentUuid.toString()));
	}
	
	private void insertFile(Attachment file) {
		namedJdbcTemplate.update(INSERT_FILE, buildFileParamMap(file));
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
