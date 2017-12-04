package com.jzaoralek.scb.dataservice.dao.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.dao.TransactionDao;

import bank.fioclient.dto.AccountInfo;
import bank.fioclient.dto.Transaction;

public class TransactionDaoTest extends BaseTestCase {
	
	private static final Calendar datumPohybu = Calendar.getInstance();
	private static final Double objem = 10000D;
	private static final Long idPokynu = 1000L;
	private static final Long idPohybu = 1400L;
	
	
	@Autowired
	private TransactionDao transactionDao;

	private Transaction item;
	
	@Before
	public void setUp() {
		item = new Transaction();
		AccountInfo protiucet = new AccountInfo();
		protiucet.setCisloUctu("protiucet_cisloUctu");
		protiucet.setKodBanky("protiucet_kodBanky");
		protiucet.setNazevBanky("ČŠřžýáíéěščůú_nazev_bakny");
		protiucet.setNazevUctu("ČŠřžýáíéěščůúprotiucet_nazevUctu");
		item.setProtiucet(protiucet);
		
		item.setDatumPohybu(datumPohybu);
		item.setObjem(objem);
		item.setKonstantniSymbol("konstantniSymbol");
		item.setVariabilniSymbol("variabilniSymbol");
		item.setUzivatelskaIdentifikace("uzivatelskaIdentifikace");
		item.setTyp("typ");
		item.setMena("mena");
		item.setIdPokynu(idPokynu);
		item.setIdPohybu(idPohybu);
		item.setKomentar("ČŠřžýáíéěščůú_komentář");
		item.setProvedl("ČŠřžýáíéěščůú_provedl");
		item.setZpravaProPrijemnce("ČŠřžýáíéěščůú_zpravaProPrijemnce");
	}
	
	@Test
	public void insertBulkTest() {
		transactionDao.insertBulk(Arrays.asList(item));
	}
	
	@Test
	public void getAllTest() {
		transactionDao.insertBulk(Arrays.asList(item));
		List<Transaction> transactionList = transactionDao.getAll();
		Assert.assertNotNull(transactionList);
		Assert.assertTrue(transactionList.size() == 1);
	}
	
	@Test
	public void getByIdPohybuTest() {
		transactionDao.insertBulk(Arrays.asList(item));
		Transaction transaction = transactionDao.getByIdPohybu(idPohybu);
		Assert.assertNotNull(transaction);
		Assert.assertTrue(transaction.getIdPohybu().equals(idPohybu));
		
		Transaction transaction2 = transactionDao.getByIdPohybu(9999L);
		Assert.assertNull(transaction2);
	}

	@Test
	public void getByIntervaltest() {
		transactionDao.insertBulk(Arrays.asList(item));
		
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, 1);
		
		List<Transaction> transactionList = transactionDao.getByInterval(yesterday, tomorrow);
		Assert.assertNotNull(transactionList);
		Assert.assertTrue(transactionList.size() == 1);
	}
	
	@Test
	public void getAllIdPohybuTest() {
		transactionDao.insertBulk(Arrays.asList(item));
		Set<String> idPohybuList = transactionDao.getAllIdPohybu();
		Assert.assertNotNull(idPohybuList);
		Assert.assertTrue(idPohybuList.size() == 1);
	}
}
