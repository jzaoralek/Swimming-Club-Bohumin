package com.jzaoralek.scb.ui.common.validator;

import org.zkoss.bind.Validator;

/**
 * Provide validators to VM classes. Prevent instance in viewModels.
 * @author P3400343
 *
 */
public final class Validators {

	private Validators() {}

	private static final Validator emailValidator = new EmailValidator();
	private static final NotNullValidator notNullValidator = new NotNullValidator();
	private static final NotNullObjectValidator notNullObjectValidator = new NotNullObjectValidator();
	private static final BirthNumberValidator birthNumberValidator = new BirthNumberValidator();
	private static final TimeIntervalValidator timeIntervalValidator = new TimeIntervalValidator();
	private static final PasswordValidator passwordValidator = new PasswordValidator();
	
	public static Validator getEmailValidator() {
		return emailValidator;
	}
	public static NotNullValidator getNotNullValidator() {
		return notNullValidator;
	}
	public static NotNullObjectValidator getNotNullObjectValidator() {
		return notNullObjectValidator;
	}
	public static BirthNumberValidator getBirthnumbervalidator() {
		return birthNumberValidator;
	}
	public static TimeIntervalValidator getTimeintervalvalidator() {
		return timeIntervalValidator;
	}
	public static PasswordValidator getPasswordvalidator() {
		return passwordValidator;
	}
}