package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.domain.Lesson.DayOfWeek;

public interface LessonDao {

	List<Lesson> getAll();
	List<Lesson> getByCourse(UUID courseUuid);
	List<Lesson> getByCourseAndDayOfWeek(UUID courseUuid, DayOfWeek dayOfWeek);
	Lesson getByUuid(UUID uuid);
	void insert(Lesson lesson);
	void update(Lesson lesson);
	void delete(Lesson lesson);
}
