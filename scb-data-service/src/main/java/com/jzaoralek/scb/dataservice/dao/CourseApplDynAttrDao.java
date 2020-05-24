package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttr;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;

public interface CourseApplDynAttrDao {

	List<CourseApplDynAttr> getByCourseAppl(CourseApplication courseAppl);
	CourseApplDynAttr getByUuid(UUID uuid);
	void insert(CourseApplDynAttr dynAttr);
	void update(CourseApplDynAttr dynAttr);
	void deleteByCourseAppl(UUID courseApplUuid);
}