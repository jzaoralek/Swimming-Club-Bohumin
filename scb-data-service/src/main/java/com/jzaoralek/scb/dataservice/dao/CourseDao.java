package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.ScbUser;

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
	List<ScbUser> getTrainersByCourse(UUID courseUuid);
	void addTrainersToCourse(List<ScbUser> trainers, UUID courseUuid);
	void removeTrainersFromCourse(List<ScbUser> trainers, UUID courseUuid);
	void removeAllTrainersFromCourse(UUID courseUuid);
}
