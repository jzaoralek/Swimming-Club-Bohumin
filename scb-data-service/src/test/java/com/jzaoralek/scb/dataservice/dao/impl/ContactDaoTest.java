package com.jzaoralek.scb.dataservice.dao.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.dao.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.ContactDao;
import com.jzaoralek.scb.dataservice.domain.Contact;

public class ContactDaoTest extends BaseTestCase {

	private static final String FIRSTNAME = "firstname";
	private static final String SURNAME = "surname";
	private static final String STREET = "street";
	private static final Long LAND_REGISTRY_NO = 1352L;
	private static final Short HOUSE_NO = 10;
	private static final String CITY = "city";
	private static final String ZIP_CODE = "zipCode";
	private static final String EMAIL1 = "email1";
	private static final String EMAIL2 = "email2";
	private static final String PHONE1 = "phone1";
	private static final String PHONE2 = "phone2";

	@Autowired
	private ContactDao contactDao;

	private Contact item;

	@Before
	public void setUp() {
		item = new Contact();
		fillIdentEntity(item);
		item.setFirstname(FIRSTNAME);
		item.setSurname(SURNAME);
		item.setStreet(STREET);
		item.setLandRegistryNumber(LAND_REGISTRY_NO);
		item.setHouseNumber(HOUSE_NO);
		item.setCity(CITY);
		item.setZipCode(ZIP_CODE);
		item.setEmail1(EMAIL1);
		item.setEmail2(EMAIL2);
		item.setPhone1(PHONE1);
		item.setPhone2(PHONE2);

		contactDao.insert(item);
	}

	@Test
	public void testInsertAndGetByUuid() {
		Contact item = contactDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(FIRSTNAME.equals(item.getFirstname()));
		Assert.assertTrue(SURNAME.equals(item.getSurname()));
		Assert.assertTrue(STREET.equals(item.getStreet()));
		Assert.assertTrue(LAND_REGISTRY_NO.longValue() == item.getLandRegistryNumber().longValue());
		Assert.assertTrue(HOUSE_NO == item.getHouseNumber());
		Assert.assertTrue(CITY.equals(item.getCity()));
		Assert.assertTrue(ZIP_CODE.equals(item.getZipCode()));
		Assert.assertTrue(EMAIL1.equals(item.getEmail1()));
		Assert.assertTrue(EMAIL2.equals(item.getEmail2()));
		Assert.assertTrue(PHONE1.equals(item.getPhone1()));
		Assert.assertTrue(PHONE2.equals(item.getPhone2()));
	}

	@Test
	public void update() {
		String UPDATED_POSTFIX = "updated";
		Short HOUSE_NO_UPDATED = 6;
		Long LAND_REGISTRY_NO_UPDATED = 1000L;

		item.setFirstname(FIRSTNAME+UPDATED_POSTFIX);
		item.setSurname(SURNAME+UPDATED_POSTFIX);
		item.setStreet(STREET+UPDATED_POSTFIX);
		item.setLandRegistryNumber(LAND_REGISTRY_NO_UPDATED);
		item.setHouseNumber(HOUSE_NO_UPDATED);
		item.setCity(CITY+UPDATED_POSTFIX);
		item.setZipCode(ZIP_CODE+UPDATED_POSTFIX);
		item.setEmail1(EMAIL1+UPDATED_POSTFIX);
		item.setEmail2(EMAIL2+UPDATED_POSTFIX);
		item.setPhone1(PHONE1+UPDATED_POSTFIX);
		item.setPhone2(PHONE2+UPDATED_POSTFIX);

		contactDao.update(item);
		Contact itemUpdated = contactDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(itemUpdated);
		Assert.assertTrue(ITEM_UUID.toString().equals(itemUpdated.getUuid().toString()));
		Assert.assertTrue((FIRSTNAME+UPDATED_POSTFIX).equals(itemUpdated.getFirstname()));
		Assert.assertTrue((SURNAME+UPDATED_POSTFIX).equals(itemUpdated.getSurname()));
		Assert.assertTrue((STREET+UPDATED_POSTFIX).equals(itemUpdated.getStreet()));
		Assert.assertTrue(LAND_REGISTRY_NO_UPDATED.longValue() == itemUpdated.getLandRegistryNumber().longValue());
		Assert.assertTrue(HOUSE_NO_UPDATED == itemUpdated.getHouseNumber());
		Assert.assertTrue((CITY+UPDATED_POSTFIX).equals(itemUpdated.getCity()));
		Assert.assertTrue((ZIP_CODE+UPDATED_POSTFIX).equals(itemUpdated.getZipCode()));
		Assert.assertTrue((EMAIL1+UPDATED_POSTFIX).equals(itemUpdated.getEmail1()));
		Assert.assertTrue((EMAIL2+UPDATED_POSTFIX).equals(itemUpdated.getEmail2()));
		Assert.assertTrue((PHONE1+UPDATED_POSTFIX).equals(itemUpdated.getPhone1()));
		Assert.assertTrue((PHONE2+UPDATED_POSTFIX).equals(itemUpdated.getPhone2()));
	}

	@Test
	public void delete() {
		contactDao.delete(item);
		Contact item = contactDao.getByUuid(ITEM_UUID);
		Assert.assertNull(item);
	}
}
