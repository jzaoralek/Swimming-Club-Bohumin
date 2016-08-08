package com.jzaoralek.scb.ui.common.template;

import org.zkoss.bind.Validator;

import com.jzaoralek.scb.ui.common.validator.EmailValidator;
import com.jzaoralek.scb.ui.common.validator.NotNullObjectValidator;
import com.jzaoralek.scb.ui.common.validator.NotNullValidator;

/**
 * Provide validators to VM classes. Prevent instance in viewModels.
 * @author P3400343
 *
 */
public class Validators {

	private static final Validator emailValidator = new EmailValidator();
	private static final NotNullValidator notNullValidator = new NotNullValidator();
	private static final NotNullObjectValidator notNullObjectValidator = new NotNullObjectValidator();

	public static Validator getEmailValidator() {
		return emailValidator;
	}
	public static NotNullValidator getNotNullValidator() {
		return notNullValidator;
	}
	public static NotNullObjectValidator getNotNullObjectValidator() {
		return notNullObjectValidator;
	}
}
