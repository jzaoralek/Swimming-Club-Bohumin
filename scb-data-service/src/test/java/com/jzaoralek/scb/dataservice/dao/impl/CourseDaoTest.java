package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.CourseDao;
import com.jzaoralek.scb.dataservice.domain.Course;

public class CourseDaoTest extends BaseTestCase {

	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final Long PRICE_SEMESTER_1 = 2000L;
	private static final Long PRICE_SEMESTER_2 = 3000L;

	@Autowired
	private CourseDao courseDao;

	private Course item;

	@Before
	public void setUp() {
		item = new Course();
		fillIdentEntity(item);
		item.setDescription(DESCRIPTION);
		item.setName(NAME);
		item.setYearFrom(YEAR_FROM);
		item.setYearTo(YEAR_TO);
		item.setPriceSemester1(PRICE_SEMESTER_1);
		item.setPriceSemester2(PRICE_SEMESTER_2);

		courseDao.insert(item);
	}

	@Test
	public void testGetAll() {
		assertList(courseDao.getAll(YEAR_FROM, YEAR_TO), 1, ITEM_UUID);
	}

	@Test
	public void testGetAllExceptCourse() {
		assertList(courseDao.getAllExceptCourse(UUID.randomUUID()), 1, ITEM_UUID);
	}

	@Test
	public void testGetByUuid() {
		Course item = courseDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(YEAR_FROM == item.getYearFrom());
		Assert.assertTrue(YEAR_TO == item.getYearTo());
		Assert.assertTrue(NAME.equals(item.getName()));
		Assert.assertTrue(DESCRIPTION.equals(item.getDescription()));
	}

	@Test
	public void testGetPlainByUuid() {
		Course item = courseDao.getPlainByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(YEAR_FROM == item.getYearFrom());
		Assert.assertTrue(YEAR_TO == item.getYearTo());
		Assert.assertTrue(NAME.equals(item.getName()));
		Assert.assertTrue(DESCRIPTION.equals(item.getDescription()));
	}
	
	@Test
	public void testGetByCourseParticipantUuid() {
		assertList(courseDao.getByCourseParticipantUuid(UUID.randomUUID(), YEAR_FROM, YEAR_TO), 0 , null);
	}

	@Test
	public void testUpdate() {
		String DESCRIPTION_UPDATED = "descriptionUpdated";
		String NAME_UPDATED = "nameUpdated";
		int YEAR_FROM_UPDATED = 2017;
		int YEAR_TO_UPDATED = 2018;

		item.setDescription(DESCRIPTION_UPDATED);
		item.setName(NAME_UPDATED);
		item.setYearFrom(YEAR_FROM_UPDATED);
		item.setYearTo(YEAR_TO_UPDATED);

		courseDao.update(item);

		Course itemUpdated = courseDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(itemUpdated);
		Assert.assertTrue(ITEM_UUID.toString().equals(itemUpdated.getUuid().toString()));
		Assert.assertTrue(YEAR_FROM_UPDATED == itemUpdated.getYearFrom());
		Assert.assertTrue(YEAR_TO_UPDATED == itemUpdated.getYearTo());
		Assert.assertTrue(NAME_UPDATED.equals(itemUpdated.getName()));
		Assert.assertTrue(DESCRIPTION_UPDATED.equals(itemUpdated.getDescription()));
	}

	@Test
	public void testDelete() {
		courseDao.delete(item);
		Assert.assertNull(courseDao.getByUuid(ITEM_UUID));
	}
}
