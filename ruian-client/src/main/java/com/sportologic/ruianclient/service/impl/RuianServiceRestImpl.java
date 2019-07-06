package com.sportologic.ruianclient.service.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.sportologic.ruianclient.model.RuianMunicipality;
import com.sportologic.ruianclient.model.RuianMunicipalityResponse;
import com.sportologic.ruianclient.model.RuianPlace;
import com.sportologic.ruianclient.model.RuianPlaceResponse;
import com.sportologic.ruianclient.model.RuianRegion;
import com.sportologic.ruianclient.model.RuianRegionResponse;
import com.sportologic.ruianclient.model.RuianStreet;
import com.sportologic.ruianclient.model.RuianStreetResponse;
import com.sportologic.ruianclient.service.RuianService;

import webtools.rest.RestExecutor;
import webtools.rest.exception.RestException;

@Service("ruianServiceRest")
public class RuianServiceRestImpl implements RuianService {

//	@Value("${rest.domain}")
	private String domain = "https://ruian.fnx.io";
	
//	@Value("${rest.authToken}")
	private String authToken = "dd6afece52966e0109885ca6de64e6bd4b0d5236db6cede1463bcdd1fb2cfa16";
	
	private RestExecutor restExecutor;
	
	@PostConstruct
    public void init() {
        restExecutor = new RestExecutor(domain, null);
    }
	
	public List<RuianRegion> getRegionList() {
		RuianRegionResponse response;
		try {
			response = restExecutor.execute("/api/v1/ruian/build/regions?apiKey=" + authToken,  HttpMethod.GET, null, RuianRegionResponse.class);
			if (response == null) {
				return Collections.emptyList();
			}			
			return response.getData();
		} catch (RestException e) {
			// TODO: log exception
			return null;
		}
	}

	public List<RuianMunicipality> getMunicipalityList(String regionId) {
		RuianMunicipalityResponse response;
		try {
			response = restExecutor.execute("/api/v1/ruian/build/municipalities?apiKey=" + authToken + "&regionId=" + regionId,  HttpMethod.GET, null, RuianMunicipalityResponse.class);
			if (response == null) {
				return Collections.emptyList();
			}			
			return response.getData();
		} catch (RestException e) {
			// TODO: log exception
			return null;
		}
	}

	public List<RuianStreet> getStreetList(String municipalityId) {
		RuianStreetResponse response;
		try {
			response = restExecutor.execute("/api/v1/ruian/build/streets?apiKey=" + authToken + "&municipalityId=" + municipalityId,  HttpMethod.GET, null, RuianStreetResponse.class);
			if (response == null) {
				return Collections.emptyList();
			}
			return response.getData();
		} catch (RestException e) {
			// TODO: log exception
			return null;
		}
	}

	public List<RuianPlace> getPlacesList(String municipalityId, String streetName) {
		RuianPlaceResponse response;
		try {
			response = restExecutor.execute("/api/v1/ruian/build/places?apiKey=" + authToken + "&municipalityId=" + municipalityId + "&streetName=" + streetName,  HttpMethod.GET, null, RuianPlaceResponse.class);
			if (response == null) {
				return Collections.emptyList();
			}
			return response.getData();
		} catch (RestException e) {
			// TODO: log exception
			return null;
		}
	}

	public String validate(String municipalityName, String zip, String ce, String co, String cp, String street) {
		// TODO Auto-generated method stub
		return null;
	}
}
