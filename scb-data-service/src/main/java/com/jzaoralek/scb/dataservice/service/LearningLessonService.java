package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.LearningLesson;

public interface LearningLessonService {

	List<LearningLesson> getByLesson(UUID lessonUuid);
	List<LearningLesson> getByCourse(UUID courseUuid);
	LearningLesson getByUUID(UUID uuid);
	LearningLesson store(LearningLesson lesson);
	void delete(LearningLesson lesson);
}