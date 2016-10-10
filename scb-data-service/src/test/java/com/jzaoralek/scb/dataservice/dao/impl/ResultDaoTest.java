package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.dao.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.CodeListDao;
import com.jzaoralek.scb.dataservice.dao.ResultDao;
import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.Result;

public class ResultDaoTest extends BaseTestCase {

	private static final long RESULT_TIME = 100;
	private static final Date RESULT_DATE = Calendar.getInstance().getTime();
	private static final UUID COURSE_PARTICIPANT_UUID = UUID.randomUUID();
	private static final String DESCRIPTION = "description";
	private static final Integer DISTANCE = 200;

	@Autowired
	private ResultDao resultDao;

	@Autowired
	private CodeListDao codeListdao;

	private Result item;
	private CodeListItem codelistItem;

	@Before
	public void setUp() {
		item = new Result();
		fillIdentEntity(item);
		item.setResultTime(RESULT_TIME);
		item.setResultDate(RESULT_DATE);
		codelistItem = buildCodelistItem();
		item.setStyle(codelistItem);
		item.setCourseParticipantUuid(COURSE_PARTICIPANT_UUID);
		item.setDescription(DESCRIPTION);
		item.setDistance(DISTANCE);

		codeListdao.insert(codelistItem);
		resultDao.insert(item);
	}

	@Test
	public void testGetByCourseParticipant() {
		assertList(resultDao.getByCourseParticipant(COURSE_PARTICIPANT_UUID), 1, ITEM_UUID);
	}

	@Test
	public void testGetByCourseParticipantAndStyle() {
		assertList(resultDao.getByCourseParticipantAndStyle(COURSE_PARTICIPANT_UUID, item.getStyle().getUuid()), 1, ITEM_UUID);
	}

	@Test
	public void testgetByUuid() {
		Result item = resultDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(RESULT_TIME == item.getResultTime());
		Assert.assertTrue(codelistItem.getUuid().toString().equals(item.getStyle().getUuid().toString()));
		Assert.assertTrue(COURSE_PARTICIPANT_UUID.toString().equals(item.getCourseParticipantUuid().toString()));
		Assert.assertTrue(DESCRIPTION.equals(item.getDescription()));
		Assert.assertTrue(DISTANCE.intValue() == item.getDistance().intValue());
	}

	@Test
	public void testupdate() {
		item.setResultTime(200L);
		item.setDescription("newDescription");
		item.setDistance(50);

		resultDao.update(item);

		Result item = resultDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(200L == item.getResultTime());
		Assert.assertTrue("newDescription".equals(item.getDescription()));
		Assert.assertTrue(50 == item.getDistance().intValue());
	}

	@Test
	public void testdelete() {
		resultDao.delete(item.getUuid());
		Result item = resultDao.getByUuid(ITEM_UUID);
		Assert.assertNull(item);
	}

	@Test
	public void testdeleteByCourseParticipant() {
		resultDao.deleteByCourseParticipant(item.getCourseParticipantUuid());
		Result item = resultDao.getByUuid(ITEM_UUID);
		Assert.assertNull(item);
	}

	@Test
	public void testStyleUsedInResult() {
		Assert.assertTrue(resultDao.styleUsedInResult(item.getStyle().getUuid()));
	}
}
