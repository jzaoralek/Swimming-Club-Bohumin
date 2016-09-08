package com.jzaoralek.scb.dataservice.service;

import java.util.List;

import org.javatuples.Pair;

public interface ConfigurationService {

	String getCourseApplicationYear();
	Pair<Integer,Integer> getYearFromTo();
	List<String> getCourseYearList();
}
