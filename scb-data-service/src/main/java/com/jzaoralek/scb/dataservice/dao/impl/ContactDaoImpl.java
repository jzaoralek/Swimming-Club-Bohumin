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
import com.jzaoralek.scb.dataservice.domain.AddressValidationStatus;
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
	private static final String EMAIL1_PARAM = "email1";
	private static final String EMAIL2_PARAM = "email2";
	private static final String PHONE1_PARAM = "phone1";
	private static final String PHONE2_PARAM = "phone2";
	private static final String REGION_PARAM = "region";
	private static final String ADDRESS_VALIDATION_STATUS_PARAM = "address_validation_status";
	private static final String FOREIGN_ADDRESS_PARAM = "foreign_address";
	private static final String EVIDENCE_NUMBER_PARAM = "evidence_number";

	private static final String CITIZENSHIP_PARAM = "citizenship";
	private static final String SEX_MALE_PARAM = "sex_male";

	
	private static final String INSERT = " INSERT INTO contact " +
		" (uuid, firstname, surname, citizenship, sex_male, street, land_registry_number, house_number, city, zip_code, region, evidence_number, foreign_address, address_validation_status, email1, email2, phone1, phone2, modif_at, modif_by) " +
		" VALUES " +
		" (:"+UUID_PARAM+", :"+FIRSTNAME_PARAM+", :"+SURNAME_PARAM+", :"+CITIZENSHIP_PARAM+", :"+SEX_MALE_PARAM+", :"+STREET_PARAM+", :"+LAND_REGISTRY_NUMBER_PARAM+", :"+HOUSE_NUMBER_PARAM+", :"+CITY_PARAM+", :"+ZIPCODE_PARAM+", :"+REGION_PARAM+", :"+EVIDENCE_NUMBER_PARAM+", :"+FOREIGN_ADDRESS_PARAM+", :"+ADDRESS_VALIDATION_STATUS_PARAM+", :"+EMAIL1_PARAM+", :"+EMAIL2_PARAM+", :"+PHONE1_PARAM+", :"+PHONE2_PARAM+", :"+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+") ";
	private static final String SELECT_BY_UUID = "SELECT uuid, firstname, surname, citizenship, sex_male, street, land_registry_number, house_number, city, zip_code, region, evidence_number, foreign_address, address_validation_status, email1, email2, phone1, phone2, modif_at, modif_by from contact WHERE uuid = :" + UUID_PARAM;
	private static final String SELECT_BY_EMAIL_COUNT = "SELECT count(*) FROM contact WHERE email1 = "+EMAIL1_PARAM;
	private static final String SELECT_BY_EMAIL = "SELECT uuid, firstname, surname, citizenship, sex_male, street, land_registry_number, house_number, city, zip_code, region, evidence_number, foreign_address, address_validation_status, email1, email2, phone1, phone2, modif_at, modif_by FROM contact WHERE email1 = :"+EMAIL1_PARAM;
	private static final String SELECT_EMAIl_ALL = "SELECT DISTINCT email1 FROM contact";
	private static final String DELETE = "DELETE FROM contact where uuid = :" + UUID_PARAM;
	private static final String UPDATE = "UPDATE contact SET firstname=:"+FIRSTNAME_PARAM+", surname=:"+SURNAME_PARAM+", citizenship=:"+CITIZENSHIP_PARAM+", sex_male=:"+SEX_MALE_PARAM+", street=:"+STREET_PARAM+", land_registry_number=:"+LAND_REGISTRY_NUMBER_PARAM+", house_number=:"+HOUSE_NUMBER_PARAM+", city=:"+CITY_PARAM+", zip_code=:"+ZIPCODE_PARAM+", region=:"+REGION_PARAM+", evidence_number=:"+EVIDENCE_NUMBER_PARAM+", foreign_address=:"+FOREIGN_ADDRESS_PARAM+", address_validation_status=:"+ADDRESS_VALIDATION_STATUS_PARAM+", email1=:"+EMAIL1_PARAM+", email2=:"+EMAIL2_PARAM+", phone1=:"+PHONE1_PARAM+", phone2=:"+PHONE2_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+" WHERE uuid=:"+UUID_PARAM;
	private static final String UPDATE_ADDRESS_VALID_STATUS = "UPDATE contact SET address_validation_status=:"+ADDRESS_VALIDATION_STATUS_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+" WHERE uuid=:"+UUID_PARAM;
	
	@Autowired
	public ContactDaoImpl(DataSource ds) {
		super(ds);
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
	public boolean existsByEmail(String email) {
		Long emailCount = namedJdbcTemplate.queryForObject(SELECT_BY_EMAIL_COUNT, new MapSqlParameterSource().addValue(EMAIL1_PARAM, email), Long.class);
		return emailCount > 0;
	}
	
	@Override
	public List<Contact> getByEmail(String email) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(EMAIL1_PARAM, email);
		return namedJdbcTemplate.query(SELECT_BY_EMAIL, paramMap, new ContactRowMapper());
	}
	
	@Override
	public List<String> getEmailAll() {
		return namedJdbcTemplate.getJdbcOperations().queryForList(SELECT_EMAIl_ALL, String.class);
	}
	
	@Override
	public void insert(Contact contact) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(contact, paramMap);
		paramMap.addValue(FIRSTNAME_PARAM, contact.getFirstname());
		paramMap.addValue(SURNAME_PARAM, contact.getSurname());
		paramMap.addValue(CITIZENSHIP_PARAM, contact.getCitizenship());
		paramMap.addValue(SEX_MALE_PARAM, contact.isSexMale() ? "1" : "0");
		paramMap.addValue(STREET_PARAM, contact.getStreet());
		paramMap.addValue(LAND_REGISTRY_NUMBER_PARAM, contact.getLandRegistryNumber());
		paramMap.addValue(HOUSE_NUMBER_PARAM, contact.getHouseNumber());
		paramMap.addValue(CITY_PARAM, contact.getCity());
		paramMap.addValue(ZIPCODE_PARAM, contact.getZipCode());
		paramMap.addValue(REGION_PARAM, contact.getRegion());
		paramMap.addValue(EVIDENCE_NUMBER_PARAM, contact.getEvidenceNumber());
		paramMap.addValue(FOREIGN_ADDRESS_PARAM, contact.getForeignAddress());
		paramMap.addValue(ADDRESS_VALIDATION_STATUS_PARAM, contact.getAddressValidationStatus() != null ? contact.getAddressValidationStatus().name() : AddressValidationStatus.NOT_VERIFIED.name());
		paramMap.addValue(EMAIL1_PARAM, contact.getEmail1());
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
		paramMap.addValue(CITIZENSHIP_PARAM, contact.getCitizenship());
		paramMap.addValue(SEX_MALE_PARAM, contact.isSexMale() ? "1" : "0");
		paramMap.addValue(STREET_PARAM, contact.getStreet());
		paramMap.addValue(LAND_REGISTRY_NUMBER_PARAM, contact.getLandRegistryNumber());
		paramMap.addValue(HOUSE_NUMBER_PARAM, contact.getHouseNumber());
		paramMap.addValue(CITY_PARAM, contact.getCity());
		paramMap.addValue(ZIPCODE_PARAM, contact.getZipCode());
		paramMap.addValue(REGION_PARAM, contact.getRegion());
		paramMap.addValue(EVIDENCE_NUMBER_PARAM, contact.getEvidenceNumber());
		paramMap.addValue(FOREIGN_ADDRESS_PARAM, contact.getForeignAddress());
		paramMap.addValue(ADDRESS_VALIDATION_STATUS_PARAM, contact.getAddressValidationStatus() != null ? contact.getAddressValidationStatus().name() : AddressValidationStatus.NOT_VERIFIED.name());
		paramMap.addValue(EMAIL1_PARAM, contact.getEmail1());
		paramMap.addValue(EMAIL2_PARAM, contact.getEmail2());
		paramMap.addValue(PHONE1_PARAM, contact.getPhone1());
		paramMap.addValue(PHONE2_PARAM, contact.getPhone2());

		namedJdbcTemplate.update(UPDATE, paramMap);
	}
	
	@Override
	public void updateAddressValidStatus(Contact contact) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(contact, paramMap);
		paramMap.addValue(ADDRESS_VALIDATION_STATUS_PARAM, contact.getAddressValidationStatus() != null ? contact.getAddressValidationStatus().name() : AddressValidationStatus.NOT_VERIFIED.name());
		
		namedJdbcTemplate.update(UPDATE_ADDRESS_VALID_STATUS, paramMap);
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
			ret.setSurname(rs.getString("surname"));
			ret.setCitizenship(rs.getString("citizenship"));
			ret.setSexMale(rs.getInt("sex_male") == 1);
			ret.setHouseNumber(rs.getString("house_number"));
			ret.setLandRegistryNumber(Long.valueOf(rs.getInt("land_registry_number")));
			ret.setRegion(rs.getString("region"));
			ret.setEvidenceNumber(rs.getString("evidence_number"));
			ret.setForeignAddress(rs.getString("foreign_address"));
			ret.setAddressValidationStatus(AddressValidationStatus.valueOf(rs.getString("address_validation_status")));
			ret.setPhone1(rs.getString("phone1"));
			ret.setPhone2(rs.getString("phone2"));
			ret.setStreet(rs.getString("street"));
			ret.setZipCode(rs.getString("zip_code"));

			return ret;
		}
	}
}