package com.sportologic.ruianclient.model;

import java.io.Serializable;
import java.util.Comparator;

public class RuianMunicipality implements Serializable {

	private static final long serialVersionUID = -3975417284100899516L;

	public static final Comparator<RuianMunicipality> MUNICIPALITY_COMP =
			new Comparator<RuianMunicipality>() {
				public int compare(RuianMunicipality mun1, RuianMunicipality mun2) {
					String mun1Name = mun1.getMunicipalityName();
					String mun2Name = mun2.getMunicipalityName();
		            return mun1Name.compareTo(mun2Name);
				}
		};
		
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
