package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.CourseApplicationFileConfigDao;
import com.jzaoralek.scb.dataservice.domain.Attachment;

public class CourseApplicationFileConfigDaoTest extends BaseTestCase {

	@Autowired
	private CourseApplicationFileConfigDao courseApplicationFileConfigDao;
	
	@Test
	public void testGetByUuid() {
		Attachment item = courseApplicationFileConfigDao.getFileByUuid(UUID.fromString("fd33a4d4-7e99-11e6-ae22-56b6b6499628"));
//		Assert.assertNotNull(item);
	}
}
