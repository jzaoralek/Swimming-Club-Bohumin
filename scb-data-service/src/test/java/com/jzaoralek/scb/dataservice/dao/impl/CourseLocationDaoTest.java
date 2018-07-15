package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.CourseLocationDao;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;

public class CourseLocationDaoTest extends BaseTestCase {

	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	
	@Autowired
	private CourseLocationDao courseLocationDao;

	private CourseLocation item;

	@Before
	public void setUp() {
		item = new CourseLocation();
		if (item != null && item.getUuid() == null) {
			item.setUuid(ITEM_UUID);
		}
		item.setDescription(DESCRIPTION);
		item.setName(NAME);

		courseLocationDao.insert(item);
	}
	
	@Test
	public void testGetAll() {
		List<CourseLocation> list = courseLocationDao.getAll();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 1);
		if (!CollectionUtils.isEmpty(list)) {
			CourseLocation item = list.get(0);
			Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		}
	}

	@Test
	public void testGetByUuid() {
		CourseLocation item = courseLocationDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(NAME.equals(item.getName()));
		Assert.assertTrue(DESCRIPTION.equals(item.getDescription()));
	}
	
	@Test
	public void testUpdate() {
		String DESCRIPTION_UPDATED = "descriptionUpdated";
		String NAME_UPDATED = "nameUpdated";

		item.setDescription(DESCRIPTION_UPDATED);
		item.setName(NAME_UPDATED);

		courseLocationDao.update(item);

		CourseLocation itemUpdated = courseLocationDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(itemUpdated);
		Assert.assertTrue(ITEM_UUID.toString().equals(itemUpdated.getUuid().toString()));
		Assert.assertTrue(NAME_UPDATED.equals(itemUpdated.getName()));
		Assert.assertTrue(DESCRIPTION_UPDATED.equals(itemUpdated.getDescription()));
	}

	@Test
	public void testDelete() {
		courseLocationDao.delete(item);
		Assert.assertNull(courseLocationDao.getByUuid(ITEM_UUID));
	}
}
