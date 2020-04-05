package com.jzaoralek.scb.ui.pages.courseapplication.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.domain.Course;

public class CourseFilter {
	
	private String courseName;
	private String courseNameLc;

	public boolean matches(String courseNameIn, boolean emptyMatch) {
		if (courseName == null) {
			return emptyMatch;
		}
		if (courseName != null && !courseNameIn.toLowerCase().contains(courseNameLc)) {
			return false;
		}
		return true;
	}

	public List<Course> getApplicationListFiltered(List<Course> courseList) {
		if (courseList == null || courseList.isEmpty()) {
			return Collections.<Course>emptyList();
		}
		List<Course> ret = new ArrayList<Course>();
		for (Course item : courseList) {
			if (matches(item.getName(), true)) {
				ret.add(item);
			}
		}
		return ret;
	}

	public String getCourseName() {
		return courseName == null ? "" : courseName;
	}
	public void setCourseName(String name) {
		this.courseName = StringUtils.hasText(name) ? name.trim() : null;
		this.courseNameLc = this.courseName == null ? null : this.courseName.toLowerCase();
	}

	public void setEmptyValues() {
		courseName = null;
		courseNameLc = null;
	}
}
