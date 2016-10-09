package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.dao.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.CourseApplicationDao;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;


public class CourseApplicationDaoTest extends BaseTestCase {

	@Autowired
	private CourseApplicationDao courseApplicationDao;

	@Before
	public void setUp() {
	}

	@After
    public void tearDown() {
    }

	@Test
	public void testGetAll() {
		List<CourseApplication> courseApplicationList = courseApplicationDao.getAll(2016, 2017);
		Assert.assertNotNull(courseApplicationList);
	}


	@Test
	public void testGetNotInCourse() {

	}

	@Test
	public void testGetInCourse() {

	}

	@Test
	public void testGetAssignedToCourse() {

	}

	@Test
	public void testGetByUuid() {

	}

	@Test
	public void testInsert() {

	}

	@Test
	public void testUpdate() {

	}

	@Test
	public void testDelete() {

	}
}
