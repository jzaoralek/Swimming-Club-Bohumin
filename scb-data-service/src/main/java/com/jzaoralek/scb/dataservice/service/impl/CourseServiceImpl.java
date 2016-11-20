package com.jzaoralek.scb.dataservice.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.dao.CourseDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.CourseService;

@Service("courseService")
public class CourseServiceImpl extends BaseAbstractService implements CourseService {

	private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private CourseParticipantDao courseParticipantDao;


	@Override
	public void delete(UUID uuid) throws ScbValidationException {
		Course course = courseDao.getByUuid(uuid);
		if (course == null) {
			LOG.warn("CourseApplication not found, uuid: " + uuid);
			throw new ScbValidationException(messageSource.getMessage("msg.validation.warn.courseApplication.notExistsInDB", null, Locale.getDefault()));
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting courseApplication: " + course);
		}

		courseDao.delete(course);
	}

	@Override
	public List<Course> getAll(int yearFrom, int yearTo) {
		return courseDao.getAll(yearFrom, yearTo);
	}

	@Override
	public List<Course> getAllExceptCourse(UUID courseUuid) {
		return courseDao.getAllExceptCourse(courseUuid);
	}

	@Override
	public Course getByUuid(UUID uuid) {
		return courseDao.getByUuid(uuid);
	}

	@Override
	public List<CourseParticipant> getByCourseParticListByCourseUuid(UUID courseUuid) {
		return courseParticipantDao.getByCourseUuid(courseUuid);
	}

	@Override
	public Course store(Course course) throws ScbValidationException {
		if (course == null) {
			throw new IllegalArgumentException("course is null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing course: " + course);
		}

		boolean insert = course.getUuid() == null;
		fillIdentEntity(course);
		if (insert) {
			courseDao.insert(course);
		} else {
			courseDao.update(course);
		}

		return course;
	}

	@Override
	public void storeCourseParticipants(List<CourseParticipant> newCourseParticipantList, UUID courseUuid) throws ScbValidationException {
		if (courseUuid == null) {
			throw new IllegalArgumentException("courseUuid is null");
		}
		if (CollectionUtils.isEmpty(newCourseParticipantList)) {
			LOG.warn("CourseParticipantList is empty.");
			return;
		}

		// nacist kurz vcetne ucastniku - nove DAO
		Course courseDB = courseDao.getByUuid(courseUuid);
		List<CourseParticipant> courseParticipantListFinal = new ArrayList<CourseParticipant>();
		if (!CollectionUtils.isEmpty(courseDB.getParticipantList())) {
			// courseParticipantList porovnat z puvodni mnozinou a vzbrat pouze pridane
			for (CourseParticipant item : newCourseParticipantList) {
				if (!containInList(item, courseDB.getParticipantList())) {
					courseParticipantListFinal.add(item);
				}
			}
		} else {
			courseParticipantListFinal = newCourseParticipantList;
		}

		// pridat puvodni
		if (!CollectionUtils.isEmpty(courseDB.getParticipantList())) {
			courseParticipantListFinal.addAll(courseDB.getParticipantList());
		}
		// nastavit do course
		courseDB.setParticipantList(courseParticipantListFinal);
		// ulozit
		courseDao.update(courseDB);
	}

	@Override
	public void deleteParticipantFromCourse(UUID participantUuid, UUID courseUuid) {
		courseParticipantDao.deleteParticipantFromCourse(participantUuid, courseUuid);
	}

	private boolean containInList(CourseParticipant participant, List<CourseParticipant> courseParticipantList) {
		if (CollectionUtils.isEmpty(courseParticipantList)) {
			return false;
		}

		for (CourseParticipant item : courseParticipantList) {
			if (item.getUuid().toString().equals(participant.getUuid().toString())) {
				return true;
			}
		}

		return false;
	}
}
