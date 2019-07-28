package com.jzaoralek.scb.ui.pages;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.ui.common.component.address.AddressUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class AddressDemoVM extends BaseVM {

	private Contact contact;
	private String courseReprName;

	@Init
	public void init() {
		this.contact = new Contact();
		this.contact.setRegion("Zlínský kraj");
		this.contact.setCity("Valašské Meziříčí");
		this.contact.setStreet("Blahoslavova");
		this.contact.setLandRegistryNumber(418L);
		this.contact.setHouseNumber("6");
		this.contact.setEvidenceNumber("");
		this.contact.setZipCode("70800");
		
		// TODO: nefunkční ruian, zahraniční adresa - vše textbox
		//       male pismeno na zacatku
		//       umístění indikátoru dolů neověřená ?, neplatná křížek, ověřená fajfka
	}
	
	@Command
	public void submitCmd() {
		if (!AddressUtils.isAddressValid()) {
			return;
		}
		WebUtils.showNotificationInfo("Submitted. Contact city:" + this.contact.getCity() + ", street:" + this.contact.getStreet());
		AddressUtils.setAddressInvalid();
	}
	
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public String getCourseReprName() {
		return courseReprName;
	}
	public void setCourseReprName(String courseReprName) {
		this.courseReprName = courseReprName;
	}
}
