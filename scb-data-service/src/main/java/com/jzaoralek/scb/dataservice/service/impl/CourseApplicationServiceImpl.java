package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jzaoralek.scb.dataservice.dao.ContactDao;
import com.jzaoralek.scb.dataservice.dao.CourseApplicationDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.dao.ScbUserDao;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;

@Service("courseApplicationService")
public class CourseApplicationServiceImpl extends BaseAbstractService implements CourseApplicationService {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplicationServiceImpl.class);

	@Autowired
	private CourseApplicationDao courseApplicationDao;

	@Autowired
	private ContactDao contactDao;

	@Autowired
	private ScbUserDao scbUserDao;

	@Autowired
	private CourseParticipantDao courseParticipantDao;

	@Override
	@Transactional(rollbackFor=Throwable.class, readOnly=true)
	public List<CourseApplication> getAll() {
		return courseApplicationDao.getAll();
	}

	@Override
	@Transactional(rollbackFor=Throwable.class, readOnly=true)
	public CourseApplication getByUuid(UUID uuid) {
		return courseApplicationDao.getByUuid(uuid, true);
	}

	@Override
	@Transactional(rollbackFor=Throwable.class)
	public CourseApplication store(CourseApplication courseApplication) throws ScbValidationException {
		if (courseApplication == null) {
			throw new IllegalArgumentException("courseApplication is null");
		}

		// ucastnik
		storeCourseParticipant(courseApplication.getCourseParticipant());

		// zastupce
		storeCourseParticRepresentative(courseApplication.getCourseParticRepresentative());

		// prihlaska
		boolean insert = courseApplication.getUuid() == null;
		fillIdentEntity(courseApplication, null);
		if (insert) {
			// insert
			courseApplication.setYearTo(courseApplication.getYearFrom() + 1);
			courseApplicationDao.insert(courseApplication);
		} else {
			// update
			courseApplicationDao.update(courseApplication);
		}

		return courseApplication;
	}

	@Override
	@Transactional(rollbackFor=Throwable.class, readOnly=true)
	public void delete(UUID uuid) throws ScbValidationException {
		CourseApplication courseApplication = courseApplicationDao.getByUuid(uuid, true);
		if (courseApplication == null) {
			LOG.warn("CourseApplication not found, uuid: " + uuid);
			throw new ScbValidationException(messageSource.getMessage("msg.validation.warn.courseApplication.notExistsInDB", null, Locale.getDefault()));
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting courseApplication: " + courseApplication);
		}

		courseApplicationDao.delete(courseApplication);
	}

	private void storeCourseParticRepresentative(ScbUser courseParticRepresentative) {
		if (courseParticRepresentative == null) {
			throw new IllegalArgumentException("courseParticRepresentative is null");
		}

		// kontakt zastupce
		storeContact(courseParticRepresentative.getContact());

		courseParticRepresentative.getUuid();
		boolean insert = courseParticRepresentative.getUuid() == null;
		fillIdentEntity(courseParticRepresentative, null);
		if (insert) {
			// insert

			//TODO: situace kdy uzivatel uz existuje, nacist podle username, generovani hesla
			courseParticRepresentative.setUsername(courseParticRepresentative.getContact().getEmail1());
			courseParticRepresentative.setPassword("password");
			courseParticRepresentative.setPasswordGenerated(true);
			courseParticRepresentative.setRole(ScbUserRole.USER);

			scbUserDao.insert(courseParticRepresentative);
		} else {
			// update
			scbUserDao.update(courseParticRepresentative);
		}
	}

	private void storeCourseParticipant(CourseParticipant courseParticipant) {
		if (courseParticipant == null) {
			throw new IllegalArgumentException("courseParticipant is null");
		}

		// kontakt ucastnika
		storeContact(courseParticipant.getContact());

		// ucastnik
		boolean courseParticInsert = courseParticipant.getUuid() == null;
		fillIdentEntity(courseParticipant, null);
		if (courseParticInsert) {
			// insert
			courseParticipantDao.insert(courseParticipant);
		} else {
			// update
			courseParticipantDao.update(courseParticipant);
		}
	}

	private void storeContact(Contact contact) {
		if (contact == null) {
			throw new IllegalArgumentException("contact is null");
		}

		boolean insert = (contact.getUuid() == null);
		fillIdentEntity(contact, null);
		if (insert) {
			// insert
			contactDao.insert(contact);
		} else {
			// update
			contactDao.update(contact);
		}
	}
}