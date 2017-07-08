package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;

public interface CourseApplicationService {
	List<CourseApplication> getAll(int yearFrom, int yearTo);
	List<CourseApplication> getNotInCourse(UUID courseUuid, int yearFrom, int yearTo);
	List<CourseApplication> getInCourse(UUID courseUuid, int yearFrom, int yearTo);
	List<CourseApplication> getAssignedToCourse(int yearFrom, int yearTo);
	List<CourseApplication> getByCourseParticipantUuid(UUID courseParticipantUuid);
	CourseApplication getByUuid(UUID uuid);
	boolean existsByPersonalNumber(String personalNumber);
	void storeCourseParticipant(CourseParticipant courseParticipant);
	CourseApplication store(CourseApplication courseApplication) throws ScbValidationException;
	void updatePayed(UUID uuid, boolean payed) throws ScbValidationException;
	void delete(UUID uuid) throws ScbValidationException;
}