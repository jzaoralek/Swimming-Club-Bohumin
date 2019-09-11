package com.sportologic.ruianclient.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sportologic.ruianclient.model.RuianMunicipality;
import com.sportologic.ruianclient.model.RuianMunicipalityResponse;
import com.sportologic.ruianclient.model.RuianPlace;
import com.sportologic.ruianclient.model.RuianPlaceResponse;
import com.sportologic.ruianclient.model.RuianRegion;
import com.sportologic.ruianclient.model.RuianRegionResponse;
import com.sportologic.ruianclient.model.RuianStreet;
import com.sportologic.ruianclient.model.RuianStreetResponse;
import com.sportologic.ruianclient.model.RuianValidationResponse;
import com.sportologic.ruianclient.service.RuianService;

import webtools.rest.RestExecutor;
import webtools.rest.exception.RestException;

@Service("ruianServiceRest")
public class RuianServiceRestImpl implements RuianService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private static final String REST_EXCEPT_CAUGHT = "RestException caught, ";
	
//	@Value("${rest.domain}")
	private String domain = "https://ruian.fnx.io";
	
//	@Value("${rest.authToken}")
	private String authToken = "dd6afece52966e0109885ca6de64e6bd4b0d5236db6cede1463bcdd1fb2cfa16";
	
	private RestExecutor restExecutor;
	
	@PostConstruct
    public void init() {
        restExecutor = new RestExecutor(domain, null);
    }
	
	@Cacheable(value="ruianRegionCache")
	public List<RuianRegion> getRegionList() {
		try {
			RuianRegionResponse response = restExecutor.execute("/api/v1/ruian/build/regions?apiKey=" + authToken,  HttpMethod.GET, null, RuianRegionResponse.class);
			if (response == null) {
				return Collections.emptyList();
			}			
			Collections.sort(response.getData(), RuianRegion.REGION_COMP);
			return response.getData();
		} catch (RestException e) {
			LOG.error(REST_EXCEPT_CAUGHT, e);
			return Collections.emptyList();
		}
	}

	@Cacheable(value="ruianMunicipalityCache", key="#p0")
	public List<RuianMunicipality> getMunicipalityList(String regionId) {
		try {
			RuianMunicipalityResponse response = restExecutor.execute("/api/v1/ruian/build/municipalities?apiKey=" + authToken + "&regionId=" + regionId,  HttpMethod.GET, null, RuianMunicipalityResponse.class);
			if (response == null) {
				return Collections.emptyList();
			}
			Collections.sort(response.getData(), RuianMunicipality.MUNICIPALITY_COMP);
			return response.getData();
		} catch (RestException e) {
			LOG.error(REST_EXCEPT_CAUGHT, e);
			return Collections.emptyList();
		}
	}

	@Cacheable(value="ruianStreetCache", key="#municipalityId")
	public List<RuianStreet> getStreetList(String municipalityId) {
		try {
			RuianStreetResponse response = restExecutor.execute("/api/v1/ruian/build/streets?apiKey=" + authToken + "&municipalityId=" + municipalityId,  HttpMethod.GET, null, RuianStreetResponse.class);
			if (response == null) {
				return Collections.emptyList();
			}
			// remove items with null values
			List<RuianStreet> ret = new ArrayList<>();
			for (RuianStreet item : response.getData()) {
				if (item.getStreetName() != null) {
					ret.add(item);
				}
			}
			Collections.sort(ret, RuianStreet.STREET_COMP);
			return ret;
		} catch (RestException e) {
			LOG.error(REST_EXCEPT_CAUGHT, e);
			return Collections.emptyList();
		}
	}

	public List<RuianPlace> getPlacesList(String municipalityId, String streetName) {
		try {
			RuianPlaceResponse response = restExecutor.execute("/api/v1/ruian/build/places?apiKey=" + authToken + "&municipalityId=" + municipalityId + "&streetName=" + streetName,  HttpMethod.GET, null, RuianPlaceResponse.class);
			if (response == null) {
				return Collections.emptyList();
			}
			return response.getData();
		} catch (RestException e) {
			LOG.error(REST_EXCEPT_CAUGHT, e);
			return Collections.emptyList();
		}
	}

	public RuianValidationResponse validate(String municipalityName, String zip, String ce, String co, String cp, String street) {
		try {
			// https://ruian.fnx.io/api/v1/ruian/validate?apiKey={apiKey}&municipalityName={municipalityName}&zip={zip}&cp={cp}&street={streetName}
			StringBuilder url = new StringBuilder("/api/v1/ruian/validate?apiKey=" + authToken);
			appendNotNull(url, "municipalityName", municipalityName);
			appendNotNull(url, "zip", zip);
			appendNotNull(url, "ce", ce);
			appendNotNull(url, "co", co);
			appendNotNull(url, "cp", cp);
			appendNotNull(url, "street", street);
			return restExecutor.execute(url.toString()
					, HttpMethod.GET
					, null
					, RuianValidationResponse.class);
		} catch (RestException e) {
			LOG.error(REST_EXCEPT_CAUGHT, e);
			throw new RuntimeException(e);
		}
	}
	
	private void appendNotNull(StringBuilder sb, String key, String value) {
		if (StringUtils.hasText(value)) {
			sb.append("&" + key + "=" + value);
		}
	}
}
