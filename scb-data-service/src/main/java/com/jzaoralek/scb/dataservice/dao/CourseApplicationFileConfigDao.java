package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;

public interface CourseApplicationFileConfigDao {

	/**
	 * Files displayed on course application page.
	 * @return
	 */
	List<CourseApplicationFileConfig> getListForPage();
	/**
	 * Files attached to notification course application email.
	 * @return
	 */
	List<CourseApplicationFileConfig> getListForEmail();
	/**
	 * Load file by UUID.
	 * @param uuid
	 * @return
	 */
	Attachment getFileByUuid(UUID uuid);
	
	/**
	 * Get all course application file config.
	 * @return
	 */
	List<CourseApplicationFileConfig> getAll();
	
	/**
	 * Create new course application file config.
	 * @param config
	 */
	void insert(CourseApplicationFileConfig config);
	
	/**
	 * update course application file config.
	 * @param config
	 */
	void update(CourseApplicationFileConfig config);
	
	/**
	 * Delete course application file config.
	 * @param config
	 */
	void delete(CourseApplicationFileConfig config);
}
