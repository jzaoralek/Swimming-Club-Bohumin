package com.jzaoralek.scb.dataservice.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttr;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;

public interface CourseApplDynAttrDao {

	List<CourseApplDynAttr> getByDate(Date date);
	List<CourseApplDynAttr> getByCoursePartic(CourseParticipant coursePartic);
	CourseApplDynAttr getByUuid(UUID uuid);
	void insert(CourseApplDynAttr dynAttr);
	void update(CourseApplDynAttr dynAttr);
	void deleteByCoursePartic(UUID courseParticUuid);
}