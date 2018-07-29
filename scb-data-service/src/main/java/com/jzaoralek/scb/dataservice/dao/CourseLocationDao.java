package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseLocation;

public interface CourseLocationDao {

	List<CourseLocation> getAll();
	CourseLocation getByUuid(UUID uuid);
	void insert(CourseLocation courseLocatiom);
	void update(CourseLocation courseLocatiom);
	void delete(CourseLocation courseLocatiom);
}
