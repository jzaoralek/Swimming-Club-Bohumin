package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.Arrays;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.dao.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;

public class CourseParticipantDaoTest extends BaseTestCase {

	@Autowired
	private CourseParticipantDao courseParticipantDao;

	private CourseParticipant item;

	@Before
	public void setUp() {
		item = buildCourseParticipant();

		courseParticipantDao.insert(item);
	}

	@Test
	public void testGetByUuid() {
		CourseParticipant itemFromDb = courseParticipantDao.getByUuid(ITEM_UUID, false);
		Assert.assertNotNull(itemFromDb);
		Assert.assertTrue(ITEM_UUID.toString().equals(itemFromDb.getUuid().toString()));
		Assert.assertEquals(PARTIC_HEALTH_INFO, itemFromDb.getHealthInfo());
		Assert.assertEquals(PARTIC_PERSONAL_NO, itemFromDb.getPersonalNo());
	}

	@Test
	public void testGetByCourseUuid() {
		assertList(courseParticipantDao.getByCourseUuid(UUID.randomUUID()), 0, null);
	}

	@Test
	public void testDeleteParticipantFromCourse() {
		courseParticipantDao.deleteParticipantFromCourse(UUID.randomUUID(), ITEM_UUID);
	}

	@Test
	public void testDeleteAllFromCourse() {
		courseParticipantDao.deleteAllFromCourse(ITEM_UUID);
	}

	@Test
	public void testInsetToCourse() {
		courseParticipantDao.insetToCourse(Arrays.asList(buildCourseParticipant()), ITEM_UUID);
	}

	@Test
	public void testUpdate() {
		String PARTIC_HEALTH_INFO_UPDATED = "healthInfoUpdated";
		String PARTIC_PERSONAL_NO_UPDATED = "3625148889";
		item.setHealthInfo(PARTIC_HEALTH_INFO_UPDATED);
		item.setPersonalNo(PARTIC_PERSONAL_NO_UPDATED);

		courseParticipantDao.update(item);

		CourseParticipant itemFromDb = courseParticipantDao.getByUuid(ITEM_UUID, false);
		Assert.assertNotNull(itemFromDb);
		Assert.assertTrue(ITEM_UUID.toString().equals(itemFromDb.getUuid().toString()));
		Assert.assertEquals(PARTIC_HEALTH_INFO_UPDATED, itemFromDb.getHealthInfo());
		Assert.assertEquals(PARTIC_PERSONAL_NO_UPDATED, itemFromDb.getPersonalNo());
	}

	@Test
	public void testDelete() {
		courseParticipantDao.delete(item);
		Assert.assertNull(courseParticipantDao.getByUuid(ITEM_UUID, false));
	}
}
