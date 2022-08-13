package com.jzaoralek.scb.dataservice.task.impl;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jzaoralek.scb.dataservice.common.DataServiceConstants;
import com.jzaoralek.scb.dataservice.dao.AdmCustConfigDao;
import com.jzaoralek.scb.dataservice.datasource.ClientDatabaseContextHolder;
import com.sportologic.common.model.domain.CustomerConfig;
import com.jzaoralek.scb.dataservice.service.MailService;
import com.jzaoralek.scb.dataservice.task.MailSendTask;
import com.jzaoralek.scb.dataservice.utils.ExcUtil;

@Component("mailSendTask")
public class MailSendTaskImpl implements MailSendTask {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private static final String DELETING_HIST_SEND_EMAIL_ERROR_MAIL_SUBJECT = "Odstranovani historickych odeslanych mailu - CHYBA!";
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private AdmCustConfigDao admCustConfigDao;
	
	/**
	 * Delete email messages older then 90 days.
	 */
	@Override
	public void deleteHistMailSendList() {
		List<CustomerConfig> custCfgs = admCustConfigDao.getAll();
		String customerDBCtx = null;
		for (CustomerConfig custCfg : custCfgs) {
			ClientDatabaseContextHolder.set(custCfg.getCustId());
			customerDBCtx = ClientDatabaseContextHolder.getClientDatabase();
			try {
				LOG.info("Deleting of historic mails started for customer: {}", customerDBCtx);
				Calendar toCal = Calendar.getInstance();
				toCal.add(Calendar.DATE, -90);
				mailService.deleteSendMailToDate(toCal.getTime());
			} catch (Exception e) {
				// notification email about processing with error
				String exceptionStr = ExcUtil.traceMessage(e).toString();
				mailService.sendMail(DataServiceConstants.ADMIN_EMAIL, null, DELETING_HIST_SEND_EMAIL_ERROR_MAIL_SUBJECT, exceptionStr, null, false, false, null, customerDBCtx);
				LOG.error("Unexpected excetion during deleting old send emails.", e);
			}
		}
		
	}
}