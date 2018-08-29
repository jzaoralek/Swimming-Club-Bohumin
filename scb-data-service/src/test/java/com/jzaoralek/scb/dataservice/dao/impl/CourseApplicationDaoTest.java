package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.ContactDao;
import com.jzaoralek.scb.dataservice.dao.CourseApplicationDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.dao.ScbUserDao;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;


public class CourseApplicationDaoTest extends BaseTestCase {

	@Autowired
	private CourseApplicationDao courseApplicationDao;

	@Autowired
	private ScbUserDao scbUserDao;

	@Autowired
	private ContactDao contactDao;

	@Autowired
	private CourseParticipantDao courseParticipantDao;

	private UUID COURSE_PARTICIPANT_UUID;

	private CourseApplication item;

	@Before
	public void setUp() {
		item = new CourseApplication();
		fillIdentEntity(item);
		CourseParticipant courseParticipant = buildCourseParticipantUUIDGenerated();
		COURSE_PARTICIPANT_UUID = courseParticipant.getUuid();
		item.setCourseParticipant(courseParticipant);
		item.setCourseParticRepresentative(buildScbUser());
		item.setYearFrom(YEAR_FROM);
		item.setYearTo(YEAR_TO);

		contactDao.insert(item.getCourseParticipant().getContact());
		courseParticipantDao.insert(item.getCourseParticipant());

		contactDao.insert(item.getCourseParticRepresentative().getContact());
		scbUserDao.insert(item.getCourseParticRepresentative());
		courseApplicationDao.insert(item);
	}

	@After
    public void tearDown() {
    }

	@Test
	public void testGetAll() {
		assertList(courseApplicationDao.getAll(YEAR_FROM, YEAR_TO), 1, ITEM_UUID);
	}
	
	@Test
	public void testGetUnregisteredToCurrYear() {
		courseApplicationDao.delete(item);
		item.setYearFrom(YEAR_FROM + 1);
		item.setYearTo(YEAR_TO + 1);
		courseApplicationDao.insert(item);
		assertList(courseApplicationDao.getUnregisteredToCurrYear(YEAR_FROM, YEAR_TO), 0, ITEM_UUID);
	}


	@Test
	public void testGetNotInCourse() {
		assertList(courseApplicationDao.getNotInCourse(UUID.randomUUID(), YEAR_FROM, YEAR_TO), 1, ITEM_UUID);
	}

	@Test
	public void testGetInCourse() {
		assertList(courseApplicationDao.getInCourse(UUID.randomUUID(), YEAR_FROM, YEAR_TO), 0, ITEM_UUID);
	}

	@Test
	public void testGetAssignedToCourse() {
		assertList(courseApplicationDao.getAssignedToCourse(YEAR_FROM, YEAR_TO), 0, ITEM_UUID);
	}
	
	public void testUpdateNotifiedPayment() {
		courseApplicationDao.updateNotifiedPayment(Arrays.asList(COURSE_PARTICIPANT_UUID), Calendar.getInstance().getTime(), true);
	}
	
	@Test
	public void testGetByCourseParticipantUuid() {
		assertList(courseApplicationDao.getByCourseParticipantUuid(COURSE_PARTICIPANT_UUID), 1, item.getUuid());
	}

	@Test
	public void testGetByUuid() {
		CourseApplication item = courseApplicationDao.getByUuid(ITEM_UUID, false);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(YEAR_FROM == item.getYearFrom());
		Assert.assertTrue(YEAR_TO == item.getYearTo());
	}

	@Test
	public void testUpdate() {
		item.setYearFrom(2017);
		item.setYearTo(2018);

		courseApplicationDao.update(item);
		CourseApplication item = courseApplicationDao.getByUuid(ITEM_UUID, false);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(2017 == item.getYearFrom());
		Assert.assertTrue(2018 == item.getYearTo());
	}

	@Test
	public void testUpdatePayed() {
		item.setPayed(true);

		courseApplicationDao.update(item);
		CourseApplication item = courseApplicationDao.getByUuid(ITEM_UUID, false);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(item.isPayed());
	}

	@Test
	public void testDelete() {
		courseApplicationDao.delete(item);
		CourseApplication item = courseApplicationDao.getByUuid(ITEM_UUID, false);
		Assert.assertNull(item);
	}
}
