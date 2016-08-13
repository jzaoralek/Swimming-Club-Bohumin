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
import com.jzaoralek.scb.dataservice.domain.Contact;

@Repository
public class ContactDaoImpl extends BaseJdbcDao implements ContactDao {

	private static final String FIRSTNAME_PARAM = "firstname";
	private static final String SURNAME_PARAM = "surname";
	private static final String STREET_PARAM = "street";
	private static final String LAND_REGISTRY_NUMBER_PARAM = "land_registry_number";
	private static final String HOUSE_NUMBER_PARAM = "house_number";
	private static final String CITY_PARAM = "city";
	private static final String ZIPCODE_PARAM = "zip_code";
	private static final String EMAUL1_PARAM = "email1";
	private static final String EMAIL2_PARAM = "email2";
	private static final String PHONE1_PARAM = "phone1";
	private static final String PHONE2_PARAM = "phone2";

	private static final String INSERT = " INSERT INTO contact " +
		" (uuid, firstname, surname, street, land_registry_number, house_number, city, zip_code, email1, email2, phone1, phone2, modif_at, modif_by) " +
		" VALUES " +
		" (:"+UUID_PARAM+", :"+FIRSTNAME_PARAM+", :"+SURNAME_PARAM+", :"+STREET_PARAM+", :"+LAND_REGISTRY_NUMBER_PARAM+", :"+HOUSE_NUMBER_PARAM+", :"+CITY_PARAM+", :"+ZIPCODE_PARAM+", :"+EMAUL1_PARAM+", :"+EMAIL2_PARAM+", :"+PHONE1_PARAM+", :"+PHONE2_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+") ";
	private static final String SELECT_BY_UUID = "SELECT uuid, firstname, surname, street, land_registry_number, house_number, city, zip_code, email1, email2, phone1, phone2, modif_at, modif_by from contact WHERE uuid = :" + UUID_PARAM;
	private static final String DELETE = "DELETE FROM contact where uuid = :" + UUID_PARAM;
	private static final String UPDATE = "UPDATE contact SET firstname=:"+FIRSTNAME_PARAM+", surname=:"+SURNAME_PARAM+", street=:"+STREET_PARAM+", land_registry_number=:"+LAND_REGISTRY_NUMBER_PARAM+", house_number=:"+HOUSE_NUMBER_PARAM+", city=:"+CITY_PARAM+", zip_code=:"+ZIPCODE_PARAM+", email1=:"+EMAUL1_PARAM+", email2=:"+EMAIL2_PARAM+", phone1=:"+PHONE1_PARAM+", phone2=:"+PHONE2_PARAM+" WHERE uuid=:"+UUID_PARAM;

	@Autowired
	public ContactDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<Contact> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact getByUuid(UUID uuid) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new ContactRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void insert(Contact contact) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(contact, paramMap);
		paramMap.addValue(FIRSTNAME_PARAM, contact.getFirstname());
		paramMap.addValue(SURNAME_PARAM, contact.getSurname());
		paramMap.addValue(STREET_PARAM, contact.getStreet());
		paramMap.addValue(LAND_REGISTRY_NUMBER_PARAM, contact.getLandRegistryNumber());
		paramMap.addValue(HOUSE_NUMBER_PARAM, contact.getHouseNumber());
		paramMap.addValue(CITY_PARAM, contact.getCity());
		paramMap.addValue(ZIPCODE_PARAM, contact.getZipCode());
		paramMap.addValue(EMAUL1_PARAM, contact.getEmail1());
		paramMap.addValue(EMAIL2_PARAM, contact.getEmail2());
		paramMap.addValue(PHONE1_PARAM, contact.getPhone1());
		paramMap.addValue(PHONE2_PARAM, contact.getPhone2());

		namedJdbcTemplate.update(INSERT, paramMap);

	}

	@Override
	public void update(Contact contact) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(contact, paramMap);
		paramMap.addValue(FIRSTNAME_PARAM, contact.getFirstname());
		paramMap.addValue(SURNAME_PARAM, contact.getSurname());
		paramMap.addValue(STREET_PARAM, contact.getStreet());
		paramMap.addValue(LAND_REGISTRY_NUMBER_PARAM, contact.getLandRegistryNumber());
		paramMap.addValue(HOUSE_NUMBER_PARAM, contact.getHouseNumber());
		paramMap.addValue(CITY_PARAM, contact.getCity());
		paramMap.addValue(ZIPCODE_PARAM, contact.getZipCode());
		paramMap.addValue(EMAUL1_PARAM, contact.getEmail1());
		paramMap.addValue(EMAIL2_PARAM, contact.getEmail2());
		paramMap.addValue(PHONE1_PARAM, contact.getPhone1());
		paramMap.addValue(PHONE2_PARAM, contact.getPhone2());

		namedJdbcTemplate.update(UPDATE, paramMap);
	}

	@Override
	public void delete(Contact contact) {
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, contact.getUuid().toString()));
	}

	public static final class ContactRowMapper implements RowMapper<Contact> {
		@Override
		public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
			Contact ret = new Contact();
			fetchIdentEntity(rs, ret);
			ret.setCity(rs.getString("city"));
			ret.setEmail1(rs.getString("email1"));
			ret.setEmail2(rs.getString("email2"));
			ret.setFirstname(rs.getString("firstname"));
			ret.setHouseNumber(rs.getShort("house_number"));
			ret.setLandRegistryNumber(rs.getInt("land_registry_number"));
			ret.setPhone1(rs.getString("phone1"));
			ret.setPhone2(rs.getString("phone2"));
			ret.setStreet(rs.getString("street"));
			ret.setZipCode(rs.getString("zip_code"));
			ret.setSurname(rs.getString("surname"));

			return ret;
		}
	}
}