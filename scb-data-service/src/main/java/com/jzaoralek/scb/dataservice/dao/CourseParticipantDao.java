package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseParticipant;

public interface CourseParticipantDao {

	CourseParticipant getByUuid(UUID uuid, boolean deep);
	/**
	 * Return course participant with one course.
	 * @param courseCourseParticUuid
	 * @return
	 */
	CourseParticipant getCourseParticInOneCourse(UUID courseCourseParticUuid);
	boolean existsByPersonalNumber(String personalNumber);
	CourseParticipant getByPersonalNumberAndInterval(String personalNumber, int yearFrom, int yearTo);
	List<CourseParticipant> getByCourseUuid(UUID courseUuid);
	List<CourseParticipant> getByLearningLessonUuid(UUID learningLessonUuid);
	List<CourseParticipant> getByUserUuid(UUID userUuid);
	void insertToLearningLesson(UUID learningLessonUuid, List<CourseParticipant> participantList);
	void deleteAllFromLearningLesson(UUID learningLessonUuid);
	void deleteParticipantFromCourse(UUID participantUuid, UUID courseUuid);
	void deleteAllFromCourse(UUID courseUuid);
	void insetToCourse(List<CourseParticipant> courseParticipantList, UUID courseUuid);
	void insert(CourseParticipant courseParticipant);
	void update(CourseParticipant courseParticipant);
	void delete(CourseParticipant courseParticipant);
}
