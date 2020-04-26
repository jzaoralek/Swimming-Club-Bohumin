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
	private static final TimeIntervalValidator timeIntervalValidator = new TimeIntervalValidator();
	private static final PasswordValidator passwordValidator = new PasswordValidator();
	private static final SexMaleValidator sexMaleValidator = new SexMaleValidator();
	private static final EqualValueValidator equalValueValidator = new EqualValueValidator();
	
	public static SexMaleValidator getSexmalevalidator() {
		return sexMaleValidator;
	}
	public static Validator getEmailValidator() {
		return emailValidator;
	}
	public static NotNullValidator getNotNullValidator() {
		return notNullValidator;
	}
	public static NotNullObjectValidator getNotNullObjectValidator() {
		return notNullObjectValidator;
	}
	public static TimeIntervalValidator getTimeintervalvalidator() {
		return timeIntervalValidator;
	}
	public static PasswordValidator getPasswordvalidator() {
		return passwordValidator;
	}
	public static EqualValueValidator getEqualValueValidator() {
		return equalValueValidator;
	}
}