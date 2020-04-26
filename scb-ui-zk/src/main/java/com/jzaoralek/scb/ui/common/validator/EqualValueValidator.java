package com.jzaoralek.scb.ui.common.validator;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.util.resource.Labels;

public class EqualValueValidator extends ScbAbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		String value = (String) ctx.getProperty().getValue();
		if (StringUtils.isEmpty(value)) {
			addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return;
		}
		
		String compareToValue = (String)ctx.getBindContext().getValidatorArg("compareTo");
		if (!StringUtils.hasText(compareToValue)) {
			removeValidationStyle(ctx);
			return;
		}
		
		if (!compareToValue.equals(value)) {
			addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.ValuesNotEqual"));
			return;
		}

        removeValidationStyle(ctx);
	}
}
