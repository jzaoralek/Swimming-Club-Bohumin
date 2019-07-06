package com.sportologic.ruianclient.service;

import java.util.List;

import com.sportologic.ruianclient.model.RuianMunicipality;
import com.sportologic.ruianclient.model.RuianPlace;
import com.sportologic.ruianclient.model.RuianRegion;
import com.sportologic.ruianclient.model.RuianStreet;

public interface RuianService {

	List<RuianRegion> getRegionList();
	List<RuianMunicipality> getMunicipalityList(String regionId);
	List<RuianStreet> getStreetList(String municipalityId);
	List<RuianPlace> getPlacesList(String municipalityId, String streetName);
	
	/**
	 * Validation of address
	 * @param municipalityName – název obce
	 * @param zip – PSČ
	 * @param ce – Číslo evidenční.
	 * @param co – číslo orientační
	 * @param cp – číslo popisné
	 * @param street – název ulice
	 * @return
	 */
	String validate(String municipalityName, String zip, String ce, String co, String cp, String street);
}
