package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.LearningLessonDao;
import com.jzaoralek.scb.dataservice.dao.LessonDao;
import com.jzaoralek.scb.dataservice.domain.LearningLesson;
import com.jzaoralek.scb.dataservice.domain.Lesson;

public class LearningLessonDaoTest extends BaseTestCase {

	@Autowired
	private LearningLessonDao learningLessonDao;

	@Autowired
	private LessonDao lessonDao;

	private LearningLesson item;
	private Lesson lessonItem;

	@Before
	public void setUp() {
		item = new LearningLesson();
		fillIdentEntity(item);
		item.setTimeFrom(TIME_FROM);
		item.setLessonDate(Calendar.getInstance().getTime());
		item.setTimeTo(TIME_TO);
		item.setDescription(DESCRIPTION);
		lessonItem = buildLesson();
		item.setLesson(lessonItem);
		item.setParticipantList(Arrays.asList(buildCourseParticipant()));

		lessonDao.insert(lessonItem);
		learningLessonDao.insert(item);
	}

	@Test
	public void testGetByLesson() {
		assertList(learningLessonDao.getByLesson(lessonItem.getUuid()), 1, lessonItem.getUuid());
	}

	@Test
	public void testUpdate() {
		List<LearningLesson> list = learningLessonDao.getByLesson(lessonItem.getUuid());
		LearningLesson itemDB = learningLessonDao.getByUUID(list.get(0).getUuid());

		String descriptionUpdated = "descriptionUpdated";
		itemDB.setDescription(descriptionUpdated);
		itemDB.setParticipantList(null);

		learningLessonDao.update(itemDB);

		list = learningLessonDao.getByLesson(lessonItem.getUuid());
		itemDB = list.get(0);
		Assert.assertTrue(descriptionUpdated.equals(itemDB.getDescription()));
		Assert.assertTrue(CollectionUtils.isEmpty(itemDB.getParticipantList()));
	}

	@Test
	public void testDelete() {
		learningLessonDao.delete(item);
		assertList(learningLessonDao.getByLesson(lessonItem.getUuid()), 0, null);
	}
}