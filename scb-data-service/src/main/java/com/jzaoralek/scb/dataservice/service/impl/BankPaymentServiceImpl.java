package com.jzaoralek.scb.dataservice.service.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.service.BankPaymentService;

import bank.fioclient.dto.AccountStatement;
import bank.fioclient.dto.AuthToken;
import bank.fioclient.exception.FioClientException;

@Service("bankPaymentService")
public class BankPaymentServiceImpl implements BankPaymentService {

	@Value("${auth.token}")
    private String authToken;
	
	@Autowired
	private FioService fioService;
	
	@Override
	public AccountStatement transactions(Calendar datumOd, Calendar datumDo) {		
		try {
			return fioService.transactions(new AuthToken(authToken), datumOd, datumDo);
		} catch (FioClientException e) {
			e.printStackTrace();
			return null;
		}		
	}
}