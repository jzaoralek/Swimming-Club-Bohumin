package com.sportologic.ruianclient.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.sportologic.ruianclient.client.RuianClient;
import com.sportologic.ruianclient.model.RuianRegion;
import com.sportologic.ruianclient.service.RuianService;

import webtools.rest.exception.RestException;

@Service("ruianServiceRest")
public class RuianServiceRestImpl implements RuianService {

	private RuianClient ruianClient;
	
	@PostConstruct
    public void init() {
		ruianClient = new RuianClient();
    }
	
	public List<RuianRegion> getRegionList() {
		try {
			return ruianClient.getRegionList("dd6afece52966e0109885ca6de64e6bd4b0d5236db6cede1463bcdd1fb2cfa16");
		} catch (RestException e) {
			return null;
		}
	}
}
