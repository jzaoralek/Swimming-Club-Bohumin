package com.jzaoralek.scb.dataservice.service;

import java.util.Calendar;

import bank.fioclient.dto.AccountStatement;

public interface BankPaymentService {

	public AccountStatement transactions(Calendar datumOd, Calendar datumDo);
}
