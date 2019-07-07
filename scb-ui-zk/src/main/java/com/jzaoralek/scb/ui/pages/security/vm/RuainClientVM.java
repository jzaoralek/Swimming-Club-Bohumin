package com.jzaoralek.scb.ui.pages.security.vm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;
import com.sportologic.ruianclient.model.RuianMunicipality;
import com.sportologic.ruianclient.model.RuianPlace;
import com.sportologic.ruianclient.model.RuianRegion;
import com.sportologic.ruianclient.model.RuianStreet;
import com.sportologic.ruianclient.model.RuianValidationResponse;
import com.sportologic.ruianclient.service.RuianService;

public class RuainClientVM extends BaseVM {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@WireVariable
	private RuianService ruianServiceRest;
	
	private String response;
	private String city;
	private String street;
	private String cp;
	private String co;
	private String ce;
	private String zip;

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
	
	@NotifyChange("response")
	@Command
	public void placeValidationCmd() {
		try {
			RuianValidationResponse res = ruianServiceRest.validate(this.city, this.zip, this.ce, this.co, this.cp, this.street);
			this.response = null;
			if (res != null) {
				this.response = res.toString();			
			}			
		} catch (RuntimeException e) {
			LOG.error("RuntimeException caught, ", e);
			WebUtils.showNotificationError("Služba pro ověření adresy není dostupná, použijte prosím neověřenou adresu.");
		}
	}
	
	public String getResponse() {
		return response;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getCo() {
		return co;
	}

	public void setCo(String co) {
		this.co = co;
	}

	public String getCe() {
		return ce;
	}

	public void setCe(String ce) {
		this.ce = ce;
	}
	
	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
}