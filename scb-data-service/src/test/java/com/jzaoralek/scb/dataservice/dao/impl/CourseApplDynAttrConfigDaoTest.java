package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.CourseApplDynAttrConfigDao;
import com.jzaoralek.scb.dataservice.domain.Config;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig;
import com.jzaoralek.scb.dataservice.domain.Result;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig.CourseApplDynAttrConfigType;

public class CourseApplDynAttrConfigDaoTest extends BaseTestCase {

	private static final Date CREATED_AT = Calendar.getInstance().getTime();
	private static final String DESCRIPTION = "description";
	private static final String NAME = "name";
	private static final boolean REQUIRED = true;
	private static final CourseApplDynAttrConfigType type = CourseApplDynAttrConfigType.DOUBLE;
	
	@Autowired
	private CourseApplDynAttrConfigDao courseApplDynAttrConfigDao;
	
	private CourseApplDynAttrConfig item;
	
	@Before
	public void setUp() {
		item = new CourseApplDynAttrConfig();
		fillIdentEntity(item);
		item.setCreatedAt(CREATED_AT);
		item.setDescription(DESCRIPTION);
		item.setName(NAME);
		item.setRequired(true);
		item.setTerminatedAt(null);
		item.setType(CourseApplDynAttrConfigType.DOUBLE);
		
		courseApplDynAttrConfigDao.insert(item);
	}
	
	@Test
	public void testgetByUuid() {
		CourseApplDynAttrConfig item = courseApplDynAttrConfigDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(CREATED_AT.compareTo(item.getCreatedAt()) == 1);
		Assert.assertTrue(REQUIRED == item.isRequired());
		Assert.assertTrue(DESCRIPTION.equals(item.getDescription()));
		Assert.assertTrue(NAME.equals(item.getName()));
		Assert.assertTrue(type == item.getType());
		Assert.assertNull(item.getTerminatedAt());
	}
	
	@Test
	public void testGetAll() {
		List<CourseApplDynAttrConfig> itemList = courseApplDynAttrConfigDao.getAll();
		Assert.assertNotNull(itemList);
		Assert.assertTrue(itemList.size() == 1);
	}
	
	@Test
	public void testUpdate()  {
		CourseApplDynAttrConfig item = courseApplDynAttrConfigDao.getByUuid(ITEM_UUID);
		item.setName("nameNew");
		item.setDescription("descriptionNew");
		item.setRequired(false);

		courseApplDynAttrConfigDao.update(item);
		item = courseApplDynAttrConfigDao.getByUuid(ITEM_UUID);
		Assert.assertTrue(item.getName().equals("nameNew"));
		Assert.assertTrue(item.getDescription().equals("descriptionNew"));
		Assert.assertFalse(item.isRequired());
	}
	
	@Test
	public void testDelete() {
		courseApplDynAttrConfigDao.delete(ITEM_UUID);
		CourseApplDynAttrConfig item = courseApplDynAttrConfigDao.getByUuid(ITEM_UUID);
		Assert.assertNull(item);
	}
	
	public void testTerminate() {
		Date terminated = Calendar.getInstance().getTime();
		item.setTerminatedAt(terminated);
		courseApplDynAttrConfigDao.terminate(item);
		
		CourseApplDynAttrConfig itemTerminated = courseApplDynAttrConfigDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(itemTerminated);
		Assert.assertTrue(itemTerminated.getTerminatedAt().compareTo(terminated) == 1);
	}
}
