package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.LearningLesson;
import com.jzaoralek.scb.dataservice.domain.LearningLessonStats;
import com.jzaoralek.scb.dataservice.domain.LearningLessonStatsWrapper;

public interface LearningLessonService {

	List<LearningLesson> getByLesson(UUID lessonUuid);
	List<LearningLesson> getByCourse(UUID courseUuid);
	LearningLesson getByUUID(UUID uuid);
	LearningLesson store(LearningLesson lesson);
	void delete(LearningLesson lesson);
	/**
	 * Cilem sestavit list lekci, u kazde list participantu s boolean attendance.
	 * @param courseUuid
	 * @param participantUuid
	 * @return
	 */
	LearningLessonStatsWrapper buildCourseStatistics(UUID courseUuid, UUID participantUuid);
}
