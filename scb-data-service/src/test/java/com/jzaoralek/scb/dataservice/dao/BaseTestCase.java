package com.jzaoralek.scb.dataservice.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.CodeListItem.CodeListType;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.IdentEntity;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;

@ContextConfiguration(locations={"/scb-data-service-test-context.xml"})
public abstract class BaseTestCase extends AbstractTransactionalJUnit4SpringContextTests {

	protected static final UUID ITEM_UUID = UUID.randomUUID();
	protected static final String MODIF_BY = "modifByUsername";
	protected static final Date MODIF_AT = Calendar.getInstance().getTime();

	protected static final String CONTACT_FIRSTNAME = "firstname";
	protected static final String CONTACT_SURNAME = "surname";
	protected static final String CONTACT_STREET = "street";
	protected static final Long CONTACT_LAND_REGISTRY_NO = 1352L;
	protected static final Short CONTACT_HOUSE_NO = 10;
	protected static final String CONTACT_CITY = "city";
	protected static final String CONTACT_ZIP_CODE = "zipCode";
	protected static final String CONTACT_EMAIL1 = "email1";
	protected static final String CONTACT_EMAIL2 = "email2";
	protected static final String CONTACT_PHONE1 = "phone1";
	protected static final String CONTACT_PHONE2 = "phone2";

	protected static final String CODELIST_ITEM_NAME = "itemName";
	protected static final CodeListType CODELIST_ITEM_TYPE = CodeListType.SWIMMING_STYLE;

	protected static final Date PARTIC_BIRTHDATE = Calendar.getInstance().getTime();
	protected static final String PARTIC_HEALTH_INFO = "healthInfo";
	protected static final String PARTIC_PERSONAL_NO = "9825366669";

	protected static final int YEAR_FROM = 2016;
	protected static final int YEAR_TO = 2017;

	protected static final String USERNAME = "username";
	protected static final String PASSWORD = "password";
	protected static final boolean PASSWORD_GENERATED = true;
	protected static final ScbUserRole ROLE = ScbUserRole.ADMIN;


	protected void fillIdentEntity(IdentEntity identEntity) {
		if (identEntity == null) {
			return;
		}

		if (identEntity != null && identEntity.getUuid() == null) {
			identEntity.setUuid(ITEM_UUID);
		}

		identEntity.setModifAt(MODIF_AT);
		identEntity.setModifBy(MODIF_BY);
	}

	protected void fillIdentEntityUuid(IdentEntity identEntity, UUID uuid) {
		if (identEntity == null) {
			return;
		}

		if (identEntity != null && identEntity.getUuid() == null) {
			identEntity.setUuid(uuid);
		}

		identEntity.setModifAt(MODIF_AT);
		identEntity.setModifBy(MODIF_BY);
	}

	protected void assertList(List<? extends IdentEntity> list, int expectedSize, UUID expectedUuid) {
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == expectedSize);
		if (!CollectionUtils.isEmpty(list)) {
			IdentEntity item = list.get(0);
			Assert.assertTrue(expectedUuid.toString().equals(item.getUuid().toString()));
		}
	}

	protected Contact buildContact() {
		Contact ret = new Contact();
		fillIdentEntity(ret);
		ret.setFirstname(CONTACT_FIRSTNAME);
		ret.setSurname(CONTACT_SURNAME);
		ret.setStreet(CONTACT_STREET);
		ret.setLandRegistryNumber(CONTACT_LAND_REGISTRY_NO);
		ret.setHouseNumber(CONTACT_HOUSE_NO);
		ret.setCity(CONTACT_CITY);
		ret.setZipCode(CONTACT_ZIP_CODE);
		ret.setEmail1(CONTACT_EMAIL1);
		ret.setEmail2(CONTACT_EMAIL2);
		ret.setPhone1(CONTACT_PHONE1);
		ret.setPhone2(CONTACT_PHONE2);

		return ret;
	}

	protected Contact buildContactGeneratedUuid() {
		Contact ret = new Contact();
		fillIdentEntityUuid(ret, UUID.randomUUID());
		ret.setFirstname(CONTACT_FIRSTNAME);
		ret.setSurname(CONTACT_SURNAME);
		ret.setStreet(CONTACT_STREET);
		ret.setLandRegistryNumber(CONTACT_LAND_REGISTRY_NO);
		ret.setHouseNumber(CONTACT_HOUSE_NO);
		ret.setCity(CONTACT_CITY);
		ret.setZipCode(CONTACT_ZIP_CODE);
		ret.setEmail1(CONTACT_EMAIL1);
		ret.setEmail2(CONTACT_EMAIL2);
		ret.setPhone1(CONTACT_PHONE1);
		ret.setPhone2(CONTACT_PHONE2);

		return ret;
	}

	protected CourseParticipant buildCourseParticipantUUIDGenerated() {
		CourseParticipant ret = new CourseParticipant();
		fillIdentEntity(ret);
		ret.setBirthdate(PARTIC_BIRTHDATE);
		ret.setContact(buildContactGeneratedUuid());
		ret.setHealthInfo(PARTIC_HEALTH_INFO);
		ret.setPersonalNo(PARTIC_PERSONAL_NO);
		return ret;
	}

	protected CourseParticipant buildCourseParticipant() {
		CourseParticipant ret = new CourseParticipant();
		fillIdentEntity(ret);
		ret.setBirthdate(PARTIC_BIRTHDATE);
		ret.setContact(buildContact());
		ret.setHealthInfo(PARTIC_HEALTH_INFO);
		ret.setPersonalNo(PARTIC_PERSONAL_NO);
		return ret;
	}

	protected CodeListItem buildCodelistItem() {
		CodeListItem ret = new CodeListItem();
		fillIdentEntity(ret);
		ret.setName(CODELIST_ITEM_NAME);
		ret.setType(CODELIST_ITEM_TYPE);

		return ret;
	}

	protected ScbUser buildScbUser() {
		ScbUser ret = new ScbUser();
		fillIdentEntity(ret);
		ret.setUsername(USERNAME);
		ret.setPassword(PASSWORD);
		ret.setPasswordGenerated(PASSWORD_GENERATED);
		ret.setRole(ROLE);
		ret.setContact(buildContact());

		return ret;
	}
}
