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
import com.jzaoralek.scb.dataservice.dao.PaymentDao;
import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.domain.Payment.PaymentType;

@Repository
public class PaymentDaoImpl extends BaseJdbcDao implements PaymentDao {

	private static final String AMOUNT_PARAM = "AMOUNT";
	private static final String TYPE_PARAM = "TYPE";
	private static final String COURSE_COURSE_PARTIC_UUID_PARAM = "COURSE_COURSE_PARTIC_UUID_PARAM";
	private static final String PAYMENT_DATE_PARAM = "payment_date";
	private static final String DATE_FROM_PARAM = "DATE_FROM";
	private static final String DATE_TO_PARAM = "DATE_TO";
	
	private static final String INSERT = "INSERT INTO payment " +
			"(uuid, amount, type, description, modif_at, modif_by, course_course_participant_uuid, payment_date) " +
			"VALUES (:"+UUID_PARAM+", :"+AMOUNT_PARAM+", :"+TYPE_PARAM+", :"+DESCRIPTION_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+", :"+COURSE_COURSE_PARTIC_UUID_PARAM+", :"+PAYMENT_DATE_PARAM+")";
	private static final String UPDATE = "UPDATE payment SET amount = :"+AMOUNT_PARAM+", type = :"+TYPE_PARAM+", description = :"+DESCRIPTION_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+", course_course_participant_uuid = :"+COURSE_COURSE_PARTIC_UUID_PARAM+", payment_date = :"+PAYMENT_DATE_PARAM+" WHERE uuid=:"+UUID_PARAM;
	private static final String DELETE = "DELETE FROM payment where uuid = :" + UUID_PARAM;
	private static final String SELECT_BY_UUID = "SELECT uuid, amount, type, description, modif_at, modif_by, course_course_participant_uuid, payment_date FROM payment WHERE uuid=:" + UUID_PARAM;
	private static final String SELECT_BY_COURSE_COURSE_PARTICIPANT_UUID = "SELECT uuid, amount, type, description, modif_at, modif_by, course_course_participant_uuid, payment_date "
			+ "FROM payment WHERE course_course_participant_uuid=:" + UUID_PARAM + " AND payment_date BETWEEN :"+DATE_FROM_PARAM+" AND :"+DATE_TO_PARAM +
			" ORDER BY payment_date desc";
	
	@Autowired
	public PaymentDaoImpl(DataSource ds) {
		super(ds);
	}
	
	@Override
	public Payment getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new PaymentRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Payment> getByCourseCourseParticipantUuid(UUID courseCourseParticipantUuid, Date from, Date to) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, courseCourseParticipantUuid.toString());
		paramMap.addValue(DATE_FROM_PARAM, from);
		paramMap.addValue(DATE_TO_PARAM, to);
		return namedJdbcTemplate.query(SELECT_BY_COURSE_COURSE_PARTICIPANT_UUID, paramMap, new PaymentRowMapper());
	}

	@Override
	public void insert(Payment payment) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(payment, paramMap);
		paramMap.addValue(AMOUNT_PARAM, payment.getAmount());
		paramMap.addValue(TYPE_PARAM, payment.getType().name());
		paramMap.addValue(DESCRIPTION_PARAM, payment.getDescription());
		paramMap.addValue(PAYMENT_DATE_PARAM, payment.getPaymentDate());
		paramMap.addValue(COURSE_COURSE_PARTIC_UUID_PARAM, payment.getCourseCourseParticipantUuid().toString());
		
		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public void update(Payment payment) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(payment, paramMap);
		paramMap.addValue(AMOUNT_PARAM, payment.getAmount());
		paramMap.addValue(TYPE_PARAM, payment.getType().name());
		paramMap.addValue(DESCRIPTION_PARAM, payment.getDescription());
		paramMap.addValue(PAYMENT_DATE_PARAM, payment.getPaymentDate());
		paramMap.addValue(COURSE_COURSE_PARTIC_UUID_PARAM, payment.getCourseCourseParticipantUuid().toString());
		
		namedJdbcTemplate.update(UPDATE, paramMap);
	}

	@Override
	public void delete(Payment course) {
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, course.getUuid().toString()));
	}
	
	public static final class PaymentRowMapper implements RowMapper<Payment> {

		@Override
		public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
			Payment ret = new Payment();
			fetchIdentEntity(rs, ret);
			ret.setAmount(rs.getLong("amount"));
			ret.setDescription(rs.getString("description"));
			ret.setType(PaymentType.valueOf(rs.getString("type")));
			ret.setCourseCourseParticipantUuid(UUID.fromString(rs.getString("course_course_participant_uuid")));
			ret.setPaymentDate(transDate(rs.getTimestamp("payment_date")));
			
			return ret;
		}
	}
}
