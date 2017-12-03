package com.jzaoralek.scb.dataservice.service.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.service.BankPaymentService;

import bank.fioclient.dto.AccountStatement;
import bank.fioclient.dto.AuthToken;
import bank.fioclient.exception.FioClientException;

@Service("bankPaymentService")
public class BankPaymentServiceImpl implements BankPaymentService {

	@Autowired
	private FioService fioService;
	
	@Override
	public AccountStatement transactions(Calendar datumOd, Calendar datumDo) {		
		try {
			AuthToken token = new AuthToken("895qPPEdiFhbGpfqPq3Srp57FJjIixZjvbCRnyeCWRzhH9aZ51tnUxv9CXJ5yr2U");
			return fioService.transactions(token, datumOd, datumDo);
		} catch (FioClientException e) {
			e.printStackTrace();
			return null;
		}		
	}
}