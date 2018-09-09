package com.jzaoralek.scb.dataservice.task;

public interface PaymentTask {
	
	/**
	 * Ztazeni novych plateb z banky.
	 */
	void updateBankPayments();
	
	/**
	 * Zparovani plateb s platbami ucastniku.
	 */
	void processPaymentPairing();
}
