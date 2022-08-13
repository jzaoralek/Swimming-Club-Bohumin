package com.jzaoralek.scb.ui.api;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.service.AdmCustConfigService;

public class CustDsConfigReloadVM {

	@WireVariable
	private AdmCustConfigService admCustConfigService;

	@Init
	public void init() {
		admCustConfigService.updateCustomerDSConfiguration();
	}
}
