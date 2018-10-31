package com.jzaoralek.scb.ui.common.validator;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.util.resource.Labels;

import com.jzaoralek.scb.dataservice.utils.SecurityUtils;

public class PasswordValidator extends ScbAbstractValidator {

	private static final int MIN_PASSWORD_LENGTH = 6;
	private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9])");
	private static final Pattern CAPITAL_LETTER_PATTERN = Pattern.compile("([A-Z])");

	public enum PasswordValidatorMode {
		/**
		 * Original password - not null and match orig password.
		 */
		ORIG_PWD,
		/**
		 * Compare two values of new password, both not null.
		 */
		REPEAT_PWD_MATCH,
		/**
		 *
		 */
		PWD_QUALITY;
	}

	@Override
	public void validate(ValidationContext ctx) {
		String modeStr = (String)ctx.getBindContext().getValidatorArg("mode");
		if (!StringUtils.hasText(modeStr)) {
			throw new IllegalArgumentException("mode is null");
		}

		String value = (String)ctx.getProperty().getValue();
		PasswordValidatorMode mode = PasswordValidatorMode.valueOf(modeStr);

		if (!validateNotNull(ctx, value)) {
			return;
		}

		switch (mode) {
			case ORIG_PWD:
				if (!value.equals(SecurityUtils.getLoggedUser().getPassword())) {
					super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.WrongValue"));
					return;
				}
				break;
			case REPEAT_PWD_MATCH:
				String value2 = (String)ctx.getBindContext().getValidatorArg("compareValue");
				if (!value.equals(value2)) {
					super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.ValuesNotEqual"));
					return;
				}
				break;
			case PWD_QUALITY:
				// change then previous one
				if (value.equals(SecurityUtils.getLoggedUser().getPassword())) {
					super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.passwordNotDifferentThanPreviousOne"));
					return;
				// minimum length 6
				} else if (value.length() < MIN_PASSWORD_LENGTH) {
					super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.minPasswordlength", new Object[] {MIN_PASSWORD_LENGTH}));
					return;
				}
				// at least one number
				if (!NUMBER_PATTERN.matcher(value).find()) {
					super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.notContainsNumber"));
					return;
				}
				// at least one capital
				if (!CAPITAL_LETTER_PATTERN.matcher(value).find()) {
					super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.notContainsCapitalLetter"));
					return;
				}

				break;
			default: throw new IllegalArgumentException("Unsupported PasswordValidatorMode: " + mode);
		}
        removeValidationStyle(ctx);
	}

	private boolean validateNotNull(ValidationContext ctx, String value) {
		if (StringUtils.isEmpty(value)) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return false;
		}

		return true;
	}
}
