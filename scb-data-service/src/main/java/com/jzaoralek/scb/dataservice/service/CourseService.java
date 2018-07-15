package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;

public interface CourseService {

	List<Course> getAll(int yearFrom, int yearTo, boolean withLessons);
	List<Course> getAllExceptCourse(UUID courseUuid);
	List<CourseParticipant> getByCourseParticListByCourseUuid(UUID courseUuid);
	List<Course> getByCourseParticipantUuid(UUID courseParticipantUuid, int yearFrom, int yearTo);
	/**
	 * Get all participants for participant representative.
	 * @param userUuid
	 * @return
	 */
	List<CourseParticipant> getCourseParticListByRepresentativeUuid(UUID representativeUserUuid);
	CourseParticipant getCourseParticipantByUuid(UUID uuid);
	Course getByUuid(UUID uuid);
	Course getPlainByUuid(UUID uuid);
	Course store(Course course) throws ScbValidationException;
	/**
	 * Return course participant with one course.
	 * @param courseCourseParticUuid
	 * @return
	 */
	CourseParticipant getCourseParticInOneCourse(UUID courseCourseParticUuid);
	void delete(UUID uuid) throws ScbValidationException;
	void storeCourseParticipants(List<CourseParticipant> newCourseParticipantList, UUID courseUuid) throws ScbValidationException;
	void deleteParticipantFromCourse(UUID participantUuid, UUID courseUuid);
	
	List<CourseLocation> getCourseLocationAll();
	CourseLocation getCourseLocationByUuid(UUID uuid);
	CourseLocation store(CourseLocation courseLocatiom);
	void deleteCourseLocation(CourseLocation courseLocatiom);
	boolean existsByCourseLocation(UUID courseLocationUuid);
}
