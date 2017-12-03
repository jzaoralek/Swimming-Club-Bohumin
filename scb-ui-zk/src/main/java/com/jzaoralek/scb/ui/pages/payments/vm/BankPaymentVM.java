package com.jzaoralek.scb.ui.pages.payments.vm;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.service.BankPaymentService;
import com.jzaoralek.scb.ui.common.vm.BaseContextVM;

import bank.fioclient.dto.Transaction;

public class BankPaymentVM extends BaseContextVM {

	@WireVariable
	private BankPaymentService bankPaymentService;
	
	private List<Transaction> transactionList;

	private Date dateFrom;
	private Date dateTo;

	@Init
	public void init() {
		initYearContext();
		
		loadData();
	}
	
	@Command
	public void reloadPaymentsCmd() {
		// TODO: JZ, vyvolat zpracovani
//		this.transactions = bankPaymentService.transactions(datumOd, datumDo);
		loadData();
	}
	
	@NotifyChange("*")
	@Command
	public void refreshDataCmd() {
		loadData();
//		filter.setEmptyValues();
	}
	
	@Override
	protected void courseYearChangeCmdCore() {
		loadData();
	}
	
	private void loadData() {
		String[] years = getYearsFromContext();
		
		int yearFrom = Integer.parseInt(years[0]);
		int yearTo = Integer.parseInt(years[1]);
		Calendar datumOd = new GregorianCalendar(yearFrom,9,1);
		Calendar datumDo = new GregorianCalendar(yearTo,6,30);
		
		this.transactionList = bankPaymentService.getByInterval(datumOd, datumDo);
	}
	
	public List<Transaction> getTransactionList() {
		return transactionList;
	}
	
	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
}