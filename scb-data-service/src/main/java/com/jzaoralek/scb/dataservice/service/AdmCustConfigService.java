package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.Set;

import com.sportologic.common.model.domain.CustomerConfig;

public interface AdmCustConfigService {

	/**
	 * Get customer config ids as Set.
	 * @return
	 */
	Set<String> getCustCongigIds();
	
	/**
	 * Getting all customers.
	 * @return
	 */
	List<CustomerConfig> getCustomerAll();
	
	/**
	 * Updating customer datasource configuration.
	 * After creating new customer instance.
	 */
	void updateCustomerDSConfiguration();
}
