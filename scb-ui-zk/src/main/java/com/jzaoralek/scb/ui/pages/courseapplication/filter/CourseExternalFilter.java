package com.jzaoralek.scb.ui.pages.courseapplication.filter;

import java.util.UUID;

public class CourseExternalFilter {

	private Boolean myCourses;
	private Boolean activeCourses;
	private UUID courseLocationUuid;

	public CourseExternalFilter(Boolean myCourses, Boolean activeCourses, UUID courseLocationUuid) {
		super();
		this.myCourses = myCourses;
		this.activeCourses = activeCourses;
		this.courseLocationUuid = courseLocationUuid;
	}
	
	public Boolean getMyCourses() {
		return myCourses;
	}
	public void setMyCourses(Boolean myCourses) {
		this.myCourses = myCourses;
	}
	public Boolean getActiveCourses() {
		return activeCourses;
	}
	public void setActiveCourses(Boolean activeCourses) {
		this.activeCourses = activeCourses;
	}
	public UUID getCourseLocationUuid() {
		return courseLocationUuid;
	}
	public void setCourseLocationUuid(UUID courseLocationUuid) {
		this.courseLocationUuid = courseLocationUuid;
	}

	@Override
	public String toString() {
		return "CourseExternalFilter [myCourses=" + myCourses + ", activeCourses=" + activeCourses
				+ ", courseLocationUuid=" + courseLocationUuid + "]";
	}	
}