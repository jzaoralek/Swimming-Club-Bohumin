package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;

@Repository
public class CourseParticipantDaoImpl extends BaseJdbcDao implements CourseParticipantDao {

	private static final String BIRTHDATE_PARAM = "birthdate";
	private static final String PERSONAL_NUMBER_PARAM = "personal_number";
	private static final String HEALTH_INSURANCE = "health_insurance";
	private static final String CONTACT_PARAM = "contact_uuid";
	private static final String HEALTH_INFO_PARAM = "health_info";

	private static final String INSERT = "INSERT INTO course_participant " +
			"(uuid, birthdate, personal_number, health_insurance, contact_uuid, health_info, modif_at, modif_by) " +
			" VALUES (:"+UUID_PARAM+", :"+BIRTHDATE_PARAM+", :"+PERSONAL_NUMBER_PARAM+", :"+HEALTH_INSURANCE+", :"+CONTACT_PARAM+", :"+HEALTH_INFO_PARAM+", "+MODIF_AT_PARAM+", :"+MODIF_BY_PARAM+")";

	@Autowired
	public CourseParticipantDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public List<CourseParticipant> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CourseParticipant getByUuid(UUID uuid, boolean deep) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(CourseParticipant courseParticipant) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(courseParticipant, paramMap);
		paramMap.addValue(BIRTHDATE_PARAM, courseParticipant.getBirthdate());
		paramMap.addValue(PERSONAL_NUMBER_PARAM, courseParticipant.getPersonalNo());
		paramMap.addValue(HEALTH_INSURANCE, courseParticipant.getHealthInsurance());
		paramMap.addValue(CONTACT_PARAM, courseParticipant.getContact() != null ? courseParticipant.getContact().getUuid().toString() : null);
		paramMap.addValue(HEALTH_INFO_PARAM, courseParticipant.getHealthInfo());
		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public void update(CourseParticipant courseParticipant) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(CourseParticipant courseParticipant) {
		// TODO Auto-generated method stub

	}

}
