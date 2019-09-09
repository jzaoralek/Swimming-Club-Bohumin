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
	boolean isCourseSelectionRequired();
	String getOrgName();
	String getOrgPhone();
	String getOrgEmail();
	String getWelcomeInfo();
	String getBaseURL();
	String getHealthAgreement();
	String getPersDataProcessAgreement();
	String getClubRulesAgreement();
	String getCourseApplicationEmailSpecText();
	String getOrgContactPerson();
	String getBankAccountNumber();
	boolean isPaymentsAvailable();
	boolean isAttendanceForParentsVisible();
	boolean isCourseApplNotVerifiedAddressAllowed();
	List<Config> getAll();
	void update(Config config);
	
	/**
	 * Return recaptcha.sitekey from property file;
	 * @param key
	 * @return
	 */
	String getRecaptchaSitekey();
	
	/**
	 * Return recaptcha.secretkey from property file;
	 * @param key
	 * @return
	 */
	String getRecaptchaSecredkey();
}
