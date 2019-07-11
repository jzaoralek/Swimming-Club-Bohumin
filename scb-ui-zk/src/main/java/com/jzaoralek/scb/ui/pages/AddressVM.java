package com.jzaoralek.scb.ui.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.SimpleListModel;

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
	private ListModel<String> municipalityListModel;
	private String municipalityNameSelected;

	private List<RuianStreet> streetList;
	private RuianStreet streetSelected;
	private ListModel<String> streetListModel;
	private String streetNameSelected;
	
	private String cp;
	private String co;
	private String ce;
	private String zip;
	private RuianValidationResponse validationResponse;

	@Init
	public void init() {
		fillRegionList();
	}
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}
	
	private void fillRegionList() {
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
		
		if (!CollectionUtils.isEmpty(this.municipalityList)) {
			List<String> municipalityNameList = new ArrayList<>();
			this.municipalityList.forEach(i  -> municipalityNameList.add(i.getMunicipalityName()));
			this.municipalityListModel = new SimpleListModel<>(municipalityNameList.toArray(new String[municipalityNameList.size()]));
		}
		
		this.streetList = Collections.emptyList();
		this.streetListModel = null;
		cleanAddress(true, true);
	}
	
	@NotifyChange("*")
	@Command
	public void municipaltitySelectCmd() {
		this.streetList = null;
		this.municipalitySelected = null;
		if (!StringUtils.hasText(this.municipalityNameSelected) 
				|| CollectionUtils.isEmpty(this.municipalityList)) {
			return;
		}
		List<RuianMunicipality> municipalityFilterred = this.municipalityList.stream()
																.filter(i -> i.getMunicipalityName().equals(this.municipalityNameSelected))
																.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(municipalityFilterred)) {
			this.municipalitySelected = municipalityFilterred.get(0);		
			this.streetList = ruianServiceRest.getStreetList(this.municipalitySelected.getMunicipalityId());
			
			if (!CollectionUtils.isEmpty(this.streetList)) {
				List<String> streetNameList = new ArrayList<>();
				this.streetList.forEach(i  -> streetNameList.add(i.getStreetName()));
				this.streetListModel = new SimpleListModel<>(streetNameList.toArray(new String[streetNameList.size()]));
			}
		}
		cleanAddress(false, true);
	}
	
	@NotifyChange("*")
	@Command
	public void streetSelectCmd() {
		this.streetSelected = null;
		if (!StringUtils.hasText(this.streetNameSelected) 
				|| CollectionUtils.isEmpty(this.streetList)) {
			return;
		}
		
		List<RuianStreet> streetFilterred = this.streetList.stream()
				.filter(i -> i.getStreetName().equals(this.streetNameSelected))
				.collect(Collectors.toList());
		
		if (!CollectionUtils.isEmpty(streetFilterred)) {
			this.streetSelected = streetFilterred.get(0);
		}
		
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
			if (this.validationResponse != null && this.validationResponse.isValid()) {
				WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.address.AddressIsValid"));
			} else if (this.validationResponse != null && !this.validationResponse.isValid()) {
				WebUtils.showNotificationError(Labels.getLabel("msg.ui.address.AddressIsNotValid"));
			}
			
		} catch (RuntimeException e) {
			LOG.error("RuntimeException caught, ", e);
			WebUtils.showNotificationError(Labels.getLabel("msg.ui.address.AddressVerificationServiceNotAvailable"));
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
			this.municipalityNameSelected = null;
			city.setSelectedItem(null);
		}
		if (cleanStreet) {
			this.streetSelected = null;
			this.streetNameSelected = null;
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
	
	public RuianValidationResponse getValidationResponse() {
		return validationResponse;
	}
	
	public ListModel<String> getMunicipalityListModel() {
		return municipalityListModel;
	}
	
	public String getMunicipalityNameSelected() {
		return municipalityNameSelected;
	}

	public void setMunicipalityNameSelected(String municipalityNameSelected) {
		this.municipalityNameSelected = municipalityNameSelected;
	}
	
	public String getStreetNameSelected() {
		return streetNameSelected;
	}

	public void setStreetNameSelected(String streetNameSelected) {
		this.streetNameSelected = streetNameSelected;
	}

	public ListModel<String> getStreetListModel() {
		return streetListModel;
	}
	
	public RuianMunicipality getMunicipalitySelected() {
		return municipalitySelected;
	}
	
	public RuianStreet getStreetSelected() {
		return streetSelected;
	}
}
