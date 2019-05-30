package com.jzaoralek.scb.ui.common.validator;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.util.resource.Labels;

import com.jzaoralek.scb.ui.common.WebConstants;

public class EmailValidator extends ScbAbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		String value = (String) ctx.getProperty().getValue();
		Boolean notNull = (Boolean)ctx.getBindContext().getValidatorArg("notNull");

		if (notNull != null && notNull && StringUtils.isEmpty(value)) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return;
		}

		Pattern emailPattern = Pattern.compile(WebConstants.EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
		if (StringUtils.hasText(value) && !emailPattern.matcher(value).matches()) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.invalidaEmail"));
            return;
		}
        removeValidationStyle(ctx);
	}

}
