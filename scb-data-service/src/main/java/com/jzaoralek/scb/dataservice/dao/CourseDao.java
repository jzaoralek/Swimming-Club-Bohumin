package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Course;

public interface CourseDao {

	List<Course> getAll(int yearFrom, int yearTo);
	List<Course> getAllExceptCourse(UUID courseUuid);
	Course getByUuid(UUID uuid);
	Course getPlainByUuid(UUID uuid);
	List<Course> getByCourseParticipantUuid(UUID courseParticipantUuid, int yearFrom, int yearTo);
	void insert(Course course);
	void update(Course course);
	void delete(Course course);
	boolean existsByCourseLocation(UUID courseLocationUuid);
}
