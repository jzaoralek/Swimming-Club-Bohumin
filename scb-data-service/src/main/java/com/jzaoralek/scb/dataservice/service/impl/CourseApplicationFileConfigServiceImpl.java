package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.Objects;
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
		return courseApplicationFileConfigDao.getListForPage();
	}

	@Override
	public List<CourseApplicationFileConfig> getListForEmail() {
		return courseApplicationFileConfigDao.getListForEmail();
	}

	@Override
	public Attachment getFileByUuid(UUID uuid) {
		return courseApplicationFileConfigDao.getFileByUuid(uuid);
	}

	@Override
	public List<CourseApplicationFileConfig> getAll() {
		return courseApplicationFileConfigDao.getAll();
	}

	@Override
	public CourseApplicationFileConfig getByUuid(UUID uuid) {
		Objects.requireNonNull(uuid, "uuid is null");
		return courseApplicationFileConfigDao.getByUuid(uuid);
	}

	@Override
	public void insert(CourseApplicationFileConfig config) {
		Objects.requireNonNull(config, "config is null");
		courseApplicationFileConfigDao.insert(config);
	}

	@Override
	public void update(CourseApplicationFileConfig config) {
		Objects.requireNonNull(config, "config is null");
		courseApplicationFileConfigDao.update(config);
	}

	@Override
	public void delete(CourseApplicationFileConfig config) {
		Objects.requireNonNull(config, "config is null");
		courseApplicationFileConfigDao.delete(config);
	}
}
