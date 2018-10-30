package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;

public interface CourseApplicationFileConfigService {

	List<CourseApplicationFileConfig> getListForPage();
	List<CourseApplicationFileConfig> getListForEmail();
	Attachment getFileByUuid(UUID uuid);
}
