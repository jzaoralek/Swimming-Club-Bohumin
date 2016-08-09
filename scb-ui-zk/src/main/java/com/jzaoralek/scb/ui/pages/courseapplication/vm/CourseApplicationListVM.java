package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.List;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseApplicationListVM extends BaseVM {

	@WireVariable
	private CourseApplicationService courseApplicationService;

	private List<CourseApplication> courseApplicationList;

	@Init
	public void init() {
		this.courseApplicationList = courseApplicationService.getAll();
	}

	public List<CourseApplication> getCourseApplicationList() {
		return courseApplicationList;
	}
}
