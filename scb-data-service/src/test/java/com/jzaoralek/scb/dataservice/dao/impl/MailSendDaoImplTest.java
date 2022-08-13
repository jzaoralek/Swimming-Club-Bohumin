package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.MailSendDao;
import com.jzaoralek.scb.dataservice.domain.MailSend;

@Ignore
public class MailSendDaoImplTest extends BaseTestCase {

	private static final String MAIL_TO = "mailTo";
	private static final String MAIL_CC = "mailCc";
	private static final String MAIL_SUBJECT = "mailSubject";
	private static final String MAIL_TEXT = "mailText";
	private static final boolean SUCCESS = true;
	private static final boolean ATTACHMENTS = false;
	private static final boolean HTML = false;
	private static final String MAIL_TO_COMPLETE_NAME = "mailToCompleteName";
	
	@Autowired
	private MailSendDao mailSendDao;
	
	private MailSend item;
	
	@Before
	public void setUp() {
		item = new MailSend(MAIL_TO, MAIL_CC, MAIL_SUBJECT, MAIL_TEXT, null, MAIL_TO_COMPLETE_NAME);
		fillIdentEntity(item);
		item.setSuccess(SUCCESS);
		item.setAttachments(ATTACHMENTS);
		item.setHtml(HTML);

		mailSendDao.insert(item);
	}
	
	@Test
	public void testGetByUuid() {
		MailSend item = mailSendDao.getByUuid(ITEM_UUID);
		Assert.assertNotNull(item);
		Assert.assertTrue(ITEM_UUID.toString().equals(item.getUuid().toString()));
		
		Assert.assertTrue(MAIL_TO.equals(item.getTo()));
		Assert.assertTrue(MAIL_CC.equals(item.getCc()));
		Assert.assertTrue(MAIL_SUBJECT.equals(item.getSubject()));
		Assert.assertTrue(MAIL_TEXT.equals(item.getText()));
		Assert.assertTrue(SUCCESS == item.isSuccess());
		Assert.assertTrue(ATTACHMENTS == item.isAttachments());
		Assert.assertTrue(HTML == item.isHtml());
		Assert.assertTrue(MAIL_TO_COMPLETE_NAME.equals(item.getToCompleteName()));
	}
	
	@Test
	public void testGetBankPaymentByDateInterval() {
		assertList(mailSendDao.getMailSendListByCriteria(getYesterday(), getTomorrow(), MAIL_TO, MAIL_SUBJECT, MAIL_TEXT), 1 , ITEM_UUID);
	}
	
	@Test
	public void testDelete() {
		mailSendDao.delete(Arrays.asList(item));
		List<MailSend> list = mailSendDao.getMailSendListByCriteria(getYesterday(), getTomorrow(), MAIL_TO, MAIL_SUBJECT, MAIL_TEXT);
		
		Assert.assertTrue(list.isEmpty());
	}
	
	@Test
	public void testDeleteSendMailToDate() {
		mailSendDao.deleteToDate(Calendar.getInstance().getTime());
		List<MailSend> list = mailSendDao.getMailSendListByCriteria(getYesterday(), getTomorrow(), MAIL_TO, MAIL_SUBJECT, MAIL_TEXT);
		
		Assert.assertTrue(list.isEmpty());
	}
}
