package com.jzaoralek.scb.dataservice.service.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.service.BankPaymentService;

import bank.fioclient.dto.AccountStatement;

public class BankPaymentServiceTest extends BaseTestCase {

	@Autowired
	private BankPaymentService bankPaymentService;
	
	@Test
	public void testGetItemListByType() {
		Calendar datumOd = new GregorianCalendar(2017,9,28);
		Calendar datumDo = new GregorianCalendar(2017,10,28);
		AccountStatement transactions = bankPaymentService.transactions(datumOd, datumDo);
		System.out.println(transactions.getTransactions().size());
	}
}