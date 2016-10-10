package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.dao.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.CodeListDao;
import com.jzaoralek.scb.dataservice.domain.CodeListItem;

public class CodeListDaoTest extends BaseTestCase {

	@Autowired
	private CodeListDao codeListDao;

	private CodeListItem item;

	@Before
	public void setUp() {
		item = buildCodelistItem();
		codeListDao.insert(item);
	}

	@Test
	public void testGetItemListByType() {
		List<CodeListItem> codelistItemList = codeListDao.getItemListByType(CODELIST_ITEM_TYPE);
		assertList(codelistItemList, 1, ITEM_UUID);
	}

	@Test
	public void testGetByTypeAndName() {
		CodeListItem item = codeListDao.getByTypeAndName(CODELIST_ITEM_TYPE, CODELIST_ITEM_NAME);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
	}

	@Test
	public void testInsertAndGetByUuid() {
		CodeListItem item = codeListDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		Assert.assertTrue(CODELIST_ITEM_NAME.equals(item.getName()));
		Assert.assertTrue(MODIF_BY.equals(item.getModifBy()));
	}

	@Test
	public void testUpdate() {
		String nameNew = "itemNameNew";
		item.setName(nameNew);
		codeListDao.update(item);
		CodeListItem item = codeListDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(nameNew.equals(item.getName()));
	}

	@Test
	public void testDelete() {
		codeListDao.delete(item);
		CodeListItem item = codeListDao.getByUuid(ITEM_UUID);
		Assert.assertNull(item);
	}
}
