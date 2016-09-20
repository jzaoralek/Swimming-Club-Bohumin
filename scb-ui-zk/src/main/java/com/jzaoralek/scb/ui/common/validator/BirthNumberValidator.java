package com.jzaoralek.scb.ui.common.validator;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;

public class BirthNumberValidator extends AbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		String value = (String) ctx.getProperty().getValue();
		Boolean notNull = (Boolean)ctx.getBindContext().getValidatorArg("notNull");

		String valueWithoutDelim = value.replace("/", "").replace("_", "");

		if (notNull != null && notNull && StringUtils.isEmpty(value)) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return;
		}


		try {
			Long.valueOf(valueWithoutDelim);
		} catch (NumberFormatException e) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.invalidaBirthNumber"));
		}
	}
}
