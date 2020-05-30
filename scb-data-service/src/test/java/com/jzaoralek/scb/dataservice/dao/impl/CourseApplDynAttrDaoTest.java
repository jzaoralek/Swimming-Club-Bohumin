package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.CourseApplDynAttrConfigDao;
import com.jzaoralek.scb.dataservice.dao.CourseApplDynAttrDao;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttr;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;

public class CourseApplDynAttrDaoTest extends BaseTestCase {

	private static final String TEXT_VALUE = "textValue";
	private static final Date DATE_VALUE = Calendar.getInstance().getTime();
	private static final int INT_VALUE = 100;
	private static final double DOUBLE_VALUE = 100.2;	
	private static final boolean BOOLEAN_VALUE = true;
	
	private UUID dynAttrConfigUuid;
	private UUID courseParticipantUuid;
	
	@Autowired
	private CourseApplDynAttrDao courseApplDynAttrDao;
	
	@Autowired
	private CourseApplDynAttrConfigDao courseApplDynAttrConfigDao;
	
	private CourseApplDynAttr item;
	private CourseParticipant courseParticipant;
	
	@Before
	public void setUp() {
		item = new CourseApplDynAttr();
		fillIdentEntity(item);
		
		
		CourseApplDynAttrConfig config = buildCourseApplDynAttrConfig();
		this.dynAttrConfigUuid = config.getUuid();
		courseApplDynAttrConfigDao.insert(config);
		
		item.setCourseApplDynConfig(config);
				
		item.setTextValue(TEXT_VALUE);
		item.setDateValue(DATE_VALUE);
		item.setIntValue(INT_VALUE);
		item.setDoubleValue(DOUBLE_VALUE);
		item.setBooleanValue(BOOLEAN_VALUE);
		
		CourseApplication courseApplication = buildCourseApplication();
		this.courseParticipant = courseApplication.getCourseParticipant();
		this.courseParticipantUuid = this.courseParticipant.getUuid();
		courseApplicationDao.insert(courseApplication);
		
		item.setCourseParticUuid(courseParticipantUuid);
		courseApplDynAttrDao.insert(item);
	}
	
	@Test
	public void testGetByCourseAppl() {
		List<CourseApplDynAttr> dynAttrList = courseApplDynAttrDao.getByCoursePartic(this.courseParticipant);
		Assert.assertNotNull(dynAttrList);
		Assert.assertTrue(dynAttrList.size() == 1);
		CourseApplDynAttr item = dynAttrList.get(0);
		assertItemValues(item);
	}
	
	@Test
	public void testGetByUuid() {
		CourseApplDynAttr item = courseApplDynAttrDao.getByUuid(ITEM_UUID);
		assertItemValues(item);
	}
	
	private void assertItemValues(CourseApplDynAttr item) {
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(courseParticipantUuid.toString().equals(item.getCourseParticUuid().toString()));
		Assert.assertTrue(dynAttrConfigUuid.toString().equals(item.getCourseApplDynConfig().getUuid().toString()));
		Assert.assertTrue(TEXT_VALUE.equals(item.getTextValue()));
		Assert.assertTrue(DATE_VALUE.compareTo(item.getDateValue()) == 1);
		Assert.assertTrue(INT_VALUE == item.getIntValue());
		Assert.assertTrue(DOUBLE_VALUE == item.getDoubleValue());
		Assert.assertTrue(BOOLEAN_VALUE == item.isBooleanValue());
	}
	
	@Test
	public void testUpdate() {
		CourseApplDynAttr item = courseApplDynAttrDao.getByUuid(ITEM_UUID);
		
		String textValueUpdated = "textValueUpdated";
		Date dateValueUpdated = Calendar.getInstance().getTime();
		int intValueUpdated = 200;
		double doubleValueUpdated = 200.6;
		boolean booleanUpdated = false;
		
		item.setTextValue(textValueUpdated);
		item.setDateValue(dateValueUpdated);
		item.setIntValue(intValueUpdated);
		item.setDoubleValue(doubleValueUpdated);
		item.setBooleanValue(booleanUpdated);

		courseApplDynAttrDao.update(item);
		item = courseApplDynAttrDao.getByUuid(ITEM_UUID);
		Assert.assertTrue(textValueUpdated.equals(item.getTextValue()));
		Assert.assertTrue(dateValueUpdated.compareTo(item.getDateValue()) == 1);
		Assert.assertTrue(intValueUpdated == item.getIntValue());
		Assert.assertTrue(doubleValueUpdated == item.getDoubleValue());
		Assert.assertTrue(booleanUpdated == item.isBooleanValue());
	}
	
	@Test
	public void testDeleteByCoursePartic() {
		courseApplDynAttrDao.deleteByCoursePartic(courseParticipantUuid);
		CourseApplDynAttr item = courseApplDynAttrDao.getByUuid(ITEM_UUID);
		Assert.assertNull(item);
	}
}