package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.LearningLesson;

public interface LearningLessonDao {

	List<LearningLesson> getByLesson(UUID lessonUuid);
	List<LearningLesson> getByCourse(UUID courseUuid);
	LearningLesson getByUUID(UUID uuid);
	void insert(LearningLesson lesson);
	void update(LearningLesson lesson);
	void delete(LearningLesson lesson);
}
