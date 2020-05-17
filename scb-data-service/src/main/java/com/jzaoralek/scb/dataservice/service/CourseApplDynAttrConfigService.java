package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;

public interface CourseApplDynAttrConfigService {

	List<CourseApplDynAttrConfig> getAll();
	CourseApplDynAttrConfig getByUuid(UUID uuid);
	void store(CourseApplDynAttrConfig config) throws ScbValidationException;
	/** Used for activation and termination, 
	 * depends of terminate attribute. */
	void terminate(CourseApplDynAttrConfig config);
	void delete(UUID uuid);
}
