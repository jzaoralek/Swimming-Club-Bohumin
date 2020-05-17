package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig;

public interface CourseApplDynAttrConfigDao {

	List<CourseApplDynAttrConfig> getAll();
	CourseApplDynAttrConfig getByUuid(UUID uuid);
	List<CourseApplDynAttrConfig> getByName(String name);
	void insert(CourseApplDynAttrConfig config);
	void update(CourseApplDynAttrConfig config);
	/** Used for activation and termination, 
	 * depends of terminate attribute. */
	void terminate(CourseApplDynAttrConfig config);
	void delete(UUID uuid);
}
