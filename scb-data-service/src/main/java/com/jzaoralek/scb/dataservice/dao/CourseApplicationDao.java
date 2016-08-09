package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;

public interface CourseApplicationDao {

	List<CourseApplication> getAll();
	CourseApplication getByUuid(UUID uuid, boolean deep);
	void insert(CourseApplication courseApplication);
	void update(CourseApplication courseApplication);
	void delete(CourseApplication courseApplication);
}