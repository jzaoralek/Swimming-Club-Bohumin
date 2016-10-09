package com.jzaoralek.scb.dataservice.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.jzaoralek.scb.dataservice.domain.IdentEntity;

@ContextConfiguration(locations={"/scb-data-service-test-context.xml"})
public abstract class BaseTestCase extends AbstractTransactionalJUnit4SpringContextTests {

	protected static final UUID ITEM_UUID = UUID.randomUUID();
	protected static final String MODIF_BY = "modifByUsername";
	protected static final Date MODIF_AT = Calendar.getInstance().getTime();

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

	protected void assertList(List<? extends IdentEntity> list, int expectedSize, UUID expectedUuid) {
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == expectedSize);
		IdentEntity item = list.get(0);
		Assert.assertTrue(expectedUuid.toString().equals(item.getUuid().toString()));
	}
}
