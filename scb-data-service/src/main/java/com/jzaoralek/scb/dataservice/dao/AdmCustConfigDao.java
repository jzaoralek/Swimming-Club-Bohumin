package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.sportologic.common.model.domain.CustomerConfig;


public interface AdmCustConfigDao {

	List<CustomerConfig> getAll();
	CustomerConfig getByUuid(UUID uuid);
	CustomerConfig getByCustId(String custId);
	CustomerConfig getDefault();
}
