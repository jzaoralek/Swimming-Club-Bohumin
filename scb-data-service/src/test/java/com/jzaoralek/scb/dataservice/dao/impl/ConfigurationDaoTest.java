package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.ConfigurationDao;
import com.jzaoralek.scb.dataservice.domain.Config;

public class ConfigurationDaoTest extends BaseTestCase {

	@Autowired
	private ConfigurationDao configurationDao;

	@Test
	public void testGetByName() {
		Config item = configurationDao.getByName("COURSE_APPLICATION_ALLOWED");
		Assert.assertNotNull(item);
		Assert.assertNotNull(item.getValue());
	}

	@Test
	public void testGetAll() {
		List<Config> itemList = configurationDao.getAll();
		Assert.assertNotNull(itemList);
	}

	@Test
	public void testUpdate() {
		Config item = configurationDao.getByName("COURSE_APPLICATION_ALLOWED");
		String valueNew = "valueNew";
		item.setValue(valueNew);

		configurationDao.update(item);
		Config updatedItem = configurationDao.getByName("COURSE_APPLICATION_ALLOWED");
		Assert.assertTrue(valueNew.equals(updatedItem.getValue()));
	}
}