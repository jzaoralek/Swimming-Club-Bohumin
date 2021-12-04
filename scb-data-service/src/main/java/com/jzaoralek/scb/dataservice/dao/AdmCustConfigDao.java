package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CustomerConfig;

public interface AdmCustConfigDao {

	List<CustomerConfig> getAll();
	CustomerConfig getByUuid(UUID uuid);
	CustomerConfig getByCustId(String custId);
}
