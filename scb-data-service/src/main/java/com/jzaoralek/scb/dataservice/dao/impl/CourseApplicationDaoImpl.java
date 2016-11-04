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
import com.jzaoralek.scb.dataservice.dao.CourseApplicationDao;
import com.jzaoralek.scb.dataservice.dao.CourseDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.dao.ScbUserDao;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.ScbUser;

@Repository
public class CourseApplicationDaoImpl extends BaseJdbcDao implements CourseApplicationDao {

	@Autowired
	private CourseDao courseDao;

	private static final String COURSE_PARTICIPANT_UUID_PARAM = "COURSE_PARTICIPANT_UUID";
	private static final String USER_UUID_PARAM = "USER_UUID";
	private static final String PAYED_PARAM = "PAYED";

	private static final String INSERT = "INSERT INTO course_application (uuid, year_from, year_to, course_participant_uuid, user_uuid, modif_at, modif_by) values (:"+UUID_PARAM+",:"+YEAR_FROM_PARAM+",:"+YEAR_TO_PARAM+",:"+COURSE_PARTICIPANT_UUID_PARAM+",:"+USER_UUID_PARAM+",:"+MODIF_AT_PARAM+",:"+MODIF_BY_PARAM+")";
	private static final String SELECT_ALL = "select " +
					" con_part.firstname " +
					", con_part.surname " +
					", cp.uuid \"participant_uuid\" " +
					", cp.birthdate " +
					", cp.personal_number " +
					", con_repr.firstname \"representative_firstname\" " +
					", con_repr.surname \"representative_surname\" " +
					", con_repr.phone1 " +
					", con_repr.email1 " +
					", ca.uuid " +
					", ca.modif_at " +
					", ca.modif_by " +
					", ca.payed " +
					", con_part.city " +
					", con_part.street " +
					", con_part.land_registry_number " +
					", con_part.house_number " +
					", con_part.zip_code " +
					"from  " +
					"course_application ca " +
					", course_participant cp " +
					", contact con_part " +
					", contact con_repr " +
					", user usr " +
					"where " +
					"ca.course_participant_uuid = cp.uuid " +
					"and cp.contact_uuid = con_part.uuid " +
					"and ca.user_uuid = usr.uuid " +
					"and usr.contact_uuid = con_repr.uuid " +
					"AND ca.year_from = :"+YEAR_FROM_PARAM+" " +
					"AND ca.year_to = :"+YEAR_TO_PARAM+ " " +
					"order by ca.modif_at desc ";

	private static final String SELECT_NOT_IN_COURSE = "select " +
					" con_part.firstname " +
					", con_part.surname " +
					", cp.uuid \"participant_uuid\" " +
					", cp.birthdate " +
					", cp.personal_number " +
					", con_repr.firstname \"representative_firstname\" " +
					", con_repr.surname \"representative_surname\" " +
					", con_repr.phone1 " +
					", con_repr.email1 " +
					", ca.uuid " +
					", ca.modif_at " +
					", ca.modif_by " +
					", ca.payed " +
					", con_part.city " +
					", con_part.street " +
					", con_part.land_registry_number " +
					", con_part.house_number " +
					", con_part.zip_code " +
					"from  " +
					"course_application ca " +
					", course_participant cp " +
					", contact con_part " +
					", contact con_repr " +
					", user usr " +
					"where " +
					"ca.course_participant_uuid = cp.uuid " +
					"and cp.contact_uuid = con_part.uuid " +
					"and ca.user_uuid = usr.uuid " +
					"and usr.contact_uuid = con_repr.uuid " +
					"AND ca.year_from = :"+YEAR_FROM_PARAM+" " +
					"AND ca.year_to = :"+YEAR_TO_PARAM+ " " +
					"AND cp.uuid NOT IN (SELECT ccp.course_participant_uuid FROM course_course_participant ccp WHERE ccp.course_uuid = :"+COURSE_UUID_PARAM+") " +
					"order by ca.modif_at desc ";

