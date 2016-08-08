package com.jzaoralek.scb.ui.common.validator;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;

public class EmailValidator extends AbstractValidator {

	public static final String EMAIL_PATTERN = "[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	public void validate(ValidationContext ctx) {
		String value = (String) ctx.getProperty().getValue();
		Boolean notNull = (Boolean)ctx.getBindContext().getValidatorArg("notNull");

		if (notNull != null && notNull && StringUtils.isEmpty(value)) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return;
		}

		Pattern emailPattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
		if (StringUtils.hasText(value) && !emailPattern.matcher(value).matches()) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.invalidaEmail"));
		}
	}

}
