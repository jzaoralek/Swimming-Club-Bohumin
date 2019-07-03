package com.sportologic.ruianclient.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sportologic.ruianclient.client.RuianClient;
import com.sportologic.ruianclient.model.RuianRegion;
import com.sportologic.ruianclient.service.RuianService;

import webtools.rest.exception.RestException;

@Service("ruianServiceRest")
public class RuianServiceRestImpl implements RuianService {

	private RuianClient ruianClient = new RuianClient();
	
	public List<RuianRegion> getRegionList() {
		try {
			return ruianClient.getRegionList();
		} catch (RestException e) {
			return null;
		}
	}
}
