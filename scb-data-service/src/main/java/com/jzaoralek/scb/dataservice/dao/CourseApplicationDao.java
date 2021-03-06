package com.jzaoralek.scb.dataservice.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;

public interface CourseApplicationDao {

	List<CourseApplication> getAll(int yearFrom, int yearTo);
	List<CourseApplication> getUnregisteredToCurrYear(int yearFromPrev, int yearToPrev);
	List<CourseApplication> getNotInCourse(UUID courseUuid, int yearFrom, int yearTo);
	List<CourseApplication> getInCourse(UUID courseUuid, int yearFrom, int yearTo);
	List<CourseApplication> getAssignedToCourse(int yearFrom, int yearTo, CourseType courseType);
	List<CourseApplication> getByCourseParticipantUuid(UUID courseParticipantUuid);
	CourseApplication getByUuid(UUID uuid, boolean deep);
	void insert(CourseApplication courseApplication);
	void update(CourseApplication courseApplication);
	void updatePayed(CourseApplication courseApplication, boolean payed);
	void updateNotifiedPayment(List<UUID> courseParticUuidList, Date notifiedAt, boolean firstSemester);
	void updateCourseParticInterruption(List<UUID> courseCourseParticUuidList, Date interrupetdAt);
	void updateCourseParticCourseUuid(List<UUID> courseCourseParticUuidList, UUID  courseUuid);
	void insertCourseParticInterruption(UUID uuid, UUID courseCourseParticUuid, UUID courseUuid, Date interrupetdAt);
	void delete(CourseApplication courseApplication);
}