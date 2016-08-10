package com.jzaoralek.scb.ui.common.vm;

import org.zkoss.bind.Converter;
import org.zkoss.bind.Validator;

import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.converter.Converters;
import com.jzaoralek.scb.ui.common.validator.Validators;

public class BaseVM {

	public String getDateFormat() {
		return WebConstants.DATE_FORMAT;
	}
	public static int getFirstnameMaxlength() {
		return WebConstants.FIRSTNAME_MAXLENGTH;
	}
	public static int getSurnameMaxlength() {
		return WebConstants.SURNAME_MAXLENGTH;
	}
	public static int getHealthInsuranceMaxlength() {
		return WebConstants.HEALTH_INSURANCE_MAXLENGTH;
	}
	public static int getDateMaxlength() {
		return WebConstants.DATE_MAXLENGTH;
	}
	public static int getBirthNumberMaxlength() {
		return WebConstants.BIRTH_NUMBER_MAXLENGTH;
	}
	public static int getEmailMaxlength() {
		return WebConstants.EMAIL_MAXLENGTH;
	}
	public static int getPhoneMaxlength() {
		return WebConstants.PHONE_MAXLENGTH;
	}
	public static int getHealthInfoMaxlength() {
		return WebConstants.HEALTH_INFO_MAXLENGTH;
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

	public Validator getBirthNumberValidator() {
		return Validators.getBirthnumbervalidator();
	}

	public Converter getDateConverter() {
		return Converters.getDateconverter();
	}
}