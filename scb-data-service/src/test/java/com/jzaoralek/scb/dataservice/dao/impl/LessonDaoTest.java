package com.jzaoralek.scb.dataservice.dao.impl;

import java.sql.Time;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.dao.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.LessonDao;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.domain.Lesson.DayOfWeek;

public class LessonDaoTest extends BaseTestCase {

	private static final Time TIME_FROM = new Time(1000);
	private static final Time TIME_TO = new Time(1050);
	private static final DayOfWeek DAY_OF_WEEK = DayOfWeek.MONDAY;
	private static final UUID COURSE_UUID = UUID.randomUUID();

	@Autowired
	private LessonDao lessonDao;

	private Lesson item;

	@Before
	public void setUp() {
		item = new Lesson();
		fillIdentEntity(item);
		item.setTimeFrom(TIME_FROM);
		item.setTimeTo(TIME_TO);
		item.setDayOfWeek(DAY_OF_WEEK);
		item.setCourseUuid(COURSE_UUID);
//		item.setCourse();

		lessonDao.insert(item);
	}

	@Test
	public void testGetAll() {
		assertList(lessonDao.getAll(), 1, ITEM_UUID);
	}

	@Test
	public void testgetByCourse() {
		assertList(lessonDao.getByCourse(COURSE_UUID), 1, ITEM_UUID);
	}

	@Test
	public void testgetByCourseAndDayOfWeek() {
		assertList(lessonDao.getByCourseAndDayOfWeek(COURSE_UUID, DAY_OF_WEEK), 1, ITEM_UUID);
	}

	@Test
	public void testgetByUuid() {
		Lesson item = lessonDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(COURSE_UUID.toString().equals(item.getCourseUuid().toString()));
		Assert.assertTrue(DAY_OF_WEEK == item.getDayOfWeek());
	}

	@Test
	public void testupdate() {
		UUID newCourseUuid = UUID.randomUUID();
		item.setCourseUuid(newCourseUuid);
		item.setDayOfWeek(DayOfWeek.THURSDAY);

		lessonDao.update(item);

		Lesson item = lessonDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(newCourseUuid.toString().equals(item.getCourseUuid().toString()));
		Assert.assertTrue(DayOfWeek.THURSDAY == item.getDayOfWeek());
	}

	@Test
	public void testdelete() {
		lessonDao.delete(item);
		Assert.assertNull(lessonDao.getByUuid(ITEM_UUID));
	}
}
