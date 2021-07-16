package com.jzaoralek.scb.dataservice.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.dao.ContactDao;
import com.jzaoralek.scb.dataservice.dao.CourseApplDynAttrDao;
import com.jzaoralek.scb.dataservice.dao.CourseApplicationDao;
import com.jzaoralek.scb.dataservice.dao.CourseDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.dao.ResultDao;
import com.jzaoralek.scb.dataservice.dao.ScbUserDao;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttr;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;

@Service("courseApplicationService")
public class CourseApplicationServiceImpl extends BaseAbstractService implements CourseApplicationService {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplicationServiceImpl.class);

	@Autowired
	private ConfigurationService configurationService;
	
	@Autowired
	private CourseService courseService;

	@Autowired
	private CourseApplicationDao courseApplicationDao;

	@Autowired
	private ContactDao contactDao;

	@Autowired
	private ScbUserDao scbUserDao;

	@Autowired
	private ResultDao resultDao;

	@Autowired
	private CourseParticipantDao courseParticipantDao;

	@Autowired
	private CourseDao courseDao;
	
	@Autowired
	private CourseApplDynAttrDao courseApplDynAttrDao;
	
	@Override
	@Transactional(rollbackFor=Throwable.class, readOnly=true)
	public List<CourseApplication> getAll(int yearFrom, int yearTo) {
		return courseApplicationDao.getAll(yearFrom, yearTo);
	}

	@Override
	public List<CourseApplication> getUnregisteredToCurrYear(int yearFromPrev, int yearToPrev) {
		return courseApplicationDao.getUnregisteredToCurrYear(yearFromPrev, yearToPrev);
	}
	
	@Override
	@Transactional(rollbackFor=Throwable.class, readOnly=true)
	public CourseApplication getByUuid(UUID uuid) {
		CourseApplication courseApplication = courseApplicationDao.getByUuid(uuid, true);
		if (courseApplication.getCourseParticipant() != null) {
			courseApplication.getCourseParticipant().setResultList(resultDao.getByCourseParticipant(courseApplication.getCourseParticipant().getUuid()));
			courseApplication.getCourseParticipant().setCourseList(courseDao.getByCourseParticipantUuid(courseApplication.getCourseParticipant().getUuid(), courseApplication.getYearFrom(), courseApplication.getYearTo()));
			// dynamic attributes
			courseApplication.getCourseParticipant().setDynAttrList(courseService.getDynAttrByCoursePartic(courseApplication.getCourseParticipant()));
		}
		
		return courseApplication;
	}
	
	@Override
	public boolean existsByPersonalNumber(String personalNumber) {
		return courseParticipantDao.existsByPersonalNumber(personalNumber);
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
		
		// ucastnik update representativeUuid
		courseApplication.getCourseParticipant().setRepresentativeUuid(courseApplication.getCourseParticRepresentative().getUuid());
		courseParticipantDao.update(courseApplication.getCourseParticipant());

		// prihlaska
		boolean insert = courseApplication.getUuid() == null;
		fillIdentEntity(courseApplication);
		if (insert) {
			// insert
			if (courseApplication.getYearFrom() == 0 || courseApplication.getYearTo() == 0) {
				// set yearFromTo only if it isn't already in courseApplication
				courseApplication.fillYearFromTo(configurationService.getCourseApplicationYear());				
			}
			courseApplicationDao.insert(courseApplication);
		} else {
			// update
			courseApplicationDao.update(courseApplication);
		}
		
		return courseApplication;
	}

	@Override
	@Transactional(rollbackFor=Throwable.class)
	public void updatePayed(UUID uuid, boolean payed) throws ScbValidationException {
		CourseApplication courseApplication = courseApplicationDao.getByUuid(uuid, true);
		if (courseApplication == null) {
			LOG.warn("CourseApplication not found, uuid: {}", uuid);
			throw new ScbValidationException(messageSource.getMessage("msg.validation.warn.courseApplication.notExistsInDB", null, Locale.getDefault()));
		}

		courseApplicationDao.updatePayed(courseApplication, payed);
	}
	
	@Override
	public void updateNotifiedPayment(List<UUID> courseParticUuidList, boolean firstSemester) {
		if (CollectionUtils.isEmpty(courseParticUuidList)) {
			return;
		}
		courseApplicationDao.updateNotifiedPayment(courseParticUuidList, Calendar.getInstance().getTime(), firstSemester);
		
	}
	
	@Override
	public void updateCourseParticInterruption(List<UUID> courseParticUuidList, Date interrupetdAt) {
		if (CollectionUtils.isEmpty(courseParticUuidList)) {
			return;
		}
		courseApplicationDao.updateCourseParticInterruption(courseParticUuidList, interrupetdAt);
	}
	
	@Override
	public void updateCourseParticCourseUuid(List<UUID> courseParticUuidList, UUID courseUuid) {
		if (CollectionUtils.isEmpty(courseParticUuidList)) {
			return;
		}
		courseApplicationDao.updateCourseParticCourseUuid(courseParticUuidList, courseUuid);
	}
	
	@Override
	public void insertCourseParticInterruption(UUID courseCourseParticUuid, UUID courseUuid, Date interrupetdAt) {
		Objects.requireNonNull(courseCourseParticUuid);
		Objects.requireNonNull(courseUuid);
		Objects.requireNonNull(interrupetdAt);
		
		courseApplicationDao.insertCourseParticInterruption(UUID.randomUUID(), courseCourseParticUuid, courseUuid, interrupetdAt);
	}

	@Override
	@Transactional(rollbackFor=Throwable.class, readOnly=true)
	public void delete(UUID uuid) throws ScbValidationException {
		CourseApplication courseApplication = courseApplicationDao.getByUuid(uuid, true);
		if (courseApplication == null) {
			LOG.warn("CourseApplication not found, uuid: {}", uuid);
			throw new ScbValidationException(messageSource.getMessage("msg.validation.warn.courseApplication.notExistsInDB", null, Locale.getDefault()));
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting courseApplication: {}", courseApplication);
		}

		courseApplicationDao.delete(courseApplication);
		courseApplDynAttrDao.deleteByCoursePartic(courseApplication.getCourseParticipant().getUuid());
	}

	@Override
	@Transactional(rollbackFor=Throwable.class, readOnly=true)
	public List<CourseApplication> getNotInCourse(UUID courseUuid, int yearFrom, int yearTo) {
		return courseApplicationDao.getNotInCourse(courseUuid, yearFrom, yearTo);
	}

	@Override
	@Transactional(rollbackFor=Throwable.class, readOnly=true)
	public List<CourseApplication> getInCourse(UUID courseUuid, int yearFrom, int yearTo) {
		return courseApplicationDao.getInCourse(courseUuid, yearFrom, yearTo);
	}

	@Override
	public List<CourseApplication> getByCourseParticipantUuid(UUID courseParticipantUuid) {
		return courseApplicationDao.getByCourseParticipantUuid(courseParticipantUuid);
	}
	
	@Override
	public List<CourseApplication> getAssignedToCourse(int yearFrom, int yearTo, CourseType courseType) {
		return courseApplicationDao.getAssignedToCourse(yearFrom, yearTo, courseType);
	}

	private void storeCourseParticRepresentative(ScbUser courseParticRepresentative) {
		if (courseParticRepresentative == null) {
			throw new IllegalArgumentException("courseParticRepresentative is null");
		}
		
		// nacist podle username, pokud existuje, nastavit uuid, provede se jen update
		ScbUser userDb = scbUserDao.getByUsername(courseParticRepresentative.getUsername());
		if (userDb != null) {
			courseParticRepresentative.setUuid(userDb.getUuid());
			courseParticRepresentative.getContact().setUuid(userDb.getContact().getUuid());
		}

		// kontakt zastupce
		storeContact(courseParticRepresentative.getContact());

		boolean insert = courseParticRepresentative.getUuid() == null;
		fillIdentEntity(courseParticRepresentative);
		if (insert) {
			// insert
			//TODO: situace kdy uzivatel uz existuje, nacist podle username, generovani hesla
			courseParticRepresentative.setUsername(courseParticRepresentative.getContact().getEmail1());
			courseParticRepresentative.setPassword(SecurityUtils.generatePassword());
			courseParticRepresentative.setPasswordGenerated(true);
			courseParticRepresentative.setRole(ScbUserRole.USER);

			scbUserDao.insert(courseParticRepresentative);
		} else {
			// update
			scbUserDao.update(courseParticRepresentative);
		}
	}

	@Override
	public void storeCourseParticipant(CourseParticipant courseParticipant) {
		if (courseParticipant == null) {
			throw new IllegalArgumentException("courseParticipant is null");
		}

		// kontakt ucastnika
		storeContact(courseParticipant.getContact());

		// ucastnik
		boolean courseParticInsert = courseParticipant.getUuid() == null;
		fillIdentEntity(courseParticipant);
		if (courseParticInsert) {
			// insert
			courseParticipantDao.insert(courseParticipant);
		} else {
			// update
			courseParticipantDao.update(courseParticipant);
		}
		
		// dynamic attributes
		if (!CollectionUtils.isEmpty(courseParticipant.getDynAttrList()))  {
			courseParticipant.getDynAttrList().forEach(i -> store(i, courseParticipant.getUuid()));
		}
	}

	private void storeContact(Contact contact) {
		if (contact == null) {
			throw new IllegalArgumentException("contact is null");
		}

		boolean insert = (contact.getUuid() == null);
		fillIdentEntity(contact);
		if (insert) {
			// insert
			contactDao.insert(contact);
		} else {
			// update
			contactDao.update(contact);
		}
	}

	@Override
	public void store(CourseApplDynAttr dynAttr, UUID courseParticUuid) {
		if (dynAttr == null) {
			throw new IllegalArgumentException("dynAttr is null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing CourseApplDynAttr: {}", dynAttr);
		}
		
		boolean insertMode = dynAttr.getUuid() == null;
		fillIdentEntity(dynAttr);
		
		// store to DB
		if (insertMode) {
			dynAttr.setCourseParticUuid(courseParticUuid);
			courseApplDynAttrDao.insert(dynAttr);			
		} else {
			courseApplDynAttrDao.update(dynAttr);
		}
	}

	@Override
	public List<CourseApplDynAttr> getCourseApplDynAttrByDate(Date date) {
		return courseApplDynAttrDao.getByDate(date);
	}
}