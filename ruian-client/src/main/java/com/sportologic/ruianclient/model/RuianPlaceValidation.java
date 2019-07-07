package com.sportologic.ruianclient.model;

public class RuianPlaceValidation {

	private String confidence;
	private String municipalityId;
	private String municipalityName;
	private String streetName;
	private String ce;
	private String cp;
	private String co;
	private String zip;
	
	public String getConfidence() {
		return confidence;
	}
	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}
	public String getMunicipalityId() {
		return municipalityId;
	}
	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}
	public String getMunicipalityName() {
		return municipalityName;
	}
	public void setMunicipalityName(String municipalityName) {
		this.municipalityName = municipalityName;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public String getCe() {
		return ce;
	}
	public void setCe(String ce) {
		this.ce = ce;
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
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	@Override
	public String toString() {
		return "RuianPlaceValidation [confidence=" + confidence + ", municipalityId=" + municipalityId
				+ ", municipalityName=" + municipalityName + ", streetName=" + streetName + ", ce=" + ce + ", cp=" + cp
				+ ", co=" + co + ", zip=" + zip + "]";
	}
}
