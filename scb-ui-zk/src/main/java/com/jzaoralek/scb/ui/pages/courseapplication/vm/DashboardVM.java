package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import com.jzaoralek.scb.ui.common.utils.ConfigUtil;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class DashboardVM extends BaseVM {
	
	public String getWelcomeInfo() {
		return ConfigUtil.getWelcomeInfo(configurationService);
	}
}
