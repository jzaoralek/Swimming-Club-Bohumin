package com.sportologic.ruianclient.client;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.sportologic.ruianclient.model.RuianRegion;
import com.sportologic.ruianclient.model.RuianRegionResponse;

import webtools.rest.RestExecutor;
import webtools.rest.exception.RestException;

@Component
public class RuianClient {

	private static final String DOMAIN = "https://ruian.fnx.io";
	private static final String AUTH_TOKEN = "dd6afece52966e0109885ca6de64e6bd4b0d5236db6cede1463bcdd1fb2cfa16";
	
    private RestExecutor restExecutor;

    public RuianClient() {
        restExecutor = new RestExecutor(DOMAIN, null);
    }
    
    public List<RuianRegion> getRegionList(String token) throws RestException {
    	RuianRegionResponse response = restExecutor.execute("/api/v1/ruian/build/regions?apiKey=" + AUTH_TOKEN,  HttpMethod.GET, null, RuianRegionResponse.class);
    	if (response == null) {
    		return Collections.emptyList();
    	}
    	
		return response.getData();
	}
}
