package com.jzaoralek.scb.dataservice.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.dao.LearningLessonDao;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.LearningLesson;
import com.jzaoralek.scb.dataservice.domain.LearningLessonStats;
import com.jzaoralek.scb.dataservice.domain.LearningLessonStatsWrapper;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.LearningLessonService;

@Service("learningLessonService")
public class LearningLessonServiceImpl extends BaseAbstractService implements LearningLessonService {

	private static final Logger LOG = LoggerFactory.getLogger(LearningLessonServiceImpl.class);

	@Autowired
	private LearningLessonDao learningLessonDao;

	@Autowired
	private CourseParticipantDao courseParticipantDao;

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

	@Override
	public LearningLessonStatsWrapper buildCourseStatistics(Course course) {
		List<LearningLessonStats> learnLessonsStatsList = new ArrayList<LearningLessonStats>();
		// oducene vyucovaci hodiny s ucastniky na hodine
		List<LearningLesson> learningLessonList = learningLessonDao.getByCourseWithFilledParticipantList(course.getUuid());
		// vsichni ucastnici kurzu
		List<CourseParticipant> courseParticipantList = courseParticipantDao.getByCourseUuid(course.getUuid());
		// prochazet oducene vyucovaci hodiny a v nich ucastniky porovnat se vsemi ucastniky kurzu
		List<CourseParticipant> courseParticAttendaceList = null;
		CourseParticipant courseParticAttendace = null;
		for (LearningLesson learninLesson : learningLessonList) {
			// sestavit seznam ucastniku s ucasti na hodine
			courseParticAttendaceList = new ArrayList<CourseParticipant>();
			for (CourseParticipant courseParticipant : courseParticipantList) {
				// pro kazdeho ucastnika kurzu zjistit zda-li ma ucast na hodine
				courseParticAttendace = new CourseParticipant(courseParticipant);
				courseParticAttendace.setLessonAttendance(isCourseParticipantInList(courseParticipant, learninLesson.getParticipantList()));
				courseParticAttendaceList.add(courseParticAttendace);
			}
			learnLessonsStatsList.add(new LearningLessonStats(learninLesson, courseParticAttendaceList));
		}
		
		return new LearningLessonStatsWrapper(courseParticipantList, learnLessonsStatsList);
	}
	
	private boolean isCourseParticipantInList(CourseParticipant participant, List<CourseParticipant> courseParticList) {
		if (CollectionUtils.isEmpty(courseParticList) || participant == null) {
			return false;
		}
		for (CourseParticipant courseParticItem : courseParticList) {
			if (participant.getUuid().toString().equals(courseParticItem.getUuid().toString())) {
				return true;
			}
		}
		
		return false;
	}
}
