package com.sportologic.ruianclient.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sportologic.ruianclient.service.RuianService;

@Service("ruianServiceRest")
public class RuianServiceRestImpl implements RuianService {

	public String getRegionList() {
		
		final String uri = "https://ruian.fnx.io/api/v1/ruian/build/regions?apiKey=dd6afece52966e0109885ca6de64e6bd4b0d5236db6cede1463bcdd1fb2cfa16";
	     
	    RestTemplate restTemplate = new RestTemplate();
	    return restTemplate.getForObject(uri, String.class);
	}
}
