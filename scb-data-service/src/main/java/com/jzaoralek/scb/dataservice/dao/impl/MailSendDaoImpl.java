package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
	private static final String MAIL_TO_COMPLETE_NAME_PARAM = "MAIL_TO_COMPLETE_NAME";
	
	private static final String INSERT = "INSERT INTO mail_message_send(uuid, mail_to, mail_cc, mail_subject, mail_text, success, description, attachments, html, modif_at, modif_by, mail_to_complete_name)"
			+ "VALUES (:"+UUID_PARAM+", :"+MAIL_TO_PARAM+", :"+MAIL_CC_PARAM+", :"+MAIL_SUBJECT_PARAM+", :"+MAIL_TEXT_PARAM+", :"+SUCCESS_PARAM+", :"+DESCRIPTION_PARAM+", :"+ATTACHMENTS_PARAM+", :"+HTML_PARAM+",:"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+", :"+MAIL_TO_COMPLETE_NAME_PARAM+")";
	private static final String SELECT_MAIL_SEND_BASE = "SELECT uuid, mail_to, mail_cc, mail_subject, success, description, attachments, html, modif_at, modif_by, mail_to_complete_name "
			+ " FROM mail_message_send WHERE modif_at BETWEEN :"+DATE_FROM_PARAM+" AND :"+DATE_TO_PARAM;
			;
	private static final String SELECT_MAIL_TO_WHERE_CLAUSE = " AND mail_to LIKE :"+MAIL_TO_PARAM;
	private static final String SELECT_MAIL_SUBJECT_WHERE_CLAUSE = " AND mail_subject LIKE :"+MAIL_SUBJECT_PARAM;
	private static final String SELECT_MAIL_TEXT_WHERE_CLAUSE = " AND mail_text LIKE :"+MAIL_TEXT_PARAM;
	private static final String SELECT_MAIL_ORDER_BY_CLAUSE = " ORDER BY modif_at desc";
	private static final String SELECT_BY_UUID = "SELECT * FROM mail_message_send WHERE uuid=:" + UUID_PARAM;
	private static final String DELETE_BY_UUIDS = "DELETE FROM mail_message_send WHERE uuid IN ( :uuids ) ";
	
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
		paramMap.addValue(MAIL_TO_COMPLETE_NAME_PARAM, mail.getToCompleteName());
		
		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public List<MailSend> getMailSendListByCriteria(Date dateFrom, 
			Date dateTo, 
			String mailTo,
			String mailSubject,
			String mailText) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue(DATE_FROM_PARAM, dateFrom);
		paramMap.addValue(DATE_TO_PARAM, dateTo);
		
		StringBuilder sb = new StringBuilder(SELECT_MAIL_SEND_BASE);
		
		if (StringUtils.hasText(mailTo)) {
			paramMap.addValue(MAIL_TO_PARAM, "%" + mailTo.toLowerCase().trim() + "%");
			sb.append(SELECT_MAIL_TO_WHERE_CLAUSE);
		}
		
		if (StringUtils.hasText(mailSubject)) {
			paramMap.addValue(MAIL_SUBJECT_PARAM, "%" + mailSubject.toLowerCase().trim() + "%");
			sb.append(SELECT_MAIL_SUBJECT_WHERE_CLAUSE);
		}
		
		if (StringUtils.hasText(mailText)) {
			paramMap.addValue(MAIL_TEXT_PARAM, "%" + mailText.toLowerCase().trim() + "%");
			sb.append(SELECT_MAIL_TEXT_WHERE_CLAUSE);
		}
		
		sb.append(SELECT_MAIL_ORDER_BY_CLAUSE);
		
		return namedJdbcTemplate.query(sb.toString(), paramMap, new MailSendRowMapper(false));
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
	
	@Override
	public void delete(List<MailSend> mailSendList) {
		List<String> uuidList = new ArrayList<>();
		for (MailSend item : mailSendList) {
			uuidList.add(item.getUuid().toString());
		}
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("uuids", uuidList);
		
		namedJdbcTemplate.update(DELETE_BY_UUIDS, paramMap);			
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
			String mailToCompleteName = rs.getString("mail_to_complete_name");
			
			MailSend ret = new MailSend(mailTo, mail_cc, mail_subject, mail_text, null, mailToCompleteName);
			fetchIdentEntity(rs, ret);
			ret.setSuccess(fetchBoolean(rs.getInt("success")));
			ret.setDescription(rs.getString("description"));
			ret.setAttachments(fetchBoolean(rs.getInt("attachments")));
			ret.setHtml(fetchBoolean(rs.getInt("html")));
			
			return ret;
		}
	}
}
