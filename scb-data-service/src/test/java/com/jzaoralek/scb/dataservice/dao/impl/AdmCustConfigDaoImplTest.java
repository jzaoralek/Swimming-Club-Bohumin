package com.jzaoralek.scb.dataservice.dao.impl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseAdminTestCase;
import com.jzaoralek.scb.dataservice.dao.AdmCustConfigDao;
import com.jzaoralek.scb.dataservice.domain.CustomerConfig;

public class AdmCustConfigDaoImplTest extends BaseAdminTestCase {

	@Autowired
	private AdmCustConfigDao admCustConfigDao;
	
	@Test
	public void testGetAll() {
		assertList(admCustConfigDao.getAll(), 0, ITEM_UUID);
	}

	@Test
	public void testGetByUuid() {
		CustomerConfig item = admCustConfigDao.getByUuid(ITEM_UUID);
//		Assert.assertNotNull(item);
//		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
//		Assert.assertTrue(USERNAME.equals(item.getUsername()));
//		Assert.assertTrue(PASSWORD.equals(item.getPassword()));
//		Assert.assertTrue(PASSWORD_GENERATED == item.isPasswordGenerated());
//		Assert.assertTrue(ROLE == item.getRole());
	}

	@Test
	public void testGetByUsername() {
		CustomerConfig item = admCustConfigDao.getByCustId("kosatky");
//		Assert.assertNotNull(item);
//		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
//		Assert.assertTrue(USERNAME.equals(item.getUsername()));
//		Assert.assertTrue(PASSWORD.equals(item.getPassword()));
//		Assert.assertTrue(PASSWORD_GENERATED == item.isPasswordGenerated());
//		Assert.assertTrue(ROLE == item.getRole());
	}
}
