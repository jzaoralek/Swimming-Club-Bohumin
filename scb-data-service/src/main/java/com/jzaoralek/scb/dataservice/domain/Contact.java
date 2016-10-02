package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

/**
 * Contact used in CourseParticipant and User.
 *
 */
public class Contact implements IdentEntity {

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private String firstname;
	private String surname;
	private String street;
	private Long landRegistryNumber;
	private Short houseNumber;
	private String city;
	private String zipCode;
	private String email1;
	private String email2;
	private String phone1;
	private String phone2;

	@Override
	public UUID getUuid() {
		return uuid;
	}
	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	@Override
	public String getModifBy() {
		return modifBy;
	}
	@Override
	public void setModifBy(String modifBy) {
		this.modifBy = modifBy;
	}
	@Override
	public Date getModifAt() {
		return modifAt;
	}
	@Override
	public void setModifAt(Date modifAt) {
		this.modifAt = modifAt;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getCompleteName() {
		return this.surname + " " + this.firstname;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public Long getLandRegistryNumber() {
		return landRegistryNumber;
	}
	public void setLandRegistryNumber(Long landRegistryNumber) {
		this.landRegistryNumber = landRegistryNumber;
	}
	public Short getHouseNumber() {
		return houseNumber;
	}
	public void setHouseNumber(Short houseNumber) {
		this.houseNumber = houseNumber;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getEmail1() {
		return email1;
	}
	public void setEmail1(String email1) {
		this.email1 = email1;
	}
	public String getEmail2() {
		return email2;
	}
	public void setEmail2(String email2) {
		this.email2 = email2;
	}
	public String getPhone1() {
		return phone1;
	}
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}
	public String getPhone2() {
		return phone2;
	}
	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	@Override
	public String toString() {
		return "Contact [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", firstname=" + firstname
				+ ", surname=" + surname + ", street=" + street + ", landRegistryNumber=" + landRegistryNumber
				+ ", houseNumber=" + houseNumber + ", city=" + city + ", zipCode=" + zipCode + ", email1=" + email1
				+ ", email2=" + email2 + ", phone1=" + phone1 + ", phone2=" + phone2 + "]";
	}
}