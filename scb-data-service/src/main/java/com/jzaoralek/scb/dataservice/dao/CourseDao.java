package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Course;

public interface CourseDao {

	List<Course> getAll();
	List<Course> getAllExceptCourse(UUID courseUuid);
	Course getByUuid(UUID uuid);
	void insert(Course course);
	void update(Course course);
	void delete(Course course);
}
