package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

import org.springframework.util.StringUtils;

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
	private String region;
	private String street;
	private Long landRegistryNumber;
	private String houseNumber;
	private String evidenceNumber;
	private String city;
	private String zipCode;
	private String foreignAddress;
	private AddressValidationStatus addressValidationStatus;
	private String email1;
	private String email2;
	private String phone1;
	private String phone2;

	public Contact() {
		this.addressValidationStatus = AddressValidationStatus.NOT_VERIFIED;
	}
	
	public boolean isAddressValid() {
		return this.addressValidationStatus == AddressValidationStatus.VALID;
	}
	
	public boolean isAddressInvalid() {
		return this.addressValidationStatus == AddressValidationStatus.INVALID;
	}
	
	public boolean isAddressNotVerified() {
		return this.addressValidationStatus == AddressValidationStatus.NOT_VERIFIED;
	}
	
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
	public String getHouseNumber() {
		return houseNumber;
	}
	public void setHouseNumber(String houseNumber) {
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
	public AddressValidationStatus getAddressValidationStatus() {
		return addressValidationStatus;
	}
	public void setAddressValidationStatus(AddressValidationStatus addressValidationStatus) {
		this.addressValidationStatus = addressValidationStatus;
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
	public String buildResidence() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.city + ", ");
		if (StringUtils.hasText(this.street)) {
			sb.append(this.street + " ");			
		}
		sb.append(this.landRegistryNumber);
		if (this.houseNumber != null && !this.houseNumber.equals("0")) {
			sb.append("/"+this.houseNumber);
		}
		return sb.toString();
	}
	public String getEvidenceNumber() {
		return evidenceNumber;
	}
	public void setEvidenceNumber(String evidenceNumber) {
		this.evidenceNumber = evidenceNumber;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getForeignAddress() {
		return foreignAddress;
	}
	public void setForeignAddress(String foreignAddress) {
		this.foreignAddress = foreignAddress;
	}

	@Override
	public String toString() {
		return "Contact [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", firstname=" + firstname
				+ ", surname=" + surname + ", region=" + region + ", street=" + street + ", landRegistryNumber="
				+ landRegistryNumber + ", houseNumber=" + houseNumber + ", evidenceNumber=" + evidenceNumber + ", city="
				+ city + ", zipCode=" + zipCode + ", foreignAddress=" + foreignAddress + ", addressValidationStatus="
				+ addressValidationStatus + ", email1=" + email1 + ", email2=" + email2 + ", phone1=" + phone1
				+ ", phone2=" + phone2 + "]";
	}
}