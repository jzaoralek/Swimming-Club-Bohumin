package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.dao.CourseApplDynAttrConfigDao;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.CourseApplDynAttrConfigService;

@Service("courseApplDynAttrConfigService")
public class CourseApplDynAttrConfigServiceImpl extends BaseAbstractService implements CourseApplDynAttrConfigService {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplDynAttrConfigServiceImpl.class);
	
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
	public void store(CourseApplDynAttrConfig config) throws ScbValidationException {
		if (config == null) {
			throw new IllegalArgumentException("config is null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing config: " + config);
		}
		
		boolean insertMode = config.getUuid() == null;
		fillIdentEntity(config);
		
		// check if exist config with the same name
		List<CourseApplDynAttrConfig> configByNameList = 
				courseApplDynAttrConfigDao.getByName(config.getName());
		if ((insertMode && !CollectionUtils.isEmpty(configByNameList))
				|| (!insertMode && configByNameList.size() > 1)) {
			LOG.warn("CourseApplDynAttrConfig with same name exists, name: " + config.getName());
			throw new ScbValidationException(
					messageSource.getMessage("msg.validation.warn.courseApplDynAttrWithSameNameExists", 
							new Object[] {config.getName()}, Locale.getDefault()));
		}
		
		// store to DB
		if (insertMode) {
			courseApplDynAttrConfigDao.insert(config);			
		} else {
			courseApplDynAttrConfigDao.update(config);
		}
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
