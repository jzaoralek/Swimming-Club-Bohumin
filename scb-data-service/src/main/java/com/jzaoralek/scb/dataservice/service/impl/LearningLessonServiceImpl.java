package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.dao.LearningLessonDao;
import com.jzaoralek.scb.dataservice.domain.LearningLesson;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.LearningLessonService;

@Service("learningLessonService")
public class LearningLessonServiceImpl extends BaseAbstractService implements LearningLessonService {

	private static final Logger LOG = LoggerFactory.getLogger(LearningLessonServiceImpl.class);

	@Autowired
	private LearningLessonDao learningLessonDao;

	@Override
	public List<LearningLesson> getByLesson(UUID lessonUuid) {
		return learningLessonDao.getByLesson(lessonUuid);
	}

	@Override
	public List<LearningLesson> getByCourse(UUID courseUuid) {
		return learningLessonDao.getByCourse(courseUuid);
	}

	@Override
	public LearningLesson getByUUID(UUID uuid) {
		return learningLessonDao.getByUUID(uuid);
	}

	@Override
	public LearningLesson store(LearningLesson lesson) {
		if (lesson == null) {
			throw new IllegalArgumentException("learningLesson is null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing lesson: " + lesson);
		}


		boolean insert = lesson.getUuid() == null;
		fillIdentEntity(lesson);
		if (insert) {
			learningLessonDao.insert(lesson);
		} else {
			learningLessonDao.update(lesson);
		}

		return lesson;
	}

	@Override
	public void delete(LearningLesson lesson) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting lesson: " + lesson);
		}
		learningLessonDao.delete(lesson);
	}
}
