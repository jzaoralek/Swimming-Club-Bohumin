package com.jzaoralek.scb.ui.pages.security.vm;

import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.ui.common.vm.BaseVM;
import com.sportologic.ruianclient.model.RuianRegion;
import com.sportologic.ruianclient.service.RuianService;

public class RuainClientVM extends BaseVM {

	@WireVariable
	private RuianService ruianServiceRest;
	
	private String response;

	@NotifyChange("response")
	@Command
	public void getRegionListCmd() {
		List<RuianRegion> res = ruianServiceRest.getRegionList();
		this.response = null;
		if (res != null) {
			this.response = res.toString();			
		}
	}
	
	public String getResponse() {
		return response;
	}
}
