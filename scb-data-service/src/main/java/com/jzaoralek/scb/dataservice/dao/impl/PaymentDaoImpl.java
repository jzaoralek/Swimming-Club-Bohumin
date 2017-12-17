package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.CourseDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.dao.PaymentDao;
import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.domain.Payment.PaymentProcessType;
import com.jzaoralek.scb.dataservice.domain.Payment.PaymentType;

@Repository
public class PaymentDaoImpl extends BaseJdbcDao implements PaymentDao {

	private static final String AMOUNT_PARAM = "AMOUNT";
	private static final String TYPE_PARAM = "TYPE";
	
	private static final String COURSE_PARTIC_UUID_PARAM = "COURSE_PARTIC_UUID_PARAM";
	private static final String COURSE_UUID_PARAM = "COURSE_UUID_PARAM";
	private static final String BANK_TRANSACTION_ID_POHYBU = "bank_transaction_id_pohybu";
	
	private static final String PAYMENT_DATE_PARAM = "payment_date";
	private static final String PROCESS_TYPE_PARAM = "PROCESS_TYPE";
	
	private static final String INSERT = "INSERT INTO payment " +
			"(uuid, amount, type, description, modif_at, modif_by, course_participant_uuid, course_uuid, payment_date, process_type, bank_transaction_id_pohybu) " +
			"VALUES (:"+UUID_PARAM+", :"+AMOUNT_PARAM+", :"+TYPE_PARAM+", :"+DESCRIPTION_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+", :"+COURSE_PARTIC_UUID_PARAM+", :"+COURSE_UUID_PARAM+", :"+PAYMENT_DATE_PARAM+", :"+PROCESS_TYPE_PARAM+", :"+BANK_TRANSACTION_ID_POHYBU+")"; 
	private static final String UPDATE = "UPDATE payment SET amount = :"+AMOUNT_PARAM+", type = :"+TYPE_PARAM+", description = :"+DESCRIPTION_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+", course_participant_uuid = :"+COURSE_PARTIC_UUID_PARAM+", "
			+ "course_uuid = :"+COURSE_UUID_PARAM+", payment_date = :"+PAYMENT_DATE_PARAM+", process_type = :"+PROCESS_TYPE_PARAM+", bank_transaction_id_pohybu = :"+BANK_TRANSACTION_ID_POHYBU+" WHERE uuid=:"+UUID_PARAM;
	private static final String DELETE = "DELETE FROM payment where uuid = :" + UUID_PARAM;
	private static final String SELECT_BY_UUID = "SELECT uuid, amount, type, description, modif_at, modif_by, course_participant_uuid, course_uuid, payment_date, process_type, bank_transaction_id_pohybu FROM payment WHERE uuid=:" + UUID_PARAM;
	private static final String SELECT_BY_COURSE_COURSE_PARTICIPANT_UUID = "SELECT uuid, amount, type, description, modif_at, modif_by, course_participant_uuid, course_uuid, payment_date, process_type, bank_transaction_id_pohybu "
			+ "FROM payment WHERE course_participant_uuid=:" + COURSE_PARTIC_UUID_PARAM + " AND course_uuid=:" + COURSE_UUID_PARAM + " AND payment_date BETWEEN :"+DATE_FROM_PARAM+" AND :"+DATE_TO_PARAM +
			" ORDER BY payment_date desc";
	private static final String SELECT_BANK_PAYMENT_BY_DATE_INTERVAL = "SELECT uuid, amount, type, description, modif_at, modif_by, course_participant_uuid, course_uuid, payment_date, process_type, bank_transaction_id_pohybu "
			+ "FROM payment WHERE type = 'BANK_TRANS' AND payment_date BETWEEN :"+DATE_FROM_PARAM+" AND :"+DATE_TO_PARAM +
			" ORDER BY payment_date desc";
	private static final String SELECT_ALL_BANK_TRANS_ID_POHYBU = "SELECT DISTINCT bank_transaction_id_pohybu from payment WHERE bank_transaction_id_pohybu IS NOT NULL";
	
	@Autowired
	private CourseDao courseDao;
	
	@Autowired
	private CourseParticipantDao courseParticipantDao;
	
	@Autowired
	public PaymentDaoImpl(DataSource ds) {
		super(ds);
	}
	
