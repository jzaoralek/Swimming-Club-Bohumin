package com.jzaoralek.scb.ui.pages;

import org.zkoss.bind.annotation.Init;

import com.jzaoralek.scb.dataservice.domain.Contact;

public class AddressDemoVM {

	private Contact contact;

	@Init
	public void init() {
		this.contact = new Contact();
		this.contact.setRegion("Zlínský kraj");
		this.contact.setCity("Valašské Meziříčí2");
		this.contact.setStreet("Blahoslavova");
		this.contact.setLandRegistryNumber(418L);
		this.contact.setHouseNumber("6");
		this.contact.setEvidenceNumber("");
		this.contact.setZipCode("70800");
		
		// TODO: refaktoring AddressVM.initContact()
		// 		 nevalidní adresa na vstupu
		//       castecne validni adresa
		//       male pismeno na zacatku
		//       dynamicka zmena v kontaktu
		//       status ověření do databáze
		//       pokud neověřená, pokusit se ověřit
		//       indikátor neověřená ?, neplatná křížek, ověřená fajfka
	}
	
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}	
}
