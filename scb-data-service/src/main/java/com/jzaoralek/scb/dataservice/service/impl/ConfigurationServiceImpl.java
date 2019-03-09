package com.jzaoralek.scb.dataservice.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${recaptcha.sitekey}")
    private String recaptchaSitekey;
	
	@Value("${recaptcha.secretkey}")
    private String recaptchaSecredkey;

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
	
	@Override
	public String getOrgName() {
		return configurationDao.getByName(Config.ConfigName.ORGANIZATION_NAME.name()).getValue();
	}

	@Override
	public String getOrgPhone() {
		return configurationDao.getByName(Config.ConfigName.ORGANIZATION_PHONE.name()).getValue();
	}

	@Override
	public String getOrgEmail() {
		return configurationDao.getByName(Config.ConfigName.ORGANIZATION_EMAIl.name()).getValue();
	}

	@Override
	public String getWelcomeInfo() {
		return configurationDao.getByName(Config.ConfigName.WELCOME_INFO.name()).getValue();
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

	@Override
	public boolean isCourseSelectionRequired() {
		return Boolean.valueOf(configurationDao.getByName(Config.ConfigName.COURSE_APPL_SEL_REQ.name()).getValue());
	}

	@Override
	public String getBaseURL() {
		return configurationDao.getByName(Config.ConfigName.BASE_URL.name()).getValue();
	}

	@Override
	public String getHealthAgreement() {
		return configurationDao.getByName(Config.ConfigName.HEALTH_AGREEMENT.name()).getValue();
	}

	@Override
	public String getPersDataProcessAgreement() {
		return configurationDao.getByName(Config.ConfigName.PERSONAL_DATA_PROCESS_AGREEMENT.name()).getValue();
	}

	@Override
	public String getClubRulesAgreement() {
		return configurationDao.getByName(Config.ConfigName.CLUB_RULES_AGREEMENT.name()).getValue();
	}

	@Override
	public String getCourseApplicationEmailSpecText() {
		return configurationDao.getByName(Config.ConfigName.COURSE_APPL_EMAIL_SPEC_TEXT.name()).getValue();
	}

	@Override
	public String getOrgContactPerson() {
		return configurationDao.getByName(Config.ConfigName.ORGANIZATION_CONTACT_PERSON.name()).getValue();
	}

	@Override
	public boolean isPaymentsAvailable() {
		return Boolean.valueOf(configurationDao.getByName(Config.ConfigName.PAYMENTS_AVAILABLE.name()).getValue());
	}

	@Override
	public String getBankAccountNumber() {
		return configurationDao.getByName(Config.ConfigName.ORGANIZATION_BANK_ACCOUNT_NUMBER.name()).getValue();
	}

	@Override
	public boolean isAttendanceForParentsVisible() {
		return Boolean.valueOf(configurationDao.getByName(Config.ConfigName.ATTENDANCE_FOR_PARENTS_VISIBLE.name()).getValue());
	}

	@Override
	public String getRecaptchaSitekey() {
		return this.recaptchaSitekey;
	}

	@Override
	public String getRecaptchaSecredkey() {
		return this.recaptchaSecredkey;
	}
}