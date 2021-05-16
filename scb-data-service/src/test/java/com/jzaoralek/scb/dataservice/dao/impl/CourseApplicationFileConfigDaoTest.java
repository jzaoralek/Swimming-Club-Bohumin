package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.CourseApplicationFileConfigDao;
import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig.CourseApplicationFileType;

public class CourseApplicationFileConfigDaoTest extends BaseTestCase {

	private static final boolean EMAIL_ATTACHMENT = true;
	private static final boolean PAGE_ATTACHMENT = false;
	private static final boolean PAGE_TEXT = true;
	private static final CourseApplicationFileType TYPE = CourseApplicationFileType.OTHER;
	private static final UUID ATTACHMENT_UUID = UUID.randomUUID();
	private static final String ATTACHMENT_CONTENT_TYPE = "application/file";
	private static final String ATTACHMENT_NAME = "fileName";
	
	@Autowired
	private CourseApplicationFileConfigDao courseApplicationFileConfigDao;
	
	private CourseApplicationFileConfig item;
	
	@Before
	public void setUp() {
		item = new CourseApplicationFileConfig();
		fillIdentEntity(item);
		item.setDescription(DESCRIPTION);
		item.setEmailAttachment(EMAIL_ATTACHMENT);
		item.setPageAttachment(PAGE_ATTACHMENT);
		item.setPageText(PAGE_TEXT);
		item.setType(TYPE);
		
		Attachment attachment = new Attachment();
		attachment.setUuid(ATTACHMENT_UUID);
		attachment.setByteArray(null);
		attachment.setContentType(ATTACHMENT_CONTENT_TYPE);
		attachment.setDescription(DESCRIPTION);
		attachment.setName(ATTACHMENT_NAME);
		
		item.setAttachment(attachment);
		item.setAttachmentUuid(attachment.getUuid());
		
		courseApplicationFileConfigDao.insert(item);
	}

	@After
    public void tearDown() {
    }
	
	@Test
	public void testGetListForPage() {
		List<CourseApplicationFileConfig> list = courseApplicationFileConfigDao.getListForPage();
		Assert.assertNotNull(list);
		Assert.assertTrue(!list.isEmpty());
	}
	
	@Test
	public void testGetListForEmail() {
		List<CourseApplicationFileConfig> list = courseApplicationFileConfigDao.getListForEmail();
		Assert.assertNotNull(list);
		Assert.assertTrue(!list.isEmpty());
	}
	
	@Test
	public void testGetFileByUuid() {
		Attachment file = courseApplicationFileConfigDao.getFileByUuid(ATTACHMENT_UUID);
		Assert.assertNotNull(file);
		assertFileValues(file);
	}
	
	@Test
	public void getAll() {
		List<CourseApplicationFileConfig> list = courseApplicationFileConfigDao.getAll();
		Assert.assertNotNull(list);
		Assert.assertTrue(!list.isEmpty());
	}
	
	@Test
	public void testGetByUuid() {
		CourseApplicationFileConfig config = courseApplicationFileConfigDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(config);
		
		Assert.assertTrue(config.getDescription().equals(DESCRIPTION));
		Assert.assertTrue(config.isEmailAttachment() == EMAIL_ATTACHMENT);		
		Assert.assertTrue(config.isPageAttachment() == PAGE_ATTACHMENT);
		Assert.assertTrue(config.isPageText() == PAGE_TEXT);
		Assert.assertTrue(config.getType() == TYPE);

		Assert.assertNotNull(config.getAttachment());
		assertFileValues(config.getAttachment());
	}
	
	@Test
	public void insert() {
		List<CourseApplicationFileConfig> list = courseApplicationFileConfigDao.getAll();
		Assert.assertNotNull(list);
	}
	
	@Test
	public void update() {
		CourseApplicationFileConfig config = courseApplicationFileConfigDao.getByUuid(ITEM_UUID);
		// update config values
		config.setDescription("descChanged");
		config.setEmailAttachment(false);
		config.setPageAttachment(true);
		config.setPageText(false);
		config.setType(CourseApplicationFileType.HEALTH_INFO);
		// update attachment values
		config.getAttachment().setByteArray(null);
		config.getAttachment().setContentType("contentType");
		config.getAttachment().setDescription("attachmentDescriptionChanged");
		config.getAttachment().setName("attachmentNameChanged");
		
		courseApplicationFileConfigDao.update(config);
		config = courseApplicationFileConfigDao.getByUuid(ITEM_UUID);
		
		// check config
		Assert.assertNotNull(config);
		Assert.assertTrue(config.getDescription().equals("descChanged"));
		Assert.assertTrue(config.isEmailAttachment() == false);		
		Assert.assertTrue(config.isPageAttachment() == true);
		Assert.assertTrue(config.isPageText() == false);
		Assert.assertTrue(config.getType() == CourseApplicationFileType.HEALTH_INFO);

		// check attachment
		Assert.assertNotNull(config.getAttachment());
		Assert.assertTrue(config.getAttachment().getUuid().toString().equals(ATTACHMENT_UUID.toString()));
		Assert.assertTrue(config.getAttachment().getByteArray() == null);
		Assert.assertTrue(config.getAttachment().getContentType().equals("contentType"));
		Assert.assertTrue(config.getAttachment().getDescription().equals("attachmentDescriptionChanged"));
		Assert.assertTrue(config.getAttachment().getName().equals("attachmentNameChanged"));
	}
	
	@Test
	public void delete() {
		courseApplicationFileConfigDao.delete(item);
		List<CourseApplicationFileConfig> list = courseApplicationFileConfigDao.getAll();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.isEmpty());
	}
	
	private void assertFileValues(Attachment file) {
		Assert.assertTrue(file.getUuid().toString().equals(ATTACHMENT_UUID.toString()));
		Assert.assertTrue(file.getByteArray() == null);
		Assert.assertTrue(file.getContentType().equals(ATTACHMENT_CONTENT_TYPE));
		Assert.assertTrue(file.getDescription().equals(DESCRIPTION));
		Assert.assertTrue(file.getName().equals(ATTACHMENT_NAME));
	}
}
