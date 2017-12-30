package com.jzaoralek.scb.ui.pages.payments.vm;

import org.zkoss.bind.annotation.Init;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

public class BankPaymentUnpairedWinVM {

	private CourseParticipant coursePartic;
	private Course course;

	@Init
	public void init() {
		this.coursePartic = (CourseParticipant) WebUtils.getArg(WebConstants.COURSE_PARTIC_PARAM);
		this.course = (Course) WebUtils.getArg(WebConstants.COURSE_PARAM);
	}
	
	public CourseParticipant getCoursePartic() {
		return coursePartic;
	}
	
	public Course getCourse() {
		return course;
	}
}
