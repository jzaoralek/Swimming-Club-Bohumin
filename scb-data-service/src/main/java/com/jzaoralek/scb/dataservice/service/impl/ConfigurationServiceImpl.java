package com.jzaoralek.scb.dataservice.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.dao.ConfigurationDao;
import com.jzaoralek.scb.dataservice.domain.Config;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;

@Service("configurationService")
public class ConfigurationServiceImpl implements ConfigurationService {

	public static final String COURSE_YEAR_DELIMITER = "/";
	public static final int ACTUAL_YEAR = Calendar.getInstance().get(Calendar.YEAR);

	@Autowired
	private ConfigurationDao configurationDao;

	@Override
	public String getCourseApplicationYear() {
		Integer courseYear = Integer.valueOf(configurationDao.getByName(Config.ConfigName.COURSE_APPLICATION_YEAR.name()).getValue());
		return String.valueOf(courseYear) + COURSE_YEAR_DELIMITER + String.valueOf(courseYear+1);
	}

	@Override
	public List<Config> getAll() {
		return configurationDao.getAll();
	}

	@Override
	public void update(Config config) {
		configurationDao.update(config);
	}

	@Override
	public boolean isCourseApplicationsAllowed() {
		return Boolean.valueOf(configurationDao.getByName(Config.ConfigName.COURSE_APPLICATION_ALLOWED.name()).getValue());
	}

	@Override
	public Pair<Integer, Integer> getYearFromTo() {
		String[] yearFromToArr = getCourseApplicationYear().split(COURSE_YEAR_DELIMITER);
		return new Pair<Integer, Integer>(Integer.valueOf(yearFromToArr[0]), Integer.valueOf(yearFromToArr[1]));
	}

	@Override
	public List<String> getCourseYearList() {
		List<String> ret = new ArrayList<String>();
		int endLoopYear = ACTUAL_YEAR + 1;
		int year = Integer.valueOf(configurationDao.getByName(Config.ConfigName.COURSE_APPLICATION_YEAR.name()).getValue());
		for (int i = year; i <= endLoopYear; i++) {
			ret.add(year + COURSE_YEAR_DELIMITER + (year + 1));
			year++;
		}
		return ret;
	}
}