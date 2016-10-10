package com.jzaoralek.scb.dataservice.dao.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.dao.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.ContactDao;
import com.jzaoralek.scb.dataservice.domain.Contact;

public class ContactDaoTest extends BaseTestCase {

	@Autowired
	private ContactDao contactDao;

	private Contact item;

	@Before
	public void setUp() {
		item = buildContact();
		contactDao.insert(item);
	}

	@Test
	public void testInsertAndGetByUuid() {
		Contact item = contactDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(CONTACT_FIRSTNAME.equals(item.getFirstname()));
		Assert.assertTrue(CONTACT_SURNAME.equals(item.getSurname()));
		Assert.assertTrue(CONTACT_STREET.equals(item.getStreet()));
		Assert.assertTrue(CONTACT_LAND_REGISTRY_NO.longValue() == item.getLandRegistryNumber().longValue());
		Assert.assertTrue(CONTACT_HOUSE_NO == item.getHouseNumber());
		Assert.assertTrue(CONTACT_CITY.equals(item.getCity()));
		Assert.assertTrue(CONTACT_ZIP_CODE.equals(item.getZipCode()));
		Assert.assertTrue(CONTACT_EMAIL1.equals(item.getEmail1()));
		Assert.assertTrue(CONTACT_EMAIL2.equals(item.getEmail2()));
		Assert.assertTrue(CONTACT_PHONE1.equals(item.getPhone1()));
		Assert.assertTrue(CONTACT_PHONE2.equals(item.getPhone2()));
	}

	@Test
	public void update() {
		String UPDATED_POSTFIX = "updated";
		Short HOUSE_NO_UPDATED = 6;
		Long LAND_REGISTRY_NO_UPDATED = 1000L;

		item.setFirstname(CONTACT_FIRSTNAME+UPDATED_POSTFIX);
		item.setSurname(CONTACT_SURNAME+UPDATED_POSTFIX);
		item.setStreet(CONTACT_STREET+UPDATED_POSTFIX);
		item.setLandRegistryNumber(LAND_REGISTRY_NO_UPDATED);
		item.setHouseNumber(HOUSE_NO_UPDATED);
		item.setCity(CONTACT_CITY+UPDATED_POSTFIX);
		item.setZipCode(CONTACT_ZIP_CODE+UPDATED_POSTFIX);
		item.setEmail1(CONTACT_EMAIL1+UPDATED_POSTFIX);
		item.setEmail2(CONTACT_EMAIL2+UPDATED_POSTFIX);
		item.setPhone1(CONTACT_PHONE1+UPDATED_POSTFIX);
		item.setPhone2(CONTACT_PHONE2+UPDATED_POSTFIX);

		contactDao.update(item);
		Contact itemUpdated = contactDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(itemUpdated);
		Assert.assertTrue(ITEM_UUID.toString().equals(itemUpdated.getUuid().toString()));
		Assert.assertTrue((CONTACT_FIRSTNAME+UPDATED_POSTFIX).equals(itemUpdated.getFirstname()));
		Assert.assertTrue((CONTACT_SURNAME+UPDATED_POSTFIX).equals(itemUpdated.getSurname()));
		Assert.assertTrue((CONTACT_STREET+UPDATED_POSTFIX).equals(itemUpdated.getStreet()));
		Assert.assertTrue(LAND_REGISTRY_NO_UPDATED.longValue() == itemUpdated.getLandRegistryNumber().longValue());
		Assert.assertTrue(HOUSE_NO_UPDATED == itemUpdated.getHouseNumber());
		Assert.assertTrue((CONTACT_CITY+UPDATED_POSTFIX).equals(itemUpdated.getCity()));
		Assert.assertTrue((CONTACT_ZIP_CODE+UPDATED_POSTFIX).equals(itemUpdated.getZipCode()));
		Assert.assertTrue((CONTACT_EMAIL1+UPDATED_POSTFIX).equals(itemUpdated.getEmail1()));
		Assert.assertTrue((CONTACT_EMAIL2+UPDATED_POSTFIX).equals(itemUpdated.getEmail2()));
		Assert.assertTrue((CONTACT_PHONE1+UPDATED_POSTFIX).equals(itemUpdated.getPhone1()));
		Assert.assertTrue((CONTACT_PHONE2+UPDATED_POSTFIX).equals(itemUpdated.getPhone2()));
	}

	@Test
	public void delete() {
		contactDao.delete(item);
		Contact item = contactDao.getByUuid(ITEM_UUID);
		Assert.assertNull(item);
	}
}
