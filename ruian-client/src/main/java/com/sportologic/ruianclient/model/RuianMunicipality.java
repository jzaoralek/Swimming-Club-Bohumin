package com.sportologic.ruianclient.model;

public class RuianMunicipality {

	private String municipalityId;
	private String municipalityName;
	
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
	
	@Override
	public String toString() {
		return "RuianMunicipality [municipalityId=" + municipalityId + ", municipalityName=" + municipalityName + "]";
	}
}
