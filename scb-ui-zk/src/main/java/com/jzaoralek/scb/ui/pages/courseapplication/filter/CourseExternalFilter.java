package com.jzaoralek.scb.ui.pages.courseapplication.filter;

import java.util.UUID;

public class CourseExternalFilter {

	private Boolean myCourses;
	private UUID courseLocationUuid;

	public CourseExternalFilter(Boolean myCourses, UUID courseLocationUuid) {
		super();
		this.myCourses = myCourses;
		this.courseLocationUuid = courseLocationUuid;
	}
	
	public Boolean getMyCourses() {
		return myCourses;
	}
	public void setMyCourses(Boolean myCourses) {
		this.myCourses = myCourses;
	}
	public UUID getCourseLocationUuid() {
		return courseLocationUuid;
	}
	public void setCourseLocationUuid(UUID courseLocationUuid) {
		this.courseLocationUuid = courseLocationUuid;
	}

	@Override
	public String toString() {
		return "CourseExternalFilter [myCourses=" + myCourses + ", courseLocationUuid=" + courseLocationUuid + "]";
	}
}
