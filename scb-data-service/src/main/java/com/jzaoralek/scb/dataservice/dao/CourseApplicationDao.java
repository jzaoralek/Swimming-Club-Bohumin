package com.jzaoralek.scb.dataservice.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;

public interface CourseApplicationDao {

	List<CourseApplication> getAll(int yearFrom, int yearTo);
	List<CourseApplication> getUnregisteredToCurrYear(int yearFromPrev, int yearToPrev);
	List<CourseApplication> getNotInCourse(UUID courseUuid, int yearFrom, int yearTo);
	List<CourseApplication> getInCourse(UUID courseUuid, int yearFrom, int yearTo);
	List<CourseApplication> getAssignedToCourse(int yearFrom, int yearTo);
	List<CourseApplication> getByCourseParticipantUuid(UUID courseParticipantUuid);
	CourseApplication getByUuid(UUID uuid, boolean deep);
	void insert(CourseApplication courseApplication);
	void update(CourseApplication courseApplication);
	void updatePayed(CourseApplication courseApplication, boolean payed);
	void updateNotifiedPayment(List<UUID> courseParticUuidList, Date notifiedAt, boolean firstSemester);
	void delete(CourseApplication courseApplication);
}