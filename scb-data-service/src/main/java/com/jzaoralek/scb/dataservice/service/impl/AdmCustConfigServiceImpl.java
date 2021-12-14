package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.dao.AdmCustConfigDao;
import com.jzaoralek.scb.dataservice.domain.CustomerConfig;
import com.jzaoralek.scb.dataservice.service.AdmCustConfigService;

@Service("admCustConfigService")
public class AdmCustConfigServiceImpl implements AdmCustConfigService {

	@Autowired
	private AdmCustConfigDao admCustConfigDao;
	
	@Override
	public Set<String> getCustCongigIds() {
		List<CustomerConfig> custConfigList = admCustConfigDao.getAll();
		return custConfigList.stream()
							.map(CustomerConfig::getCustId)
							.collect(Collectors.toSet());
	}
	
	@Override
	public List<CustomerConfig> getCustomerAll() {
		return admCustConfigDao.getAll();
	}

}
