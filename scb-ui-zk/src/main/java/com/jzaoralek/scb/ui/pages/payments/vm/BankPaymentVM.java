package com.jzaoralek.scb.ui.pages.payments.vm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;

import com.jzaoralek.scb.dataservice.service.BankPaymentService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.ExcelUtil;
import com.jzaoralek.scb.ui.common.vm.BaseContextVM;

import bank.fioclient.dto.Transaction;

public class BankPaymentVM extends BaseContextVM {

	@WireVariable
	private BankPaymentService bankPaymentService;
	
	private List<Transaction> transactionList;
	private List<Transaction> transactionListBase;
	private Calendar dateFrom;
	private Calendar dateTo;
	private final TransactionFilter filter = new TransactionFilter();

	@Init
	public void init() {
		initYearContext();
		loadData();
	}
	
	@NotifyChange("*")
	@Command
	public void reloadPaymentsCmd() {
		bankPaymentService.processPayments(this.dateFrom, this.dateTo);
		loadData();
	}
	
	@NotifyChange("*")
	@Command
	public void refreshDataCmd() {
		loadData();
		filter.setEmptyValues();
	}
	
	@Command
	@NotifyChange("transactionList")
	public void filterDomCmd() {
		this.transactionList = filter.getTransactionListFiltered(this.transactionListBase);
	}
	
	@Command
	public void exportToExcel(@BindingParam("listbox") Listbox listbox) {
		ExcelUtil.exportToExcel("seznam_bankovnich_plateb.xls", buildExcelRowData(listbox));
	}
	
	@Override
	protected void courseYearChangeCmdCore() {
		loadData();
	}
	
	private void loadData() {
		setDateFromTo();
		this.transactionList = bankPaymentService.getByInterval(this.dateFrom, this.dateTo);
		this.transactionListBase = this.transactionList;
	}
	
	private void setDateFromTo() {
		String[] years = getYearsFromContext();
		
		int yearFrom = Integer.parseInt(years[0]);
		int yearTo = Integer.parseInt(years[1]);
		this.dateFrom = new GregorianCalendar(yearFrom,9,1);
		this.dateTo = new GregorianCalendar(yearTo,6,30);
	}
	
	private Map<String, Object[]> buildExcelRowData(@BindingParam("listbox") Listbox listbox) {
		Map<String, Object[]> data = new LinkedHashMap<String, Object[]>();

		// header
		Listhead lh = listbox.getListhead();
		Object[] headerArray = new Object[lh.getChildren().size()];
		for (int i = 0; i < lh.getChildren().size(); i++) {
			headerArray[i] = ((Listheader) lh.getChildren().get(i)).getLabel();
		}
		data.put("0", headerArray);

		// rows
		ListModel<Object> model = listbox.getListModel();
		Transaction item = null;
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i) instanceof Transaction) {
				item = (Transaction)model.getElementAt(i);
				data.put(String.valueOf(i+1),
						new Object[] { getDateConverter().coerceToUi(item.getDatumPohybu().getTime(), null, null),
								item.getObjem() + " " + Labels.getLabel("txt.ui.common.CZK"),
								item.getVariabilniSymbol(),
								item.getProtiucet().getNazevUctu()});
			}
		}

		return data;
	}
	
	public List<Transaction> getTransactionList() {
		return transactionList;
	}
	
	public Calendar getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Calendar dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Calendar getDateTo() {
		return dateTo;
	}

	public void setDateTo(Calendar dateTo) {
		this.dateTo = dateTo;
	}
	
	public TransactionFilter getFilter() {
		return filter;
	}
	
	public static class TransactionFilter {
		private DateFormat dateFormat = new SimpleDateFormat(WebConstants.WEB_DATE_PATTERN);
		private String nazevUctu;
		private String nazevUctuLc;
		private String datumPohybu;
		private String objem;
		private String varSymbol;

		public boolean matches(String nazevUctuIn, String birthDateIn, String objemIn, String varSymbolIn, boolean emptyMatch) {
			if (nazevUctu == null && datumPohybu == null && objem == null && varSymbol == null) {
				return emptyMatch;
			}
			if (nazevUctuIn == null || (nazevUctu != null && !nazevUctuIn.toLowerCase().contains(nazevUctuLc))) {
				return false;
			}
			if (datumPohybu != null && !birthDateIn.contains(datumPohybu)) {
				return false;
			}
			if (objem != null && !objemIn.startsWith(objem)) {
				return false;
			}
			if (varSymbolIn == null || (varSymbol != null && !varSymbolIn.contains(varSymbol))) {
				return false;
			}
			return true;
		}

		public List<Transaction> getTransactionListFiltered(List<Transaction> transactionList) {
			if (transactionList == null || transactionList.isEmpty()) {
				return Collections.<Transaction>emptyList();
			}
			List<Transaction> ret = new ArrayList<Transaction>();
			for (Transaction item : transactionList) {
				if (matches(item.getProtiucet().getNazevUctu()
						, dateFormat.format(item.getDatumPohybu().getTime())
						, String.valueOf(item.getObjem())
						, item.getVariabilniSymbol()
						, true)) {
					ret.add(item);
				}
			}
			return ret;
		}

		public String getnazevUctu() {
			return nazevUctu == null ? "" : nazevUctu;
		}
		public void setnazevUctu(String name) {
			this.nazevUctu = StringUtils.hasText(name) ? name.trim() : null;
			this.nazevUctuLc = this.nazevUctu == null ? null : this.nazevUctu.toLowerCase();
		}
		public String getDatumPohybu() {
			return datumPohybu == null ? "" : datumPohybu;
		}
		public void setDatumPohybu(String datumPohybu) {
			this.datumPohybu = StringUtils.hasText(datumPohybu) ? datumPohybu.trim() : null;
		}
		public String getObjem() {
			return objem == null ? "" : objem;
		}
		public void setObjem(String objem) {
			this.objem = StringUtils.hasText(objem) ? objem.trim() : null;
		}
		public String getVarSymbol() {
			return varSymbol == null ? "" : varSymbol;
		}
		public void setVarSymbol(String varSymbol) {
			this.varSymbol = StringUtils.hasText(varSymbol) ? varSymbol.trim() : null;
		}
		public void setEmptyValues() {
			nazevUctu = null;
			nazevUctuLc = null;
			datumPohybu = null;
			objem = null;
			varSymbol = null;
		}
	}
}