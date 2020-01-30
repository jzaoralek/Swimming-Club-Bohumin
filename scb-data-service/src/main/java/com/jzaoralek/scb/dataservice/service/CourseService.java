package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseCourseParticipantVO;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;

public interface CourseService {

	List<Course> getAll(int yearFrom, int yearTo, boolean withLessons);
	List<Course> getByTrainer(UUID userUuid, int yearFrom, int yearTo, boolean withLessons);
	List<Course> getAllExceptCourse(UUID courseUuid);
	List<CourseParticipant> getByCourseParticListByCourseUuid(UUID courseUuid, boolean inclInterrupted);
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
	 * Copy course from orig to new course.
	 * @param courseOrigUuid
	 * @param courseNew
	 * @param copyParticipants - copy participants to new course
	 * @param copyLessons - copy lessons to new course
	 * @param copyTrainers - copy lessons to new course
	 * @return
	 * @throws ScbValidationException
	 */
	Course copy(UUID courseOrigUuid, 
			Course courseNew, 
			boolean copyParticipants, 
			boolean copyLessons, 
			boolean copyTrainers) throws ScbValidationException;

	/**
	 *  Build copy of course by uuid.
	 * @param courseUuid
	 * @param courseApplicationYear
	 * @param nameFromOrig
	 * @return
	 */
	Course buildCopy(UUID courseUuid, String courseApplicationYear, boolean nameFromOrig);
	
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
	
	CourseCourseParticipantVO getCourseCourseParticipantVO(UUID courseParticUuid, UUID courseUuid);
	
	List<ScbUser> getTrainersByCourse(UUID courseUuid);
	void addTrainersToCourse(List<ScbUser> trainers, UUID courseUuid);
	void removeTrainersFromCourse(List<ScbUser> trainers, UUID courseUuid);
	void updateState(List<UUID> courseUuidList, boolean active);
}