	private static final String SELECT_IN_COURSE = "select " +
			" con_part.firstname " +
			", con_part.surname " +
			", cp.uuid \"participant_uuid\" " +
			", cp.birthdate " +
			", cp.personal_number " +
			", con_repr.firstname \"representative_firstname\" " +
			", con_repr.surname \"representative_surname\" " +
			", con_repr.phone1 " +
			", con_repr.email1 " +
			", ca.uuid " +
			", ca.modif_at " +
			", ca.modif_by " +
			", ca.payed " +
			", con_part.city " +
			", con_part.street " +
			", con_part.land_registry_number " +
			", con_part.house_number " +
			", con_part.zip_code " +
			"from  " +
			"course_application ca " +
			", course_participant cp " +
			", contact con_part " +
			", contact con_repr " +
			", user usr " +
			"where " +
			"ca.course_participant_uuid = cp.uuid " +
			"and cp.contact_uuid = con_part.uuid " +
			"and ca.user_uuid = usr.uuid " +
			"and usr.contact_uuid = con_repr.uuid " +
			"AND ca.year_from = :"+YEAR_FROM_PARAM+" " +
			"AND ca.year_to = :"+YEAR_TO_PARAM+ " " +
			"AND cp.uuid IN (SELECT ccp.course_participant_uuid FROM course_course_participant ccp WHERE ccp.course_uuid = :"+COURSE_UUID_PARAM+") " +
			"order by ca.modif_at desc ";

	private static final String SELECT_ASSIGNED_TO_COURSE = "select " +
			" con_part.firstname " +
			", con_part.surname " +
			", cp.uuid \"participant_uuid\" " +
			", cp.birthdate " +
			", cp.personal_number " +
			", con_repr.firstname \"representative_firstname\" " +
			", con_repr.surname \"representative_surname\" " +
			", con_repr.phone1 " +
			", con_repr.email1 " +
			", ca.uuid " +
			", ca.modif_at " +
			", ca.modif_by " +
			", ca.payed " +
			", con_part.city " +
			", con_part.street " +
			", con_part.land_registry_number " +
			", con_part.house_number " +
			", con_part.zip_code " +
			"from  " +
			"course_application ca " +
			", course_participant cp " +
			", contact con_part " +
			", contact con_repr " +
			", user usr " +
			", course_course_participant ccp " +
			"where " +
			"ca.course_participant_uuid = cp.uuid " +
			"and cp.contact_uuid = con_part.uuid " +
			"and ca.user_uuid = usr.uuid " +
			"and usr.contact_uuid = con_repr.uuid " +
			"AND ca.year_from = :"+YEAR_FROM_PARAM+" " +
			"AND ca.year_to = :"+YEAR_TO_PARAM+ " " +
			"AND cp.uuid = ccp.course_participant_uuid " +
			"order by con_part.surname ";

	private static final String SELECT_BY_UUID = "select uuid, year_from, year_to, course_participant_uuid, user_uuid, modif_at, modif_by, payed from course_application where uuid=:" + UUID_PARAM;
	private static final String DELETE = "DELETE FROM course_application where uuid = :" + UUID_PARAM;
	private static final String UPDATE = "UPDATE course_application SET year_from=:"+YEAR_FROM_PARAM+", year_to=:"+YEAR_TO_PARAM+", course_participant_uuid=:"+COURSE_PARTICIPANT_UUID_PARAM+", user_uuid=:"+USER_UUID_PARAM+", modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+", payed = :"+PAYED_PARAM+" WHERE uuid=:"+UUID_PARAM;
	private static final String UPDATE_PAYED = "UPDATE course_application SET modif_at = :"+MODIF_AT_PARAM+", modif_by = :"+MODIF_BY_PARAM+", payed = :"+PAYED_PARAM+" WHERE uuid=:"+UUID_PARAM;

	@Autowired
	private CourseParticipantDao courseParticipantDao;

	@Autowired
	private ScbUserDao scbUserDao;

