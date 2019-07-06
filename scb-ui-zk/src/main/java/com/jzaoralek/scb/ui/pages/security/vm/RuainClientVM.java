package com.jzaoralek.scb.ui.pages.security.vm;

import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.ui.common.vm.BaseVM;
import com.sportologic.ruianclient.model.RuianMunicipality;
import com.sportologic.ruianclient.model.RuianPlace;
import com.sportologic.ruianclient.model.RuianRegion;
import com.sportologic.ruianclient.model.RuianStreet;
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
	
	@NotifyChange("response")
	@Command
	public void getMuicipalityListCmd() {
		List<RuianMunicipality> res = ruianServiceRest.getMunicipalityList("CZ020");
		this.response = null;
		if (res != null) {
			this.response = res.toString();			
		}
	}
	
	@NotifyChange("response")
	@Command
	public void getStreetListCmd() {
		List<RuianStreet> res = ruianServiceRest.getStreetList("539163");
		this.response = null;
		if (res != null) {
			this.response = res.toString();			
		}
	}
	
	@NotifyChange("response")
	@Command
	public void getPlacesListCmd() {
		List<RuianPlace> res = ruianServiceRest.getPlacesList("539163", "Na Palouku");
		this.response = null;
		if (res != null) {
			this.response = res.toString();			
		}
	}
	
	public String getResponse() {
		return response;
	}
}
