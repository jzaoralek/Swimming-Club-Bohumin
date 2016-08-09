package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.jzaoralek.scb.dataservice.dao.BaseJdbcDao;
import com.jzaoralek.scb.dataservice.dao.CourseApplicationDao;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.ScbUser;

@Repository
public class CourseApplicationDaoImpl extends BaseJdbcDao implements CourseApplicationDao {

	private static final String YEAR_FROM_PARAM = "YEAR_FROM";
	private static final String YEAR_TO_PARAM = "YEAR_TO";
	private static final String COURSE_PARTICIPANT_UUID_PARAM = "COURSE_PARTICIPANT_UUID";
	private static final String USER_UUID_PARAM = "USER_UUID";


	private static final String INSERT = "INSERT INTO course_application (uuid, year_from, year_to, course_participant_uuid, user_uuid, modif_at, modif_by) values (:"+UUID_PARAM+",:"+YEAR_FROM_PARAM+",:"+YEAR_TO_PARAM+",:"+COURSE_PARTICIPANT_UUID_PARAM+",:"+USER_UUID_PARAM+",:"+MODIF_AT_PARAM+",:"+MODIF_BY_PARAM+")";
	private static final String SELECT_ALL = "select " +
					"con_part.firstname " +
					", con_part.surname " +
					", cp.birthdate " +
					", cp.personal_number " +
					", con_repr.firstname \"representative_firstname\" " +
					", con_repr.surname \"representative_surname\" " +
					", con_repr.phone1 " +
					", con_repr.email1 " +
					", ca.uuid " +
					", ca.modif_at " +
					", ca.modif_by " +
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
					"order by ca.modif_at desc ";

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
		// TODO Auto-generated method stub
	}

	@Override
	public void delete(CourseApplication courseApplication) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<CourseApplication> getAll() {
		return namedJdbcTemplate.query(SELECT_ALL, new CourseApplicationRowMapper());
	}

	@Override
	public CourseApplication getByUuid(UUID uuid, boolean deep) {
		// TODO Auto-generated method stub
		return null;
	}

	public static final class CourseApplicationRowMapper implements RowMapper<CourseApplication> {
		@Override
		public CourseApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseApplication ret = new CourseApplication();
			fetchIdentEntity(rs, ret);

			CourseParticipant courseParticipant = new CourseParticipant();
			courseParticipant.setBirthdate(rs.getDate("birthdate"));
			courseParticipant.setPersonalNo(rs.getString("personal_number"));

			Contact courseParticipantContact = new Contact();
			courseParticipantContact.setFirstname(rs.getString("firstname"));
			courseParticipantContact.setSurname(rs.getString("surname"));
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

			return ret;
		}
	}
}