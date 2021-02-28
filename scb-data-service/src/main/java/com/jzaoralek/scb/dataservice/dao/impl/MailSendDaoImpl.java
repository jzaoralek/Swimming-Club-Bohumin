package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.MailSendDao;
import com.jzaoralek.scb.dataservice.domain.MailSend;

@Repository
public class MailSendDaoImpl extends BaseJdbcDao implements MailSendDao {
	
	private static final String MAIL_TO_PARAM = "MAIL_TO";
	private static final String MAIL_CC_PARAM = "MAIL_CC";
	private static final String MAIL_SUBJECT_PARAM = "MAIL_SUBJECT";
	private static final String MAIL_TEXT_PARAM = "MAIL_TEXT";
	private static final String SUCCESS_PARAM = "SUCCESS";
	private static final String ATTACHMENTS_PARAM = "ATTACHMENTS";
	private static final String HTML_PARAM = "HTML";
	
	private static final String INSERT = "INSERT INTO mail_message_send(uuid, mail_to, mail_cc, mail_subject, mail_text, success, description, attachments, html, modif_at, modif_by)"
			+ "VALUES (:"+UUID_PARAM+", :"+MAIL_TO_PARAM+", :"+MAIL_CC_PARAM+", :"+MAIL_SUBJECT_PARAM+", :"+MAIL_TEXT_PARAM+", :"+SUCCESS_PARAM+", :"+DESCRIPTION_PARAM+", :"+ATTACHMENTS_PARAM+", :"+HTML_PARAM+",:"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+")";
	private static final String SELECT_BY_DATE_INTERVAL = "SELECT uuid, mail_to, mail_cc, mail_subject, success, description, attachments, html, modif_at, modif_by "
			+ " FROM mail_message_send WHERE modif_at BETWEEN :"+DATE_FROM_PARAM+" AND :"+DATE_TO_PARAM +
			" ORDER BY modif_at desc";
	private static final String SELECT_BY_UUID = "SELECT * FROM mail_message_send WHERE uuid=:" + UUID_PARAM;
	
	@Autowired
	protected MailSendDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public void insert(MailSend mail) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(mail, paramMap);
		paramMap.addValue(MAIL_TO_PARAM, mail.getTo());
		paramMap.addValue(MAIL_CC_PARAM, mail.getCc());
		paramMap.addValue(MAIL_SUBJECT_PARAM, mail.getSubject());
		paramMap.addValue(MAIL_TEXT_PARAM, mail.getText());
		paramMap.addValue(SUCCESS_PARAM, mail.isSuccess() ? "1" : "0");
		paramMap.addValue(DESCRIPTION_PARAM, mail.getDescription());
		paramMap.addValue(ATTACHMENTS_PARAM, mail.isAttachments() ? "1" : "0");
		paramMap.addValue(HTML_PARAM, mail.isHtml() ? "1" : "0");
		
		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public List<MailSend> getByDateInterval(Date from, Date to) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue(DATE_FROM_PARAM, from);
		paramMap.addValue(DATE_TO_PARAM, to);
		return namedJdbcTemplate.query(SELECT_BY_DATE_INTERVAL, paramMap, new MailSendRowMapper(false));
	}
	
	@Override
	public MailSend getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new MailSendRowMapper(true));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public static final class MailSendRowMapper implements RowMapper<MailSend> {
		
		private final boolean inclText;
		
		public MailSendRowMapper(boolean inclText) {
			this.inclText = inclText;
		}
		
		@Override
		public MailSend mapRow(ResultSet rs, int rowNum) throws SQLException {
			String mailTo = rs.getString("mail_to");
			String mail_cc = rs.getString("mail_cc");
			String mail_subject = rs.getString("mail_subject");
			String mail_text = inclText ? rs.getString("mail_text") : null;
			
			MailSend ret = new MailSend(mailTo, mail_cc, mail_subject, mail_text, null);
			fetchIdentEntity(rs, ret);
			ret.setSuccess(fetchBoolean(rs.getInt("success")));
			ret.setDescription(rs.getString("description"));
			ret.setAttachments(fetchBoolean(rs.getInt("attachments")));
			ret.setHtml(fetchBoolean(rs.getInt("html")));
			
			return ret;
		}
	}
}
