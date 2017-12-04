package com.jzaoralek.scb.dataservice.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.dao.TransactionDao;
import com.jzaoralek.scb.dataservice.service.BankPaymentService;

import bank.fioclient.dto.AccountStatement;
import bank.fioclient.dto.AuthToken;
import bank.fioclient.dto.Transaction;
import bank.fioclient.exception.EarlyAccessException;
import bank.fioclient.exception.FioClientException;

@Service("bankPaymentService")
public class BankPaymentServiceImpl implements BankPaymentService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Value("${auth.token}")
    private String authToken;
	
	@Autowired
	private FioService fioService;
	
	@Autowired
	private TransactionDao transactionDao;
	
	@Override
	public AccountStatement transactions(Calendar datumOd, Calendar datumDo) {		
		try {
			LOG.info("Getting transactions from internet banking client, dateFrom: {}, dateTo: {}", datumOd, datumDo);
			AccountStatement ret = fioService.transactions(new AuthToken(authToken), datumOd, datumDo);
			LOG.info("Geting transactions from internet banking client finished, transactions: {}, dateFrom: {} dateTo: {}", ret.getTransactions().size(), datumOd, datumDo);
			return ret;
		} catch (EarlyAccessException e) {
			LOG.error("EarlyAccessException caught, ", e);
			return null;
		} catch (FioClientException e) {
			LOG.error("FioClientException caught, ", e);
			throw new RuntimeException(e);
		}		
	}
	
	@Override
	public List<Transaction> getByInterval(Calendar dateFrom, Calendar dateTo) {
		return transactionDao.getByInterval(dateFrom, dateTo);
	}
	
	@Override
	public void processPayments(Calendar dateFrom, Calendar dateTo) {
		LOG.info("Processing payments, dateFrom: {}, dateTo: {}", dateFrom, dateTo);
		AccountStatement accountStatement = transactions(dateFrom, dateTo);
		if (accountStatement == null || CollectionUtils.isEmpty(accountStatement.getTransactions())) {
			LOG.info("No payment to process, dateFrom: {}, dateTo: {}", dateFrom, dateTo);
			return;
		}
		
		Set<String> idPohybuSet = transactionDao.getAllIdPohybu();
		
		// check unique idPohybu
		List<Transaction> transactionToProcessList = new ArrayList<>();
		
		for (Transaction transaction : accountStatement.getTransactions()) {
			if (!idPohybuSet.contains(String.valueOf(transaction.getIdPohybu()))) {
				transactionToProcessList.add(transaction);
			}
		}
		
		transactionDao.insertBulk(transactionToProcessList);
	}
}