package com.jzaoralek.scb.dataservice.dao;

import java.util.List;

import com.jzaoralek.scb.dataservice.domain.Config;

public interface ConfigurationDao {

	Config getByName(String name);
	List<Config> getAll();
	void update(Config config);
}
