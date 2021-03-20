package com.jzaoralek.scb.dataservice.task.impl;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jzaoralek.scb.dataservice.common.DataServiceConstants;
import com.jzaoralek.scb.dataservice.service.MailService;
import com.jzaoralek.scb.dataservice.task.MailSendTask;
import com.jzaoralek.scb.dataservice.utils.ExcUtil;

@Component("mailSendTask")
public class MailSendTaskImpl implements MailSendTask {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private static final String DELETING_HIST_SEND_EMAIL_ERROR_MAIL_SUBJECT = "Odstranovani historickych odeslanych mailu - CHYBA!";
	
	@Autowired
	private MailService mailService;
	
	@Override
	public void deleteHistMailSendList() {
		try {
			Calendar toCal = Calendar.getInstance();
			toCal.add(Calendar.DATE, -31);
			mailService.deleteSendMailToDate(toCal.getTime());
		} catch (Exception e) {
			// notification email about processing with error
			String exceptionStr = ExcUtil.traceMessage(e).toString();
			mailService.sendMail(DataServiceConstants.ADMIN_EMAIL, null, DELETING_HIST_SEND_EMAIL_ERROR_MAIL_SUBJECT, exceptionStr, null, false, false, null);
			LOG.error("Unexpected excetion during deleting old send emails.", e);
		}
	}
}