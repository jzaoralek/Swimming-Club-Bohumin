package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.TransactionDao;

import bank.fioclient.dto.AccountInfo;
import bank.fioclient.dto.Transaction;

@Repository
public class TransactionDaoImpl extends BaseJdbcDao implements TransactionDao {

	protected static final String ID_POHYBU_PARAM = "ID_POHYBU";
	
	private static final String COLLS_ALL = " protiucet_cisloUctu, protiucet_kodBanky, protiucet_nazevBanky, protiucet_nazevUctu, datumPohybu, objem, konstantniSymbol, variabilniSymbol, uzivatelskaIdentifikace, typ, mena, idPokynu, idPohybu, komentar, provedl, zpravaProPrijemnce "; 
	private static final String COLLS_ALL_INSERT = " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? "; 
	
	private static final String INSERT = "INSERT INTO bank_transaction ("+COLLS_ALL+ ") VALUES ("+COLLS_ALL_INSERT+")";
	private static final String SELECT_ALL = "SELECT " + COLLS_ALL
			+ " FROM bank_transaction "
			+ " ORDER BY datumPohybu DESC ";
	private static final String SELECT_BY_ID_POHYBU = "SELECT " + COLLS_ALL
			+ " FROM bank_transaction "
			+ " WHERE idPohybu = :" + ID_POHYBU_PARAM;
	private static final String SELECT_NOT_IN_PAYMENT_BY_DATUM_POHYBU_INTERVAL = "SELECT " + COLLS_ALL
			+ " FROM bank_transaction "
			+ " WHERE idpohybu NOT IN (SELECT bank_transaction_id_pohybu from payment p where p.type = 'BANK_TRANS' AND p.bank_transaction_id_pohybu IS NOT NULL) "
			+ " AND datumPohybu BETWEEN :"+DATE_FROM_PARAM+" AND :"+DATE_TO_PARAM
	        + " ORDER BY datumPohybu DESC ";
	private static final String SELECT_BY_DATUM_POHYBU_INTERVAL = "SELECT " + COLLS_ALL
			+ " FROM bank_transaction "
			+ " WHERE datumPohybu BETWEEN :"+DATE_FROM_PARAM+" AND :"+DATE_TO_PARAM
		    + " ORDER BY datumPohybu DESC ";
	
	private static final String SELECT_ALL_ID_POHYBU = "SELECT DISTINCT idPohybu from bank_transaction";
	
	@Autowired
	public TransactionDaoImpl(DataSource ds) {
		super(ds);
	}
	
	@Override
	public void insertBulk(final List<Transaction> accountStatementList) {
		namedJdbcTemplate.getJdbcOperations().batchUpdate(INSERT, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Transaction transaction = accountStatementList.get(i);
				ps.setString(1, transaction.getProtiucet() != null ? transaction.getProtiucet().getCisloUctu() : null);
				ps.setString(2, transaction.getProtiucet() != null ? transaction.getProtiucet().getKodBanky() : null);
				ps.setString(3, transaction.getProtiucet() != null ? transaction.getProtiucet().getNazevBanky() : null);
				ps.setString(4, transaction.getProtiucet() != null ? transaction.getProtiucet().getNazevUctu() : null);
				ps.setDate(5, transaction.getDatumPohybu() != null ? new java.sql.Date(transaction.getDatumPohybu().getTime().getTime()) : null);
				ps.setDouble(6, transaction.getObjem());
				ps.setString(7, transaction.getKonstantniSymbol());
				ps.setString(8, transaction.getVariabilniSymbol());
				ps.setString(9, transaction.getUzivatelskaIdentifikace());
				ps.setString(10, transaction.getTyp());
				ps.setString(11, transaction.getMena());
				ps.setLong(12, transaction.getIdPokynu());
				ps.setLong(13, transaction.getIdPohybu());
				ps.setString(14, transaction.getKomentar());
				ps.setString(15, transaction.getProvedl());
				ps.setString(16, transaction.getZpravaProPrijemnce());
			}

			@Override
			public int getBatchSize() {
				return accountStatementList.size();
			}
		  });
	}

	@Override
	public List<Transaction> getAll() {
		return namedJdbcTemplate.query(SELECT_ALL, new TransactionRowMapper());
	}

	@Override
	public Transaction getByIdPohybu(Long idPohybu) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(ID_POHYBU_PARAM, idPohybu);
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_ID_POHYBU, paramMap, new TransactionRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Transaction> getByInterval(Calendar dateFrom, Calendar dateTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource()
				.addValue(DATE_FROM_PARAM, dateFrom)
				.addValue(DATE_TO_PARAM, dateTo);
		return namedJdbcTemplate.query(SELECT_BY_DATUM_POHYBU_INTERVAL, paramMap, new TransactionRowMapper());
	}
	
	@Override
	public List<Transaction> getNotInPaymentByInterval(Calendar dateFrom, Calendar dateTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource()
				.addValue(DATE_FROM_PARAM, dateFrom)
				.addValue(DATE_TO_PARAM, dateTo);
		return namedJdbcTemplate.query(SELECT_NOT_IN_PAYMENT_BY_DATUM_POHYBU_INTERVAL, paramMap, new TransactionRowMapper());
	}

	@Override
	public Set<String> getAllIdPohybu() {
		return new HashSet<>(namedJdbcTemplate.getJdbcOperations().queryForList(SELECT_ALL_ID_POHYBU, String.class));		
	}
	
	public static final class TransactionRowMapper implements RowMapper<Transaction> {

		@Override
		public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
			Transaction ret = new Transaction();
			
			AccountInfo protiucet = new AccountInfo();
			protiucet.setCisloUctu(rs.getString("protiucet_cisloUctu"));
			protiucet.setKodBanky(rs.getString("protiucet_kodBanky"));
			protiucet.setNazevBanky(rs.getString("protiucet_nazevBanky"));
			protiucet.setNazevUctu(rs.getString("protiucet_nazevUctu"));
			ret.setProtiucet(protiucet);
			
			ret.setDatumPohybu(toCalendar(rs.getDate("datumPohybu")));
			ret.setObjem(rs.getDouble("objem"));
			ret.setKonstantniSymbol(rs.getString("konstantniSymbol"));
			ret.setVariabilniSymbol(rs.getString("variabilniSymbol"));
			ret.setUzivatelskaIdentifikace(rs.getString("uzivatelskaIdentifikace"));
			ret.setTyp(rs.getString("typ"));
			ret.setMena(rs.getString("mena"));
			ret.setIdPokynu(rs.getLong("idPokynu"));
			ret.setIdPohybu(rs.getLong("idPohybu"));
			ret.setKomentar(rs.getString("komentar"));
			ret.setProvedl(rs.getString("provedl"));
			ret.setZpravaProPrijemnce(rs.getString("zpravaProPrijemnce"));

			return ret;
		}
	}
}
