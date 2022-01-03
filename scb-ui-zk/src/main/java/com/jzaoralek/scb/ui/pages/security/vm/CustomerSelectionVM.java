package com.jzaoralek.scb.ui.pages.security.vm;

import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.sportologic.common.model.domain.CustomerConfig;
import com.jzaoralek.scb.dataservice.service.AdmCustConfigService;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

/**
 * View model for organization selection.
 * Used when customerContext is not in cookie and base url is used.
 * It means that user is in application for the first time and in url
 * is not entered customer context.
 *
 */
public class CustomerSelectionVM extends BaseVM {

	@WireVariable
	private AdmCustConfigService admCustConfigService;
	
	private List<CustomerConfig> customerList;
	private CustomerConfig customerSelected;
	
	@Override
	@Init
	public void init() {
		super.init();
		customerList = admCustConfigService.getCustomerAll();
	}
	
	/**
	 * Redirect to customer instance login page.
	 */
	@Command
	public void customerSelectCmd() {
		Executions.sendRedirect("/" + customerSelected.getCustId() + WebPages.LOGIN_PAGE.getUrl());
	}
	
	public List<CustomerConfig> getCustomerList() {
		return customerList;
	}
	
	public CustomerConfig getCustomerSelected() {
		return customerSelected;
	}

	public void setCustomerSelected(CustomerConfig customerSelected) {
		this.customerSelected = customerSelected;
	}	
}
