package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.LearningLesson;

public interface LearningLessonService {

	List<LearningLesson> getByLesson(UUID lessonUuid);
	LearningLesson store(LearningLesson lesson);
	void delete(LearningLesson lesson);
}
