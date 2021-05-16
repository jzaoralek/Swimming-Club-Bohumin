package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;

public interface CourseApplicationFileConfigService {

	List<CourseApplicationFileConfig> getListForPage();
	List<CourseApplicationFileConfig> getListForEmail();
	Attachment getFileByUuid(UUID uuid);
	/**
	 * Get all course application file config.
	 * @return
	 */
	List<CourseApplicationFileConfig> getAll();
	
	/**
	 * Load course application file config by UUID.
	 * @param uuid
	 * @return
	 */
	CourseApplicationFileConfig getByUuid(UUID uuid);
	
	/**
	 * Create new course application file config.
	 * @param config
	 */
	void insert(CourseApplicationFileConfig config);
	
	/**
	 * Update course application file config.
	 * @param config
	 */
	void update(CourseApplicationFileConfig config);
	
	/**
	 * Delete course application file config.
	 * @param config
	 */
	void delete(CourseApplicationFileConfig config);
}
