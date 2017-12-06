package com.jzaoralek.scb.dataservice.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.jzaoralek.scb.dataservice.domain.IdentEntity;

public abstract class BaseJdbcDao {

	protected static final String UUID_PARAM = "UUID";
	protected static final String MODIF_AT_PARAM = "MODIF_AT";
	protected static final String MODIF_BY_PARAM = "MODIF_BY";
	protected static final String YEAR_FROM_PARAM = "YEAR_FROM";
	protected static final String YEAR_TO_PARAM = "YEAR_TO";
	protected static final String COURSE_UUID_PARAM = "COURSE_UUID";
	protected static final String LESSON_UUID_PARAM = "LESSON_UUID";
	protected static final String TIME_FROM_PARAM = "TIME_FROM";
	protected static final String TIME_TO_PARAM = "TIME_TO";
	protected static final String DESCRIPTION_PARAM = "description";
	protected static final String USER_UUID_PARAM = "USER_UUID";
	protected static final String DATE_FROM_PARAM = "DATE_FROM";
	protected static final String DATE_TO_PARAM = "DATE_TO";

	protected final JdbcTemplate jdbcTemplate;
	protected final NamedParameterJdbcTemplate namedJdbcTemplate;

	protected BaseJdbcDao(DataSource ds) {
		jdbcTemplate = new JdbcTemplate(ds);
		namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	}

	protected void fillIdentEntity(IdentEntity identEntity, MapSqlParameterSource paramMap) {
		paramMap.addValue(UUID_PARAM, identEntity.getUuid().toString());
		paramMap.addValue(MODIF_AT_PARAM, identEntity.getModifAt());
		paramMap.addValue(MODIF_BY_PARAM, identEntity.getModifBy());
	}

	protected static void fetchIdentEntity(ResultSet rs, IdentEntity identEntity) throws SQLException {
		identEntity.setUuid(UUID.fromString(rs.getString(UUID_PARAM)));
		identEntity.setModifAt(rs.getTimestamp(MODIF_AT_PARAM));
		identEntity.setModifBy(rs.getString(MODIF_BY_PARAM));
	}

	/** Pokud neni d instanci java.util.Date, vraci instanci java.util.Date se stejnou hodnotou
	 * (dle getTime).
	 *
	 * Pro null parametr nedochází vrací null
	 * @param d
	 * @return
	 */
	protected static Date transDate(Date d) {
		if (d!=null && d.getClass() != Date.class) {
			return new Date(d.getTime());
		} else {
			return d;
		}
	}
	
	public static Calendar toCalendar(Date date){ 
	  Calendar cal = Calendar.getInstance();
	  cal.setTime(date);
	  return cal;
	}
}