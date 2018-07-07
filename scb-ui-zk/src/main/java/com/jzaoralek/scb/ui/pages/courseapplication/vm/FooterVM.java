package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import org.zkoss.bind.annotation.Init;

import com.jzaoralek.scb.ui.common.utils.ConfigUtil;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class FooterVM extends BaseVM {

	@Init
	public void init() {
		super.init();
	}
	
	public String getOrgEmail() {
		return ConfigUtil.getOrgEmail(configurationService);
	}
	
	public String getOrgPhone() {
		return ConfigUtil.getOrgPhone(configurationService);
	}
}
