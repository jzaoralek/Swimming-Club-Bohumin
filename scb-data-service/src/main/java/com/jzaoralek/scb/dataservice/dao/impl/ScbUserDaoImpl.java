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
import com.jzaoralek.scb.dataservice.dao.ContactDao;
import com.jzaoralek.scb.dataservice.dao.ScbUserDao;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;

@Repository
public class ScbUserDaoImpl extends BaseJdbcDao implements ScbUserDao {

	private static final String USERNAME_PARAM = "username";
	private static final String PASSWORD_PARAM = "password";
	private static final String PASSWORD_GENERATED_PARAM = "password_generated";
	private static final String ROLE_PARAM = "role";
	private static final String CONTACT_PARAM = "contact_uuid";

	private static final String INSERT = "INSERT INTO user " +
	"(uuid, username, password, password_generated, role, contact_uuid, modif_at, modif_by) " +
	"VALUES (:"+UUID_PARAM+", :"+USERNAME_PARAM+", :"+PASSWORD_PARAM+", :"+PASSWORD_GENERATED_PARAM+", :"+ROLE_PARAM+", :"+CONTACT_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+")";
	private static final String SELECT_BY_UUID ="SELECT uuid, username, password, password_generated, role, contact_uuid, modif_at, modif_by FROM user WHERE uuid=:" + UUID_PARAM;
	private static final String DELETE = "DELETE FROM user where uuid = :" + UUID_PARAM;

	@Autowired
	private ContactDao contactDao;

	@Autowired
	public ScbUserDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<ScbUser> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScbUser getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new ScbUserRowMapper(contactDao));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void insert(ScbUser scbUser) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(scbUser, paramMap);
		paramMap.addValue(USERNAME_PARAM, scbUser.getUsername());
		paramMap.addValue(PASSWORD_PARAM, scbUser.getPassword());
		paramMap.addValue(PASSWORD_GENERATED_PARAM, scbUser.isPasswordGenerated() ? "1" : "0");
		paramMap.addValue(ROLE_PARAM, scbUser.getRole() != null ? scbUser.getRole().name() : null);
		paramMap.addValue(CONTACT_PARAM, scbUser.getContact().getUuid() != null ? scbUser.getContact().getUuid().toString() : null);

		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public void update(ScbUser scbUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(ScbUser scbUser) {
		contactDao.delete(scbUser.getContact());
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, scbUser.getUuid().toString()));
	}

	public static final class ScbUserRowMapper implements RowMapper<ScbUser> {

		private ContactDao contactDao;

		public ScbUserRowMapper(ContactDao contactDao) {
			this.contactDao = contactDao;
		}

		@Override
		public ScbUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ScbUser ret = new ScbUser();
			fetchIdentEntity(rs, ret);

			UUID contactUuid = rs.getString("contact_uuid") != null ? UUID.fromString(rs.getString("contact_uuid")) : null;
			if (contactUuid != null) {
				ret.setContact(contactDao.getByUuid(contactUuid));
			}
			ret.setPassword(rs.getString("password"));
			ret.setPasswordGenerated(rs.getInt("password_generated") == 1);
			ret.setRole(ScbUserRole.valueOf(rs.getString("role")));
			ret.setUsername(rs.getString("username"));

			return ret;
		}
	}

}
