package com.jzaoralek.scb.dataservice.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;

public interface CourseApplicationService {
	List<CourseApplication> getAll(int yearFrom, int yearTo);
	List<CourseApplication> getUnregisteredToCurrYear(int yearFromPrev, int yearToPrev);
	List<CourseApplication> getNotInCourse(UUID courseUuid, int yearFrom, int yearTo);
	List<CourseApplication> getInCourse(UUID courseUuid, int yearFrom, int yearTo);
	List<CourseApplication> getAssignedToCourse(int yearFrom, int yearTo, CourseType courseType);
	List<CourseApplication> getByCourseParticipantUuid(UUID courseParticipantUuid);
	CourseApplication getByUuid(UUID uuid);
	boolean existsByPersonalNumber(String personalNumber);
	void storeCourseParticipant(CourseParticipant courseParticipant);
	CourseApplication store(CourseApplication courseApplication) throws ScbValidationException;
	void updatePayed(UUID uuid, boolean payed) throws ScbValidationException;
	void updateNotifiedPayment(List<UUID> courseParticUuidList, boolean firstSemester);
	void updateCourseParticInterruption(List<UUID> courseParticUuidList, Date interrupetdAt);
	void updateCourseParticCourseUuid(List<UUID> courseParticUuidList, UUID courseUuid);
	void insertCourseParticInterruption(UUID courseCourseParticUuid, UUID courseUuid, Date interrupetdAt);
	void delete(UUID uuid) throws ScbValidationException;
}