package com.jzaoralek.scb.dataservice.dao.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.dao.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.ScbUserDao;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.utils.vo.Cover;

public class ScbUserDaoTest extends BaseTestCase {

	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final boolean PASSWORD_GENERATED = true;
	private static final ScbUserRole ROLE = ScbUserRole.ADMIN;

	@Autowired
	private ScbUserDao scbUserDao;

	private ScbUser item;

	@Before
	public void setUp() {
		item = new ScbUser();
		fillIdentEntity(item);

		item.setUsername(USERNAME);
		item.setPassword(PASSWORD);
		item.setPasswordGenerated(PASSWORD_GENERATED);
		item.setRole(ROLE);
		item.setContact(buildContact());

		scbUserDao.insert(item);
	}

	@Test
	public void testGetAll() {
		assertList(scbUserDao.getAll(), 1, ITEM_UUID);
	}

	@Test
	public void testGetByUuid() {
		ScbUser item = scbUserDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(USERNAME.equals(item.getUsername()));
		Assert.assertTrue(PASSWORD.equals(item.getPassword()));
		Assert.assertTrue(PASSWORD_GENERATED == item.isPasswordGenerated());
		Assert.assertTrue(ROLE == item.getRole());
	}

	@Test
	public void testGetByUsername() {
		ScbUser item = scbUserDao.getByUsername(USERNAME);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(USERNAME.equals(item.getUsername()));
		Assert.assertTrue(PASSWORD.equals(item.getPassword()));
		Assert.assertTrue(PASSWORD_GENERATED == item.isPasswordGenerated());
		Assert.assertTrue(ROLE == item.getRole());
	}

	@Test
	public void testUpdate() {
		String updated = "updated";
		item.setUsername(USERNAME + updated);
		item.setPassword(PASSWORD + updated);
		item.setPasswordGenerated(false);
		item.setRole(ScbUserRole.USER);

		scbUserDao.update(item);
		item = scbUserDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue((USERNAME+updated).equals(item.getUsername()));
		Assert.assertTrue((PASSWORD+updated).equals(item.getPassword()));
		Assert.assertTrue(false == item.isPasswordGenerated());
		Assert.assertTrue(ScbUserRole.USER == item.getRole());
	}

	@Test
	public void testUpdatePassword() {
		String newPassword = "newPassword";
		scbUserDao.updatePassword(item, new Cover<>(newPassword.toCharArray()));

		ScbUser item = scbUserDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(newPassword.equals(item.getPassword()));
	}

	@Test
	public void testDelete() {
		scbUserDao.delete(item);
		ScbUser item = scbUserDao.getByUuid(ITEM_UUID);
		Assert.assertNull(item);
	}

	@Test
	public void testUserWithSameUsernameExists() {
		Assert.assertTrue(scbUserDao.userWithSameUsernameExists(USERNAME));
	}
}