	@Autowired
	public CourseApplicationDaoImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public void insert(CourseApplication courseApplication) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(courseApplication, paramMap);
		paramMap.addValue(YEAR_FROM_PARAM, courseApplication.getYearFrom());
		paramMap.addValue(YEAR_TO_PARAM, courseApplication.getYearTo());
		paramMap.addValue(COURSE_PARTICIPANT_UUID_PARAM, courseApplication.getCourseParticipant().getUuid() != null ? courseApplication.getCourseParticipant().getUuid().toString() : "");
		paramMap.addValue(USER_UUID_PARAM, courseApplication.getCourseParticRepresentative().getUuid() != null ? courseApplication.getCourseParticRepresentative().getUuid().toString() : "");

		namedJdbcTemplate.update(INSERT, paramMap);
	}

	@Override
	public void update(CourseApplication courseApplication) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(courseApplication, paramMap);
		paramMap.addValue(YEAR_FROM_PARAM, courseApplication.getYearFrom());
		paramMap.addValue(YEAR_TO_PARAM, courseApplication.getYearTo());
		paramMap.addValue(COURSE_PARTICIPANT_UUID_PARAM, courseApplication.getCourseParticipant().getUuid() != null ? courseApplication.getCourseParticipant().getUuid().toString() : "");
		paramMap.addValue(USER_UUID_PARAM, courseApplication.getCourseParticRepresentative().getUuid() != null ? courseApplication.getCourseParticRepresentative().getUuid().toString() : "");
		paramMap.addValue(PAYED_PARAM, courseApplication.isPayed() ? "1" : "0");

		namedJdbcTemplate.update(UPDATE, paramMap);
	}

	@Override
	public void updatePayed(CourseApplication courseApplication, boolean payed) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		fillIdentEntity(courseApplication, paramMap);

		paramMap.addValue(COURSE_PARTICIPANT_UUID_PARAM, courseApplication.getCourseParticipant().getUuid() != null ? courseApplication.getCourseParticipant().getUuid().toString() : "");
		paramMap.addValue(PAYED_PARAM, payed ? "1" : "0");

		namedJdbcTemplate.update(UPDATE_PAYED, paramMap);
	}

	@Override
	public void delete(CourseApplication courseApplication) {
		courseParticipantDao.delete(courseApplication.getCourseParticipant());
		scbUserDao.delete(courseApplication.getCourseParticRepresentative());
		namedJdbcTemplate.update(DELETE, new MapSqlParameterSource().addValue(UUID_PARAM, courseApplication.getUuid().toString()));
	}

	@Override
	public List<CourseApplication> getAll(int yearFrom, int yearTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(YEAR_FROM_PARAM, yearFrom).addValue(YEAR_TO_PARAM, yearTo);
		return namedJdbcTemplate.query(SELECT_ALL, paramMap, new CourseApplicationRowMapper(courseDao));
	}

	@Override
	public List<CourseApplication> getNotInCourse(UUID courseUuid, int yearFrom, int yearTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString()).addValue(YEAR_FROM_PARAM, yearFrom).addValue(YEAR_TO_PARAM, yearTo);
		return namedJdbcTemplate.query(SELECT_NOT_IN_COURSE, paramMap, new CourseApplicationRowMapper(courseDao));
	}

	@Override
	public List<CourseApplication> getInCourse(UUID courseUuid, int yearFrom, int yearTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(COURSE_UUID_PARAM, courseUuid.toString()).addValue(YEAR_FROM_PARAM, yearFrom).addValue(YEAR_TO_PARAM, yearTo);
		return namedJdbcTemplate.query(SELECT_IN_COURSE, paramMap, new CourseApplicationRowMapper(courseDao));
	}

	@Override
	public List<CourseApplication> getAssignedToCourse(int yearFrom, int yearTo) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(YEAR_FROM_PARAM, yearFrom).addValue(YEAR_TO_PARAM, yearTo);
		return namedJdbcTemplate.query(SELECT_ASSIGNED_TO_COURSE, paramMap, new CourseApplicationRowMapper(courseDao));
	}

	@Override
	public CourseApplication getByUuid(UUID uuid, boolean deep) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue(UUID_PARAM, uuid.toString());
		try {
			return namedJdbcTemplate.queryForObject(SELECT_BY_UUID, paramMap, new CourseApplicationRowMapperDetail(courseParticipantDao, scbUserDao));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public static final class CourseApplicationRowMapper implements RowMapper<CourseApplication> {
		private CourseDao courseDao;

		public CourseApplicationRowMapper(CourseDao courseDao) {
			this.courseDao = courseDao;
		}

		@Override
		public CourseApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseApplication ret = new CourseApplication();
			fetchIdentEntity(rs, ret);

			CourseParticipant courseParticipant = new CourseParticipant();
			courseParticipant.setUuid(UUID.fromString(rs.getString("participant_uuid")));
			courseParticipant.setBirthdate(rs.getDate("birthdate"));
			courseParticipant.setPersonalNo(rs.getString("personal_number"));
			courseParticipant.setCourseList(courseDao.getByCourseParticipantUuid(courseParticipant.getUuid()));

			Contact courseParticipantContact = new Contact();
			courseParticipantContact.setFirstname(rs.getString("firstname"));
			courseParticipantContact.setSurname(rs.getString("surname"));
			courseParticipantContact.setCity(rs.getString("city"));
			courseParticipantContact.setStreet(rs.getString("street"));
			courseParticipantContact.setLandRegistryNumber(rs.getLong("land_registry_number"));
			courseParticipantContact.setHouseNumber(rs.getShort("house_number"));
			courseParticipantContact.setZipCode(rs.getString("zip_code"));
			courseParticipant.setContact(courseParticipantContact);

			ret.setCourseParticipant(courseParticipant);

			ScbUser courseParticRepresentative = new ScbUser();
			Contact courseParticRepresentativeContact = new Contact();
			courseParticRepresentativeContact.setFirstname(rs.getString("representative_firstname"));
			courseParticRepresentativeContact.setSurname(rs.getString("representative_surname"));
			courseParticRepresentativeContact.setPhone1(rs.getString("phone1"));
			courseParticRepresentativeContact.setEmail1(rs.getString("email1"));
			courseParticRepresentative.setContact(courseParticRepresentativeContact);

			ret.setCourseParticRepresentative(courseParticRepresentative);

			ret.setPayed(rs.getInt("payed") == 1);

			return ret;
		}
	}

	public static final class CourseApplicationRowMapperDetail implements RowMapper<CourseApplication> {
		private CourseParticipantDao courseParticipantDao;
		private ScbUserDao scbUserDao;

		public CourseApplicationRowMapperDetail(CourseParticipantDao courseParticipantDao, ScbUserDao scbUserDao) {
			this.courseParticipantDao = courseParticipantDao;
			this.scbUserDao = scbUserDao;
		}

		@Override
		public CourseApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseApplication ret = new CourseApplication();
			fetchIdentEntity(rs, ret);
			ret.setYearFrom(rs.getInt("year_from"));
			ret.setYearTo(rs.getInt("year_to"));

			UUID courseParticipantUuid = rs.getString("course_participant_uuid") != null ? UUID.fromString(rs.getString("course_participant_uuid")) : null;
			if (courseParticipantUuid != null) {
				ret.setCourseParticipant(courseParticipantDao.getByUuid(courseParticipantUuid, true));
			}

			new ScbUser();
			UUID courseParticipantrepresentativeUuid = rs.getString("user_uuid") != null ? UUID.fromString(rs.getString("user_uuid")) : null;
			if (courseParticipantrepresentativeUuid != null) {
				ret.setCourseParticRepresentative(scbUserDao.getByUuid(courseParticipantrepresentativeUuid));
			}
			ret.setPayed(rs.getInt("payed") == 1);

			return ret;
		}
	}
}