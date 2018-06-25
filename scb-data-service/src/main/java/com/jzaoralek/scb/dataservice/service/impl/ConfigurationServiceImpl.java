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
	public static final int START_YEAR = 2016;

	@Autowired
	private ConfigurationDao configurationDao;

	@Override
	public String getCourseApplicationYear() {
		return configurationDao.getByName(Config.ConfigName.COURSE_APPLICATION_YEAR.name()).getValue();
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
		return new Pair<>(Integer.valueOf(yearFromToArr[0]), Integer.valueOf(yearFromToArr[1]));
	}

	@Override
	public List<String> getCourseYearList() {
		return getYearList(true);
	}

	@Override
	public List<String> getCourseYearFromActualYearList() {
		return getYearList(true);
	}

	private  List<String> getYearList(boolean fromStartYear) {
		List<String> ret = new ArrayList<>();
		int endLoopYear = ACTUAL_YEAR + 1;
		int startYear = fromStartYear ? START_YEAR : ACTUAL_YEAR;
		for (int i = startYear; i <= endLoopYear; i++) {
			ret.add(startYear + COURSE_YEAR_DELIMITER + (startYear + 1));
			startYear++;
		}
		return ret;
	}
}