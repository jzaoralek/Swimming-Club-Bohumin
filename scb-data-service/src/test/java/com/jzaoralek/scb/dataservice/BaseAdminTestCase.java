package com.jzaoralek.scb.dataservice;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.domain.IdentEntity;

@ContextConfiguration(locations={"/scb-data-service-test-admin_context.xml"})
public abstract class BaseAdminTestCase extends AbstractTransactionalJUnit4SpringContextTests {

	protected static final UUID ITEM_UUID = UUID.randomUUID();
	
	protected void assertList(List<? extends IdentEntity> list, int expectedSize, UUID expectedUuid) {
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == expectedSize);
		if (!CollectionUtils.isEmpty(list)) {
			IdentEntity item = list.get(0);
			Assert.assertTrue(expectedUuid.toString().equals(item.getUuid().toString()));
		}
	}
}
