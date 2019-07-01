package com.jzaoralek.scb.ui.pages.security.vm;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.ui.common.vm.BaseVM;
import com.sportologic.ruianclient.service.RuianService;

public class RuainClientVM extends BaseVM {

	@WireVariable
	private RuianService ruianServiceRest;
	
	@Init
	public void init() {
		super.init();
	}
	
	@Command
	public void getRegionListCmd() {
		String res = ruianServiceRest.getRegionList();
		System.out.println(res);
	}
}
