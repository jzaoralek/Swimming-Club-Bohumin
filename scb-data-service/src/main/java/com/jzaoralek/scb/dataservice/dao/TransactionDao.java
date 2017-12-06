package com.jzaoralek.scb.dataservice.dao;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import bank.fioclient.dto.Transaction;

public interface TransactionDao {
	void insertBulk(List<Transaction> transactionList);
	List<Transaction> getAll();
	Transaction getByIdPohybu(Long idPohybu);
	List<Transaction> getByInterval(Calendar dateFrom, Calendar dateTo);
	Set<String> getAllIdPohybu();
}