package com.jzaoralek.scb.ui.pages.payments.vm;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.service.BankPaymentService;

import bank.fioclient.dto.AccountStatement;

public class BankPaymentsVM {

	private static final Logger LOG = LoggerFactory.getLogger(BankPaymentsVM.class);

	@WireVariable
	private BankPaymentService bankPaymentService;
	
	@Command
	public void loadPaymentsCmd() {
		Calendar datumOd = new GregorianCalendar(2017,9,28);
		Calendar datumDo = new GregorianCalendar(2017,10,28);
		AccountStatement transactions = bankPaymentService.transactions(datumOd, datumDo);
		LOG.info(transactions.toString());
	}
}
