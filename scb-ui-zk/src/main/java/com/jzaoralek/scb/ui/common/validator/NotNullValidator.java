package com.jzaoralek.scb.ui.common.validator;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.util.resource.Labels;

public class NotNullValidator extends ScbAbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		String value = (String) ctx.getProperty().getValue();

		Object required = ctx.getValidatorArg("required");
		// dynamicka povinnost
		if (required  != null && required instanceof Boolean && !(Boolean)required) {
			return;
		}
		
		if (StringUtils.isEmpty(value)) {
            addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return;
		}

        removeValidationStyle(ctx);
	}
}
