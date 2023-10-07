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
	
	/**
	 * Load bank payments from local database that hasn't been processed, it means paired to payment.
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	List<Transaction> getNotInPaymentByInterval(Calendar dateFrom, Calendar dateTo);
	
	/**
	 * Load bank payments from internet banking client for prev 60 days and store to local database.
	 * @return count of processed bank payments
	 */
	int updateBankPayments();
	
	/**
	 * Pairing of bank payments from local database to course participant in current year by personal number as variable symbol.
	 * @param dateFrom
	 * @param dateTo
	 * @return count of paired payments
	 */
	int processPaymentPairing(Calendar dateFrom, Calendar dateTo);
}
