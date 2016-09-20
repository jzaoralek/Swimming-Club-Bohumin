package com.jzaoralek.scb.ui.common.validator;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;

public class NotNullValidator extends AbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		String value = (String) ctx.getProperty().getValue();

		if (StringUtils.isEmpty(value)) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return;
		}
	}
}
