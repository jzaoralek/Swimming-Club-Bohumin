package com.jzaoralek.scb.ui.common.vm;

import org.zkoss.bind.Validator;

import com.jzaoralek.scb.ui.common.template.Validators;

public class BaseVM {

	private static final String DATE_FORMAT = "dd.MM.yyyy";
	private static final int FIRSTNAME_MAXLENGTH = 240;
	private static final int SURNAME_MAXLENGTH = 240;
	private static final int HEALTH_INSURANCE_MAXLENGTH = 240;
	private static final int DATE_MAXLENGTH = 10;
	private static final int BIRTH_NUMBER_MAXLENGTH = 12;
	private static final int EMAIL_MAXLENGTH = 100;
	private static final int PHONE_MAXLENGTH = 14;

	public String getDateFormat() {
		return DATE_FORMAT;
	}
	public static int getFirstnameMaxlength() {
		return FIRSTNAME_MAXLENGTH;
	}
	public static int getSurnameMaxlength() {
		return SURNAME_MAXLENGTH;
	}
	public static int getHealthInsuranceMaxlength() {
		return HEALTH_INSURANCE_MAXLENGTH;
	}
	public static int getDateMaxlength() {
		return DATE_MAXLENGTH;
	}
	public static int getBirthNumberMaxlength() {
		return BIRTH_NUMBER_MAXLENGTH;
	}
	public static int getEmailMaxlength() {
		return EMAIL_MAXLENGTH;
	}
	public static int getPhoneMaxlength() {
		return PHONE_MAXLENGTH;
	}

	public Validator getEmailValidator() {
		return Validators.getEmailValidator();
	}

	public Validator getNotNullValidator() {
		return Validators.getNotNullValidator();
	}

	public Validator getNotNullObjectValidator() {
		return Validators.getNotNullObjectValidator();
	}
}