package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.dao.CourseApplicationFileConfigDao;
import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationFileConfigService;

@Service("courseApplicationFileConfigService")
public class CourseApplicationFileConfigServiceImpl extends BaseAbstractService implements CourseApplicationFileConfigService {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplicationFileConfigServiceImpl.class);
	
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
	public void store(CourseApplicationFileConfig config) {
		Objects.requireNonNull(config, "config is null");

		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing config: " + config);
		}
		
		boolean insertMode = config.getUuid() == null;
		fillIdentEntity(config);
		
		// store to DB
		if (insertMode) {
			courseApplicationFileConfigDao.insert(config);			
		} else {
			courseApplicationFileConfigDao.update(config);
		}
	}

	@Override
	public void delete(CourseApplicationFileConfig config) {
		Objects.requireNonNull(config, "config is null");
		courseApplicationFileConfigDao.delete(config);
	}
}
