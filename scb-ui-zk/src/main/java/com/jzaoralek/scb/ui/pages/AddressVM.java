package com.jzaoralek.scb.ui.pages;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Combobox;

import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;
import com.sportologic.ruianclient.model.RuianMunicipality;
import com.sportologic.ruianclient.model.RuianRegion;
import com.sportologic.ruianclient.model.RuianStreet;
import com.sportologic.ruianclient.model.RuianValidationResponse;
import com.sportologic.ruianclient.service.RuianService;

public class AddressVM extends BaseVM {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@WireVariable
	private RuianService ruianServiceRest;
	
	@Wire
	private Combobox city;
	
	@Wire
	private Combobox street;
	
	private String response;
	
	private List<RuianRegion> regionList;
	private RuianRegion regionSelected;
	private List<RuianMunicipality> municipalityList;
	private RuianMunicipality municipalitySelected;
	private List<RuianStreet> streetList;
	private RuianStreet streetSelected;
	private String cp;
	private String co;
	private String ce;
	private String zip;
	private RuianValidationResponse validationResponse;

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}
	
	@NotifyChange({"response","regionList"})
	@Command
	public void getRegionListCmd() {
		this.regionList = ruianServiceRest.getRegionList();
		this.response = null;
		if (this.regionList != null) {
			this.response = this.regionList.toString();			
		}
	}
	
	@NotifyChange("*")
	@Command
	public void regionSelectCmd() {
		if (this.regionSelected == null) {
			return;
		}
		this.municipalityList = ruianServiceRest.getMunicipalityList(this.regionSelected.getRegionId());
		this.streetList = Collections.emptyList();
		cleanAddress(true, true);
	}
	
	@NotifyChange("*")
	@Command
	public void municipaltitySelectCmd() {		
		if (this.municipalitySelected == null) {
			return;
		}
		this.streetList = ruianServiceRest.getStreetList(this.municipalitySelected.getMunicipalityId());
		cleanAddress(false, true);
	}
	
	@NotifyChange("*")
	@Command
	public void streetSelectCmd() {
		cleanAddress(false, false);
	}
	
	@NotifyChange("validationResponse")
	@Command
	public void addressItemChangedCmd() {
		this.validationResponse = null;
	}
	
	@NotifyChange("validationResponse")
	@Command
	public void placeValidationCmd() {
		try {
			this.validationResponse = ruianServiceRest.validate(getMunicipalityName(), this.zip, this.ce, this.co, this.cp, getStreetName());			
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
	
	private void cleanAddress(boolean cleanMunic, boolean cleanStreet) {
		if (cleanMunic) {
			this.municipalitySelected = null;			
			city.setSelectedItem(null);
		}
		if (cleanStreet) {
			this.streetSelected = null;
			street.setSelectedItem(null);
		}
		this.cp = null;
		this.co = null;
		this.ce = null;
		this.zip = null;
		
		this.validationResponse = null;		
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
	
	public RuianValidationResponse getValidationResponse() {
		return validationResponse;
	}
}
