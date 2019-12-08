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
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Popup;
import org.zkoss.zul.SimpleListModel;

import com.jzaoralek.scb.dataservice.domain.AddressValidationStatus;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.ui.common.component.address.AddressUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;
import com.sportologic.ruianclient.model.RuianMunicipality;
import com.sportologic.ruianclient.model.RuianPlace;
import com.sportologic.ruianclient.model.RuianRegion;
import com.sportologic.ruianclient.model.RuianStreet;
import com.sportologic.ruianclient.model.RuianValidationResponse;

public class AddressVM extends BaseVM {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Wire
	private Combobox city;
	
	@Wire
	private Combobox street;
	
	@Wire
	private Popup placeListPopup;
	
	@Wire
	private Button placeSearchBtn;
	
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
	
	private Long cp;
	private String co;
	private String ce;
	private String zip;
	private RuianValidationResponse validationResponse;
	
	private List<RuianPlace> placeList;
	private String placeStreetName;

	private Contact contact;

	@Init
	public void init(@BindingParam("contact")Contact contact) {
		fillRegionList();
		initContact(contact);
	}
	
	private void initContact(Contact contact) {
		if (contact != null) {
			this.contact = contact;
			// init region
			initRegion();
			// init municipality
			initMunicipality();
			// init street
			initStreet();
			// init cp
			initOtherElems();
		} else {
			this.contact = new Contact();
		}
	}
	
