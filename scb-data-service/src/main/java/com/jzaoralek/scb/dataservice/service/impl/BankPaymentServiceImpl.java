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
import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.dao.CourseDao;
import com.jzaoralek.scb.dataservice.dao.CourseParticipantDao;
import com.jzaoralek.scb.dataservice.dao.PaymentDao;
import com.jzaoralek.scb.dataservice.dao.TransactionDao;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.domain.Payment.PaymentProcessType;
import com.jzaoralek.scb.dataservice.service.BankPaymentService;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;

import bank.fioclient.dto.AccountStatement;
import bank.fioclient.dto.AuthToken;
import bank.fioclient.dto.Transaction;
import bank.fioclient.exception.EarlyAccessException;
import bank.fioclient.exception.FioClientException;

@Service("bankPaymentService")
public class BankPaymentServiceImpl extends BaseAbstractService implements BankPaymentService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Value("${auth.token}")
    private String authToken;
	
	@Autowired
	private FioService fioService;
	
	@Autowired
	private TransactionDao transactionDao;
	
	@Autowired
	private PaymentDao paymentDao;
	
	@Autowired
	private CourseParticipantDao courseParticipantDao;
	
	@Autowired
	private CourseDao courseDao;
	
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
	public List<Transaction> getNotInPaymentByInterval(Calendar dateFrom, Calendar dateTo) {
		return transactionDao.getNotInPaymentByInterval(dateFrom, dateTo);
	}
	
	@Override
	public int updateBankPayments(Calendar dateFrom, Calendar dateTo) {
		LOG.info("Updating bank payments, dateFrom: {}, dateTo: {}", dateFrom, dateTo);
		// nacteni bankovnich transakci z FIO
		AccountStatement accountStatement = transactions(dateFrom, dateTo);
		if (accountStatement == null || CollectionUtils.isEmpty(accountStatement.getTransactions())) {
			LOG.info("No bank payment to process, dateFrom: {}, dateTo: {}", dateFrom, dateTo);
			return 0;
		}
		// nacteni sady jiz zpracovanych idPohybu bankovnich transakci z FIO
		Set<String> idPohybuSet = transactionDao.getAllIdPohybu();
		
		// vytvoreni seznamu novych bankovnich transakci pro zpracovani
		List<Transaction> transactionToProcessList = new ArrayList<>();
		for (Transaction transaction : accountStatement.getTransactions()) {
			if (!idPohybuSet.contains(String.valueOf(transaction.getIdPohybu()))) {
				transactionToProcessList.add(transaction);
			}
		}
		
		if (CollectionUtils.isEmpty(transactionToProcessList)) {
			LOG.info("No new bank payment to process, dateFrom: {}, dateTo: {}", dateFrom, dateTo);
			return 0;
		}
		
		LOG.info("New bank payment to process: {}, dateFrom: {}, dateTo: {}", transactionToProcessList.size(), dateFrom, dateTo);
		
		// ulozeni seznamu novych bankovnich transakci
		transactionDao.insertBulk(transactionToProcessList);
		
		LOG.info("Updating bank payments finished. New bank payments: {}, dateFrom: {}, dateTo: {}", transactionToProcessList.size(), dateFrom, dateTo);
		
		return transactionToProcessList.size();
	}
	
	@Override
	public int processPaymentPairing(Calendar dateFrom, Calendar dateTo) {
		LOG.info("Processing transaction to payment pairing, dateFrom: {}, dateTo: {}", dateFrom, dateTo);
		
		// nacteni idPohybu jiz zparovanych plateb 
		Set<String> processedIdPohybuSet = paymentDao.getAllBankTransIdPohybu();
		
		// nacteni bankovnich transakci pro dany rocnik a vyrazeni jiz zparovanych, vysledkem je seznam plateb, ktere zatim nebyly zparovany
		List<Transaction> transactionList = transactionDao.getByInterval(dateFrom, dateTo);
		
		// vyrazenio jiz zparovanych bankovnich transakci a vytvoreni seznamu novych bankovnich transakci pro zparovani
		List<Transaction> unpairedTransactionList = new ArrayList<>();
		for (Transaction transaction : transactionList) {
			if (!processedIdPohybuSet.contains(String.valueOf(transaction.getIdPohybu()))) {
				unpairedTransactionList.add(transaction);
			}
		}
		
		if (CollectionUtils.isEmpty(unpairedTransactionList)) {
			LOG.info("No transaction to pairing, dateFrom: {}, dateTo: {}", dateFrom, dateTo);
			return 0;
		}
		
		LOG.info("Transaction to pairing: {}, dateFrom: {}, dateTo: {}", unpairedTransactionList.size(), dateFrom, dateTo);
		
		// projit bankovni transakce ke zparovani a v danem rocniku vyhledat ucastniky k nimz transakce patri a vytvorit seznam plateb ke zpracovani
		List<Payment> paymentToProcessList = new ArrayList<>();
		Payment payment = null;
		List<Course> courseList = null;
		for (Transaction transaction : unpairedTransactionList) {
			// vyhledat zda-li v danem rocniku existuje courseParticipant s personalNo = varSymbol
			if (StringUtils.hasText(transaction.getVariabilniSymbol())) {
				CourseParticipant courseParticipant = courseParticipantDao.getByPersonalNumberAndInterval(transaction.getVariabilniSymbol(), dateFrom.get(Calendar.YEAR), dateTo.get(Calendar.YEAR));
				if (courseParticipant != null) {
					// dotahnout kurz do nehoz ucastnik parti, pocita se s tim ze v danem rocniku muze byt ucastnik pouze v jednom kurzu!!!
					courseList = courseDao.getByCourseParticipantUuid(courseParticipant.getUuid(), dateFrom.get(Calendar.YEAR), dateTo.get(Calendar.YEAR));
					// na zaklade transaction a courseParticipant vytvorit payment pro zpracovani
					payment = new Payment(transaction, courseList.get(0), courseParticipant, PaymentProcessType.AUTOMATIC);
					fillIdentEntity(payment);
					paymentToProcessList.add(payment);
				}				
			}
		}
		
		if (CollectionUtils.isEmpty(paymentToProcessList)) {
			LOG.info("No new payment to process, dateFrom: {}, dateTo: {}", dateFrom, dateTo);
			return 0;
		}
		
		LOG.info("New payment to process: {}, dateFrom: {}, dateTo: {}", paymentToProcessList.size(), dateFrom, dateTo);
		
		// ulozeni seznamu novych plateb
		for (Payment paymentToProcess : paymentToProcessList) {
			paymentDao.insert(paymentToProcess);
		}
		
		LOG.info("Processing new payments finished. Processed new payments: {}, dateFrom: {}, dateTo: {}", paymentToProcessList.size(), dateFrom, dateTo);
		
		return paymentToProcessList.size();
	}
}