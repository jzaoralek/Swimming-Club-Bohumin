package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;

public interface LessonService {

	List<Lesson> getAll();
	List<Lesson> getByCourse(UUID courseUuid);
	Lesson getByUuid(UUID uuid);
	Lesson store(Lesson lesson);
	void delete(UUID lessonUuid) throws ScbValidationException;
}
