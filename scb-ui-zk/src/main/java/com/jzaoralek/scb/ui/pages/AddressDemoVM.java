package com.jzaoralek.scb.ui.pages;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

import com.jzaoralek.scb.dataservice.domain.Contact;

public class AddressDemoVM {

	private Contact contact;

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
		
		// TODO: validace adresních elementů
		//       nefunkční ruian, zahraniční adresa - vše textbox
		//       male pismeno na zacatku
		//       nabidka adres zobrazená na tlačítko a ověřit
		//       umístění indikátoru dolů neověřená ?, neplatná křížek, ověřená fajfka
	}
	
	@Command
	public void submitCmd() {
		System.out.println(this.contact);
	}
	
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}	
}
