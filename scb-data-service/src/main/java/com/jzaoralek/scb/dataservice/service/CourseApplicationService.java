package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;

public interface CourseApplicationService {
	List<CourseApplication> getAll();
	CourseApplication getByUuid(UUID uuid);
	CourseApplication store(CourseApplication courseApplication) throws ScbValidationException;
	void delete(UUID uuid);
}