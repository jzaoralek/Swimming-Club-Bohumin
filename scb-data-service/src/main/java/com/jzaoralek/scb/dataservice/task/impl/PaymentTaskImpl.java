package com.jzaoralek.scb.dataservice.task.impl;

import java.util.GregorianCalendar;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jzaoralek.scb.dataservice.common.DataServiceConstants;
import com.jzaoralek.scb.dataservice.datasource.ClientDatabaseContextHolder;
import com.jzaoralek.scb.dataservice.service.BankPaymentService;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.dataservice.service.MailService;
import com.jzaoralek.scb.dataservice.task.PaymentTask;
import com.jzaoralek.scb.dataservice.utils.ExcUtil;
import com.jzaoralek.scb.dataservice.utils.PaymentUtils;

@Component("paymentTask")
public class PaymentTaskImpl implements PaymentTask {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private static final String PROCESS_PAYMENT_SUCCESS_MAIL_SUBJECT = "Platby [ automaticke zpracovani ] - USPESNE DOKONCENO";
	private static final String PROCESS_PAYMENT_ERROR_MAIL_SUBJECT = "Platby [ automaticke zpracovani ] - CHYBA!";
	private static final String PAIRING_PAYMENT_SUCCESS_MAIL_SUBJECT = "Platby [ automaticke parovani ] - USPESNE DOKONCENO";
	private static final String PAIRING_PAYMENT_ERROR_MAIL_SUBJECT = "Platby [ automaticke parovani ] - CHYBA!";
	
	@Autowired
	protected ConfigurationService configurationService;
	
	@Autowired
	private BankPaymentService bankPaymentService;

	@Autowired
	private MailService mailService;
	
	@Override
	public void updateBankPayments() {
		final String customerDBCtx = ClientDatabaseContextHolder.getClientDatabase();
		try {
			// kontrola zda-li povolen modul platby
			if (!configurationService.isPaymentsAvailable()) {
				return;
			}
			Pair<GregorianCalendar, GregorianCalendar> dateFromTo = PaymentUtils.getDefaultDateFromTo(configurationService);
			int newPaymentCount = bankPaymentService.updateBankPayments(dateFromTo.getValue0(), dateFromTo.getValue1());
			// notification email about success processing
			mailService.sendMail(DataServiceConstants.PLATBY_EMAIL, null, PROCESS_PAYMENT_SUCCESS_MAIL_SUBJECT, "Zpracovano " + newPaymentCount + " novych plateb.", null, false, false, null, customerDBCtx);
		} catch (Exception e) {
			// notification email about processing with error
			String exceptionStr = ExcUtil.traceMessage(e).toString();
			mailService.sendMail(DataServiceConstants.ADMIN_EMAIL, null, PROCESS_PAYMENT_ERROR_MAIL_SUBJECT, exceptionStr, null, false, false, null, customerDBCtx);
			mailService.sendMail(DataServiceConstants.PLATBY_EMAIL, null, PROCESS_PAYMENT_ERROR_MAIL_SUBJECT, exceptionStr, null, false, false, null, customerDBCtx);
			LOG.error("Unexpected excetion during updating bank payments.", e);
		}
	}

	@Override
	public void processPaymentPairing() {
		final String customerDBCtx = ClientDatabaseContextHolder.getClientDatabase();
		try {
			// kontrola zda-li je povolen modul platby
			if (!configurationService.isPaymentsAvailable()) {
				return;
			}
			Pair<GregorianCalendar, GregorianCalendar> dateFromTo = PaymentUtils.getDefaultDateFromTo(configurationService);
			int processPaymentCount = bankPaymentService.processPaymentPairing(dateFromTo.getValue0(), dateFromTo.getValue1());
			// notification email about success processing
			mailService.sendMail(DataServiceConstants.PLATBY_EMAIL, null, PAIRING_PAYMENT_SUCCESS_MAIL_SUBJECT, "Zparovano " + processPaymentCount + " plateb.", null, false, false, null, customerDBCtx);
		} catch (Exception e) {
			// notification email about processing with error
			String exceptionStr = ExcUtil.traceMessage(e).toString();
			mailService.sendMail(DataServiceConstants.ADMIN_EMAIL, null, PAIRING_PAYMENT_ERROR_MAIL_SUBJECT, exceptionStr, null, false, false, null, customerDBCtx);
			mailService.sendMail(DataServiceConstants.PLATBY_EMAIL, null, PAIRING_PAYMENT_ERROR_MAIL_SUBJECT, exceptionStr, null, false, false, null, customerDBCtx);
			LOG.error("Unexpected excetion during bank payments pairing.", e);
		}
	}
}
