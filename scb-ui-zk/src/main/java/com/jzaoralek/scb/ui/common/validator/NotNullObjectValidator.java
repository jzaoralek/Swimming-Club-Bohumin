package com.jzaoralek.scb.ui.common.validator;

import org.zkoss.bind.ValidationContext;
import org.zkoss.util.resource.Labels;

public class NotNullObjectValidator extends ScbAbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		Object value = ctx.getProperty().getValue();

		if (value == null) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return;
		}
        removeValidationStyle(ctx);
	}
}
