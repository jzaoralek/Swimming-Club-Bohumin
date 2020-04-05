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
	private String cp;
	private String co;
	private String ce;
	private String zip;
	
	private List<RuianRegion> regionList;
	private RuianRegion regionSelected;
	private List<RuianMunicipality> municipalityList;
	private RuianMunicipality municipalitySelected;
	private List<RuianStreet> streetList;
	private RuianStreet streetSelected;

	@NotifyChange({"response","regionList"})
	@Command
	public void getRegionListCmd() {
		this.regionList = ruianServiceRest.getRegionList();
		this.response = null;
		if (this.regionList != null) {
			this.response = this.regionList.toString();			
		}
	}
	
	@NotifyChange("response")
	@Command
	public void getMuicipalityListCmd() {
		this.municipalityList = ruianServiceRest.getMunicipalityList("CZ020");
		this.response = null;
		if (this.municipalityList != null) {
			this.response = this.municipalityList.toString();			
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
			RuianValidationResponse res = ruianServiceRest.validate(getMunicipalityName(), this.zip, this.ce, this.co, this.cp, getStreetName());
			this.response = null;
			if (res != null) {
				this.response = res.toString();			
			}			
		} catch (RuntimeException e) {
			LOG.error("RuntimeException caught, ", e);
			WebUtils.showNotificationError("Služba pro ověření adresy není dostupná, použijte prosím neověřenou adresu.");
		}
	}
	
	private String getMunicipalityName() {
		if (this.municipalitySelected != null) {
			return this.municipalitySelected.getMunicipalityName();
		}
		
		return null;
	}
	
	private String getStreetName() {
		if (this.streetSelected != null) {
			return this.streetSelected.getStreetName();
		}
		
		return null;
	}
	
	@NotifyChange("municipalityList")
	@Command
	public void regionSelectCmd() {
		if (this.regionSelected == null) {
			return;
		}
		this.municipalityList = ruianServiceRest.getMunicipalityList(this.regionSelected.getRegionId());
	}
	
	@NotifyChange("streetList")
	@Command
	public void municipaltitySelectCmd() {		
		if (this.municipalitySelected == null) {
			return;
		}
		this.streetList = ruianServiceRest.getStreetList(this.municipalitySelected.getMunicipalityId());
	}
	
	public String getResponse() {
		return response;
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
	
	public List<RuianRegion> getRegionList() {
		return regionList;
	}
	
	public RuianRegion getRegionSelected() {
		return regionSelected;
	}

	public void setRegionSelected(RuianRegion regionSelected) {
		this.regionSelected = regionSelected;
	}
	
	public List<RuianMunicipality> getMunicipalityList() {
		return municipalityList;
	}
	
	public RuianMunicipality getMunicipalitySelected() {
		return municipalitySelected;
	}

	public void setMunicipalitySelected(RuianMunicipality municipalitySelected) {
		this.municipalitySelected = municipalitySelected;
	}
	
	public List<RuianStreet> getStreetList() {
		return streetList;
	}
	
	public RuianStreet getStreetSelected() {
		return streetSelected;
	}

	public void setStreetSelected(RuianStreet streetSelected) {
		this.streetSelected = streetSelected;
	}
}