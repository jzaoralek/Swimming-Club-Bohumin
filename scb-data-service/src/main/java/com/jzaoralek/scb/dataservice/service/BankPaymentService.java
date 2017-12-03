package com.jzaoralek.scb.dataservice.service;

import java.util.Calendar;
import java.util.List;

import bank.fioclient.dto.AccountStatement;
import bank.fioclient.dto.Transaction;

public interface BankPaymentService {

	/**
	 * Load bank payments from internet banking client.
	 * @param datumOd
	 * @param datumDo
	 * @return
	 */
	AccountStatement transactions(Calendar datumOd, Calendar datumDo);
	
	/**
	 * Load bank payments from local database.
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	List<Transaction> getByInterval(Calendar dateFrom, Calendar dateTo);
}
