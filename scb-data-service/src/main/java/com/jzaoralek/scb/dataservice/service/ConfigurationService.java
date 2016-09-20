package com.jzaoralek.scb.dataservice.service;

import java.util.List;

import org.javatuples.Pair;

import com.jzaoralek.scb.dataservice.domain.Config;

public interface ConfigurationService {

	String getCourseApplicationYear();
	Pair<Integer,Integer> getYearFromTo();
	List<String> getCourseYearList();
	List<String> getCourseYearFromActualYearList();
	boolean isCourseApplicationsAllowed();
	List<Config> getAll();
	void update(Config config);
}
