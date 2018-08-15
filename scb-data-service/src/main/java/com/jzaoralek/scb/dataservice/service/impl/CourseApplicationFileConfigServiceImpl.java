package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.dao.CourseApplicationFileConfigDao;
import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationFileConfigService;

@Service("courseApplicationFileConfigService")
public class CourseApplicationFileConfigServiceImpl extends BaseAbstractService implements CourseApplicationFileConfigService {

	@Autowired
	private CourseApplicationFileConfigDao courseApplicationFileConfigDao;
	
	@Override
	public List<CourseApplicationFileConfig> getListForPage() {
		return null;
	}

	@Override
	public List<CourseApplicationFileConfig> getListForEmail() {
		return null;
	}

	@Override
	public Attachment getFileByUuid(UUID uuid) {
		return courseApplicationFileConfigDao.getFileByUuid(uuid);
	}

}
