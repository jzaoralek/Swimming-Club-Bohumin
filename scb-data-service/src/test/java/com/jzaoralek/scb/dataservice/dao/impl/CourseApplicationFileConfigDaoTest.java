package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.CourseApplicationFileConfigDao;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;

public class CourseApplicationFileConfigDaoTest extends BaseTestCase {

	@Autowired
	private CourseApplicationFileConfigDao courseApplicationFileConfigDao;
	
	private CourseApplicationFileConfig item;
	
	@Before
	public void setUp() {
		item = new CourseApplicationFileConfig();
		fillIdentEntity(item);
//		courseApplicationDao.insert(item);
	}

	@After
    public void tearDown() {
    }
	
	@Test
	public void testGetListForPage() {
		courseApplicationFileConfigDao.getListForPage();
	}
	
	@Test
	public void testGetListForEmail() {
		courseApplicationFileConfigDao.getListForEmail();
	}
	
	@Test
	public void testGetByUuid() {
		courseApplicationFileConfigDao.getFileByUuid(UUID.fromString("fd33a4d4-7e99-11e6-ae22-56b6b6499628"));
//		Assert.assertNotNull(item);
	}
}
