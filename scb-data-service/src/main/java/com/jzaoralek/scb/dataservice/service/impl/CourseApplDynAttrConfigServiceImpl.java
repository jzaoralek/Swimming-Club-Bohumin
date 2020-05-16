package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.dao.ConfigurationDao;
import com.jzaoralek.scb.dataservice.dao.CourseApplDynAttrConfigDao;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig;
import com.jzaoralek.scb.dataservice.service.CourseApplDynAttrConfigService;

@Service("courseApplDynAttrConfigService")
public class CourseApplDynAttrConfigServiceImpl implements CourseApplDynAttrConfigService {

	@Autowired
	private CourseApplDynAttrConfigDao courseApplDynAttrConfigDao;
	
	@Override
	public List<CourseApplDynAttrConfig> getAll() {
		return courseApplDynAttrConfigDao.getAll();
	}

	@Override
	public CourseApplDynAttrConfig getByUuid(UUID uuid) {
		return courseApplDynAttrConfigDao.getByUuid(uuid);
	}

	@Override
	public void insert(CourseApplDynAttrConfig config) {
		// TODO: validace, že neexistuje položka se stejným jménem
		courseApplDynAttrConfigDao.insert(config);
	}

	@Override
	public void update(CourseApplDynAttrConfig config) {
		// TODO: validace, že neexistuje položka se stejným jménem
		courseApplDynAttrConfigDao.update(config);
	}

	@Override
	public void terminate(CourseApplDynAttrConfig config) {
		courseApplDynAttrConfigDao.terminate(config);
	}

	@Override
	public void delete(UUID uuid) {
		courseApplDynAttrConfigDao.delete(uuid);
	}
}
