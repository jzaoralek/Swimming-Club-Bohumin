package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;

public interface CourseApplicationService {
	List<CourseApplication> getAll();
	CourseApplication getByUuid(UUID uuid);
	CourseApplication store(CourseApplication courseApplication);
	void delete(UUID uuid);
}