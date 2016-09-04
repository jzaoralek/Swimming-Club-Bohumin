package com.jzaoralek.scb.dataservice.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.service.ConfigurationService;

@Service("configurationService")
public class ConfigurationServiceImpl implements ConfigurationService {

	public static final String COURSE_YEAR_DELIMITER = "/";

	private static List<String> courseYearList = buildCourseList();

	@Override
	public String getCourseApplicationYear() {
		return courseYearList.get(0);
	}

	@Override
	public List<String> getCourseYearList() {
		return courseYearList;
	}

	private static List<String> buildCourseList() {
		List<String> ret = new ArrayList<String>();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 1; i <= 3; i++) {
			ret.add(year + COURSE_YEAR_DELIMITER + (year + 1));
			year++;
		}
		return ret;
	}
}