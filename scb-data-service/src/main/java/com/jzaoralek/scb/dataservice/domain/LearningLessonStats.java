package com.jzaoralek.scb.dataservice.domain;

import java.util.List;

/*
 * Polozka lekce pro statistiku dochazky, u kazde list participantu s boolean attendance 
 */
public class LearningLessonStats {
	
	private LearningLesson learningLesson;
	private List<CourseParticipant> courseParticipantList;
	
	public LearningLessonStats(LearningLesson learningLesson, List<CourseParticipant> courseParticipantList) {
		super();
		this.learningLesson = learningLesson;
		this.courseParticipantList = courseParticipantList;
	}
	
	public LearningLesson getLearningLesson() {
		return learningLesson;
	}
	public void setLearningLesson(LearningLesson learningLesson) {
		this.learningLesson = learningLesson;
	}
	public List<CourseParticipant> getCourseParticipantList() {
		return courseParticipantList;
	}
	public void setCourseParticipantList(List<CourseParticipant> courseParticipantList) {
		this.courseParticipantList = courseParticipantList;
	}
	
	@Override
	public String toString() {
		return "LearningLessonStats [learningLesson=" + learningLesson + ", courseParticipantList="
				+ courseParticipantList + "]";
	}
}
