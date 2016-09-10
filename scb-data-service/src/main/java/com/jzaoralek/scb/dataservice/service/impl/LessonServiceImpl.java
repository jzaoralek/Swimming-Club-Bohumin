package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.dao.LessonDao;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.LessonService;
import com.jzaoralek.scb.dataservice.utils.DateUtils;

@Service("lessonService")
public class LessonServiceImpl extends BaseAbstractService implements LessonService {

	private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

	@Autowired
	private LessonDao lessonDao;

	@Override
	public List<Lesson> getAll() {
		return lessonDao.getAll();
	}

	@Override
	public List<Lesson> getByCourse(UUID courseUuid) {
		return lessonDao.getByCourse(courseUuid);
	}

	@Override
	public Lesson getByUuid(UUID uuid) {
		return lessonDao.getByUuid(uuid);
	}

	@Override
	public Lesson store(Lesson lesson) throws ScbValidationException {
		if (lesson == null) {
			throw new IllegalArgumentException("lesson is null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing lesson: " + lesson);
		}

		// pokud prunik s existujici lekci ve stejny den v tydnu, ohlasit chybu
		storeValidation(lesson);

		boolean insert = lesson.getUuid() == null;
		// ((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()
		fillIdentEntity(lesson, null);
		if (insert) {
			lessonDao.insert(lesson);
		} else {
			lessonDao.update(lesson);
		}

		return lesson;

	}

	@Override
	public void delete(UUID lessonUuid) throws ScbValidationException {
		Lesson lesson = lessonDao.getByUuid(lessonUuid);
		if (lesson == null) {
			LOG.warn("Lesson not found, uuid: " + lessonUuid);
			throw new ScbValidationException(messageSource.getMessage("msg.validation.warn.courseApplication.notExistsInDB", null, Locale.getDefault()));
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting lesson: " + lesson);
		}
		lessonDao.delete(lesson);
	}

	private void storeValidation(Lesson lesson) throws ScbValidationException {
		// validace zda-li lekce ve stejny den a stejný čas
		List<Lesson> sameDayOfWeekCourseLessonList = lessonDao.getByCourseAndDayOfWeek(lesson.getCourseUuid(), lesson.getDayOfWeek());
		for (Lesson item : sameDayOfWeekCourseLessonList) {
			if (lesson.getUuid() != null && item.getUuid().toString().equals(lesson.getUuid().toString())) {
				continue;
			}

			if (DateUtils.isIntersection(lesson.getTimeFrom(), lesson.getTimeTo(), item.getTimeFrom(), item.getTimeTo())) {
				throw new ScbValidationException(messageSource.getMessage("msg.validation.warn.lesson.sameTimeIntervalAsOtherLesson", null, Locale.getDefault()));
			}
		}
	}
}