	@Override
	public Payment getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new PaymentRowMapper(courseDao, courseParticipantDao));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Payment> getByCourseCourseParticipantUuid(UUID courseCourseParticipantUuid, UUID courseUuid, Date from, Date to) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource()
				.addValue(COURSE_PARTIC_UUID_PARAM, courseCourseParticipantUuid.toString())
				.addValue(COURSE_UUID_PARAM, courseUuid.toString());
		paramMap.addValue(DATE_FROM_PARAM, from);
		paramMap.addValue(DATE_TO_PARAM, to);
		return namedJdbcTemplate.query(SELECT_BY_COURSE_COURSE_PARTICIPANT_UUID, paramMap, new PaymentRowMapper(courseDao, courseParticipantDao));
	}
	
	@Override
	public List<Payment> getBankPaymentByDateInterval(Date from, Date to) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue(DATE_FROM_PARAM, from);
		paramMap.addValue(DATE_TO_PARAM, to);
		return namedJdbcTemplate.query(SELECT_BANK_PAYMENT_BY_DATE_INTERVAL, paramMap, new PaymentRowMapper(courseDao, courseParticipantDao));
	}

	@Override
	public void insert(Payment payment) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(payment, paramMap);
		paramMap.addValue(AMOUNT_PARAM, payment.getAmount());
		paramMap.addValue(TYPE_PARAM, payment.getType().name());
		paramMap.addValue(PROCESS_TYPE_PARAM, payment.getProcessType().name());
		paramMap.addValue(DESCRIPTION_PARAM, payment.getDescription());
		paramMap.addValue(PAYMENT_DATE_PARAM, payment.getPaymentDate());
		paramMap.addValue(BANK_TRANSACTION_ID_POHYBU, payment.getBankTransactionIdPohybu());
		paramMap.addValue(COURSE_PARTIC_UUID_PARAM, payment.getCourseParticipant().getUuid().toString());
		paramMap.addValue(COURSE_UUID_PARAM, payment.getCourse().getUuid().toString());
		
		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public void update(Payment payment) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(payment, paramMap);
		paramMap.addValue(AMOUNT_PARAM, payment.getAmount());
		paramMap.addValue(TYPE_PARAM, payment.getType().name());
		paramMap.addValue(PROCESS_TYPE_PARAM, payment.getProcessType().name());
		paramMap.addValue(DESCRIPTION_PARAM, payment.getDescription());
		paramMap.addValue(PAYMENT_DATE_PARAM, payment.getPaymentDate());
		paramMap.addValue(COURSE_PARTIC_UUID_PARAM, payment.getCourseParticipant().getUuid().toString());
		paramMap.addValue(COURSE_UUID_PARAM, payment.getCourse().getUuid().toString());
		paramMap.addValue(BANK_TRANSACTION_ID_POHYBU, payment.getBankTransactionIdPohybu());
		
		namedJdbcTemplate.update(UPDATE, paramMap);
	}

	@Override
	public void delete(Payment course) {
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, course.getUuid().toString()));
	}
	
	@Override
	public Set<String> getAllBankTransIdPohybu() {
		return new HashSet<>(namedJdbcTemplate.getJdbcOperations().queryForList(SELECT_ALL_BANK_TRANS_ID_POHYBU, String.class));
	}
	
	public static final class PaymentRowMapper implements RowMapper<Payment> {

		private CourseDao courseDao;
		private CourseParticipantDao courseParticipantDao;
		
		public PaymentRowMapper(CourseDao courseDao, CourseParticipantDao courseParticipantDao) {
			this.courseDao = courseDao;
			this.courseParticipantDao = courseParticipantDao;
		}
		
		@Override
		public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
			Payment ret = new Payment();
			fetchIdentEntity(rs, ret);
			ret.setAmount(rs.getLong("amount"));
			ret.setDescription(rs.getString("description"));
			ret.setType(PaymentType.valueOf(rs.getString("type")));
			ret.setBankTransactionIdPohybu(rs.getLong("bank_transaction_id_pohybu"));
			UUID courseParticipantUuid = UUID.fromString(rs.getString("course_participant_uuid"));
			if (courseParticipantUuid != null) {
				ret.setCourseParticipant(courseParticipantDao.getByUuid(courseParticipantUuid, false));
			}
			UUID courseUuid = UUID.fromString(rs.getString("course_uuid"));
			if (courseUuid != null) {
				ret.setCourse(courseDao.getPlainByUuid(courseUuid));
			}
			
			ret.setPaymentDate(transDate(rs.getTimestamp("payment_date")));
			ret.setProcessType(PaymentProcessType.valueOf(rs.getString("process_type")));
			
			return ret;
		}
	}
}
