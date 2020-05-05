package com.jzaoralek.scb.dataservice.dao;

import java.util.List;

import com.jzaoralek.scb.dataservice.domain.Config;
import com.jzaoralek.scb.dataservice.domain.Config.ConfigCategory;

public interface ConfigurationDao {

	Config getByName(String name);
	List<Config> getAll();
	List<Config> getByCategory(ConfigCategory category);
	void update(Config config);
}
