package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.dao.CourseApplicationDao;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;

@Service
public class CourseApplicationServiceImpl implements CourseApplicationService {

	@Autowired
	private CourseApplicationDao courseApplicationDao;

	@Override
	public List<CourseApplication> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CourseApplication getByUuid(UUID uuid) {
		CourseApplication ret = new CourseApplication();

		Contact contact = new Contact();
		contact.setCity("city");
		contact.setEmail1("email1");
		contact.setEmail2("email2");
		contact.setFirstname("firstname");
		contact.setHouseNumber((short)10);
		contact.setLandRegistryNumber(1599);
		contact.setPhone1("phone1");
		contact.setPhone2("phone2");
		contact.setStreet("street");
		contact.setSurname("surname");
		contact.setUuid(uuid);
		contact.setZipCode("zipCode");

		CourseParticipant courseParticipant = new CourseParticipant();
		courseParticipant.setContact(contact);
		courseParticipant.setUuid(UUID.randomUUID());

		ret.setCourseParticipant(courseParticipant);

		ScbUser user = new ScbUser();
		user.setContact(contact);
		user.setFirstname("firstname");
		user.setLastname("lastname");
		user.setPassword("password");
		user.setPasswordGenerated(false);
		user.setRole(ScbUserRole.USER);
		ret.setCourseParticRepresentative(user);

		ret.setUuid(uuid);
		ret.setYearFrom(2016);
		ret.setYearTo(2017);

		return ret;
	}

	@Override
	public CourseApplication store(CourseApplication courseApplication) {
		if (courseApplication == null) {
			throw new IllegalArgumentException("courseApplication is null");
		}

		if (courseApplication.getUuid() == null) {
			// insert

			// kontakt ucastnika
			// ucastnik
			// kontakt zastupce
			// zastupce
			// prihlaska
			courseApplication.setUuid(UUID.randomUUID());
			courseApplication.setYearTo(courseApplication.getYearFrom() + 1);
			courseApplicationDao.insert(courseApplication);
		} else {
			// update
		}

		return courseApplication;
	}

	@Override
	public void delete(UUID uuid) {
		// TODO Auto-generated method stub

	}
}