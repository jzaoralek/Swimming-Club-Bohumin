package com.jzaoralek.scb.dataservice.service.impl;

import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.service.ConfigurationService;

@Service("configurationService")
public class ConfigurationServiceImpl implements ConfigurationService {

	@Override
	public String getCourseApplicationYear() {
		return "2016";
	}
}