	private void initRegion() {
		List<RuianRegion> regionFilterred = this.regionList.stream()
				.filter(i -> i.getRegionName().equals(this.contact.getRegion()))
				.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(regionFilterred)) {
			this.regionSelected = regionFilterred.get(0);
			if (this.regionSelected != null) {
				regionSelectCore();	
			}
		} else {
			this.regionSelected = new RuianRegion();
			this.regionSelected.setRegionName(this.contact.getRegion());
		}
	}
	
	private void initMunicipality() {
		List<RuianMunicipality> municipalityFilterred = null;
		if (!CollectionUtils.isEmpty(this.municipalityList)) {
			municipalityFilterred = this.municipalityList.stream()
					.filter(i -> i.getMunicipalityName().equals(this.contact.getCity()))
					.collect(Collectors.toList());
		}
		if (!CollectionUtils.isEmpty(municipalityFilterred)) {
			this.municipalitySelected = municipalityFilterred.get(0);
			if (this.municipalitySelected != null) {
				this.municipalityNameSelected = this.municipalitySelected.getMunicipalityName();
			}
			if (this.municipalitySelected != null) {
				municipaltitySelectCore();
			}
		} else {			
			this.municipalitySelected = new RuianMunicipality();
			this.municipalitySelected.setMunicipalityName(this.contact.getCity());
			this.municipalityNameSelected = this.municipalitySelected.getMunicipalityName();
			this.municipalityListModel = new SimpleListModel<>(new String[]{this.municipalitySelected.getMunicipalityName()});
		}
	}
	
	private void initStreet() {
		if (this.municipalitySelected != null) {
			List<RuianStreet> streetFilterred = null;
			if (!CollectionUtils.isEmpty(this.streetList)) {
				streetFilterred = this.streetList.stream()
						.filter(i -> i.getStreetName().equals(this.contact.getStreet()))
						.collect(Collectors.toList());
			}
			if (!CollectionUtils.isEmpty(streetFilterred)) {
				this.streetSelected = streetFilterred.get(0);
				if (this.streetSelected != null) {
					this.streetNameSelected = this.streetSelected.getStreetName();
				}
				if (this.streetSelected != null) {
					streetSelectCore();
				}
			} else {
				this.streetSelected = new RuianStreet();
				this.streetSelected.setStreetName(this.contact.getStreet());
				this.streetNameSelected = this.streetSelected.getStreetName();
				this.streetListModel = new SimpleListModel<>(new String[]{this.streetSelected.getStreetName()});
			}
		}
	}
	
	private void initOtherElems() {
		this.cp = contact.getLandRegistryNumber();
		this.co = contact.getHouseNumber();
		this.ce = contact.getEvidenceNumber();
		this.zip = contact.getZipCode();
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
		regionSelectCore();
		cleanAddress(true, true);
	}
	
	private void regionSelectCore() {
		if (this.regionSelected == null || !StringUtils.hasText(this.regionSelected.getRegionId())) {
			return;
		}
		
		this.contact.setRegion(this.regionSelected.getRegionName());
		this.municipalityList = ruianServiceRest.getMunicipalityList(this.regionSelected.getRegionId());
		
		if (!CollectionUtils.isEmpty(this.municipalityList)) {
			List<String> municipalityNameList = new ArrayList<>();
			this.municipalityList.forEach(i  -> municipalityNameList.add(i.getMunicipalityName()));
			this.municipalityListModel = new SimpleListModel<>(municipalityNameList.toArray(new String[municipalityNameList.size()]));
		}
		
		this.streetList = Collections.emptyList();
		this.streetListModel = null;
	}
	
	@NotifyChange("*")
	@Command
	public void municipaltitySelectCmd() {
		municipaltitySelectCore();
		cleanAddress(false, true);
	}
	
	private void municipaltitySelectCore() {
		this.streetList = null;
		this.municipalitySelected = null;
		
		// nasetovani z comboboxu, pokud zadána ulice nevybraná ze seznamu
		String cityNameEntered = this.city != null ? this.city.getValue() : "";
		if (StringUtils.hasText(cityNameEntered) && !cityNameEntered.equals(this.municipalityNameSelected)) {
			this.contact.setCity(cityNameEntered);
			this.municipalityNameSelected = cityNameEntered;
			this.municipalityListModel = new SimpleListModel<>(new String[]{this.municipalityNameSelected});

			return;
		}
		
		if (!StringUtils.hasText(this.municipalityNameSelected) 
				|| CollectionUtils.isEmpty(this.municipalityList)) {
			return;
		}
		List<RuianMunicipality> municipalityFilterred = this.municipalityList.stream()
																.filter(i -> i.getMunicipalityName().equals(this.municipalityNameSelected))
																.collect(Collectors.toList());

		this.contact.setCity(this.municipalityNameSelected);
		
		if (!CollectionUtils.isEmpty(municipalityFilterred)) {
			this.municipalitySelected = municipalityFilterred.get(0);		
			this.streetList = ruianServiceRest.getStreetList(this.municipalitySelected.getMunicipalityId());
			
			if (!CollectionUtils.isEmpty(this.streetList)) {
				List<String> streetNameList = new ArrayList<>();
				this.streetList.forEach(i  -> streetNameList.add(i.getStreetName()));
				this.streetListModel = new SimpleListModel<>(streetNameList.toArray(new String[streetNameList.size()]));
			}
		}
	}
	
	@NotifyChange("*")
	@Command
	public void streetSelectCmd() {
		streetSelectCore();
		cleanAddress(false, false);
	}
	
	private void streetSelectCore() {
		// nasetovani z comboboxu, pokud nevalidni ulice
		this.contact.setStreet(this.street != null ? this.street.getValue() : "");
		this.streetSelected = null;
		if (!StringUtils.hasText(this.streetNameSelected) 
				|| CollectionUtils.isEmpty(this.streetList)) {
			return;
		}
		
		this.contact.setStreet(this.streetNameSelected);
		
		List<RuianStreet> streetFilterred = this.streetList.stream()
				.filter(i -> i.getStreetName() != null && i.getStreetName().equals(this.streetNameSelected))
				.collect(Collectors.toList());
		
		if (!CollectionUtils.isEmpty(streetFilterred)) {
			this.streetSelected = streetFilterred.get(0);
		}
	}
	
	@NotifyChange("contact")
	@Command
	public void addressItemChangedCmd() {
		this.contact.setLandRegistryNumber(this.cp);
		this.contact.setHouseNumber(this.co);
		this.contact.setEvidenceNumber(this.ce);
		this.contact.setZipCode(this.zip);
		
		this.validationResponse = null;
		this.contact.setAddressValidationStatus(AddressValidationStatus.NOT_VERIFIED);
	}
	
	@NotifyChange("contact")
	@Command
	public void placeValidationCmd() {
		try {
			this.validationResponse = ruianServiceRest.validate(getMunicipalityName(), this.zip, this.ce, this.co, this.cp != null ? String.valueOf(this.cp) : "", getStreetName());			
			if (this.validationResponse != null && this.validationResponse.isValid()) {
				this.contact.setAddressValidationStatus(AddressValidationStatus.VALID);
				WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.address.AddressIsValid"));
			} else if (this.validationResponse != null && !this.validationResponse.isValid()) {
				this.contact.setAddressValidationStatus(AddressValidationStatus.INVALID);
				WebUtils.showNotificationError(Labels.getLabel("msg.ui.address.AddressIsNotValid"));
			} else {
				this.contact.setAddressValidationStatus(AddressValidationStatus.NOT_VERIFIED);
			}
		} catch (RuntimeException e) {
			LOG.error("RuntimeException caught, ", e);
			WebUtils.showNotificationError(Labels.getLabel("msg.ui.address.AddressVerificationServiceNotAvailable"));
		}
	}
	
	@NotifyChange("contact")
	@Command
	public void validateAddressManualCmd() {
		// check ADMIN permission
		if (!isLoggedUserInRole("ADMIN")) {
			return;
		}
		this.contact.setAddressValidationStatus(AddressValidationStatus.VALID);
		scbUserService.updateAddressValidStatus(this.contact);
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.address.AddressValidated"));
	}
	
	@NotifyChange({"placeList","placeStreetName"})
	@Command
	public void getPlacesCmd() {
		if (!isGetPlacesEnabled()) {
			WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.address.FillCityAndStreet"));
			return;
		}
		
		String streetName = getStreetName();
		this.placeStreetName = streetName;
		this.placeList = ruianServiceRest.getPlacesList(getMunicipalityId(), streetName);
		
		if (!CollectionUtils.isEmpty(this.placeList)) {
			Collections.sort(this.placeList, RuianPlace.PLACE_COMP);			
		}
		
		placeListPopup.open(placeSearchBtn);
	}
	
	@DependsOn({"municipalitySelected", "municipalityNameSelected", "streetSelected", "streetNameSelected"})
	public boolean isGetPlacesEnabled() {
		String municipalityId = getMunicipalityId();
		String streetName = getStreetName();
		
		return StringUtils.hasText(municipalityId) && StringUtils.hasText(streetName);
	}
	
	private String getMunicipalityId() {
		RuianMunicipality ruianMunicipality = null;
		if (this.municipalitySelected != null) {
			ruianMunicipality = this.municipalitySelected;
		} else if (StringUtils.hasText(this.municipalityNameSelected)) {
			ruianMunicipality = getMunicipalityFromListByName(this.municipalityNameSelected);
		} else if (this.city != null) {
			ruianMunicipality = getMunicipalityFromListByName(this.city.getValue());
		}
		
		if (ruianMunicipality != null) {
			return ruianMunicipality.getMunicipalityId();
		}
		
		return null;
	}
	
	private RuianMunicipality getMunicipalityFromListByName(String name) {
		if (!StringUtils.hasText(name) || CollectionUtils.isEmpty(this.municipalityList)) {
			return null;
		}
		
		List<RuianMunicipality> municipalityFilterred = this.municipalityList.stream()
				.filter(i -> i.getMunicipalityName().equals(name))
				.collect(Collectors.toList());
		
		if (!CollectionUtils.isEmpty(municipalityFilterred)) {
			return municipalityFilterred.get(0);
		}
		
		return null;
	}
	
	/**
	 * Command pro submit vyvolavajici validaci adresy.
	 */
	@Command
	public void addressSubmitCmd() {
		AddressUtils.setAddressValid();
	}
	
	@NotifyChange("*")
	@Command
	public void placeSelectCmd(@BindingParam("item") RuianPlace item) {
		if (item == null) {
			return;
		}
		this.cp = StringUtils.hasText(item.getPlaceCp()) ? Long.valueOf(item.getPlaceCp()) : null;
		this.contact.setLandRegistryNumber(this.cp);			
		
		this.co = item.getPlaceCo();
		this.contact.setHouseNumber(item.getPlaceCo());
		
		this.ce = item.getPlaceCe();
		this.contact.setEvidenceNumber(item.getPlaceCe());
		
		this.zip = item.getPlaceZip();
		this.contact.setZipCode(item.getPlaceZip());
		
		this.contact.setAddressValidationStatus(AddressValidationStatus.VALID);
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.address.AddressIsValid"));
		
		placeListPopup.close();
	}
	
	private String getMunicipalityName() {
		if (this.municipalitySelected != null) {
			return this.municipalitySelected.getMunicipalityName();
		} else if (StringUtils.hasText(this.municipalityNameSelected)) {
			return this.municipalityNameSelected;
		} else if (this.city != null) {
			return this.city.getValue();
		}
		
		return null;
	}
	
	private String getStreetName() {
		if (this.streetSelected != null) {
			return this.streetSelected.getStreetName();
		} else if (StringUtils.hasText(this.streetNameSelected)) {
			return this.streetNameSelected;
		} else if (this.street != null) {
			return this.street.getValue();
		}
		
		return null;
	}
	
	private void cleanAddress(boolean cleanMunic, boolean cleanStreet) {
		if (cleanMunic) {
			this.municipalitySelected = null;
			this.municipalityNameSelected = null;
			if (city != null) {
				city.setSelectedItem(null);				
			}
		}
		if (cleanStreet) {
			this.streetSelected = null;
			this.streetNameSelected = null;
			if (street != null) {
				street.setSelectedItem(null);
				street.setValue(null);
			}
		}
		this.cp = null;
		this.co = null;
		this.ce = null;
		this.zip = null;
		
		this.validationResponse = null;
		this.contact.setAddressValidationStatus(AddressValidationStatus.NOT_VERIFIED);
	}
	
	public String getResponse() {
		return response;
	}

	public Long getCp() {
		return cp;
	}

	public void setCp(Long cp) {
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
	
	public Contact getContact() {
		return contact;
	}
	
	public List<RuianPlace> getPlaceList() {
		return placeList;
	}
	
	public String getPlaceStreetName() {
		return placeStreetName;
	}
	
}