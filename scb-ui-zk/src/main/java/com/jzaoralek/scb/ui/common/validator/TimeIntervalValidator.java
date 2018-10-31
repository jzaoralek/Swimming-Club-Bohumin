package com.jzaoralek.scb.ui.common.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.util.resource.Labels;

import com.jzaoralek.scb.ui.common.WebConstants;

public class TimeIntervalValidator extends ScbAbstractValidator {

	private static final String MIN_SEC_MILLIS_PATTERN = "mm:ss,SSS";
	private static final String SEC_MILLIS_PATTERN = "ss,SSS";
	private static final String SEC_PATTERN = "ss";

	@Override
	public void validate(ValidationContext ctx) {
		String value = (String) ctx.getProperty().getValue();
		Boolean notNull = (Boolean)ctx.getBindContext().getValidatorArg("notNull");

		if (notNull != null && notNull && StringUtils.isEmpty(value)) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return;
		}
		String pattern = null;
		if (value.indexOf(WebConstants.MINUTE_SECOND_DELIM) >= 0 && value.indexOf(WebConstants.SECOND_MILISECOND_DELIM) >= 0) {
			pattern = MIN_SEC_MILLIS_PATTERN;
		} else if (value.indexOf(WebConstants.MINUTE_SECOND_DELIM) < 0 && value.indexOf(WebConstants.SECOND_MILISECOND_DELIM) >= 0) {
			pattern = SEC_MILLIS_PATTERN;
		} else if (value.indexOf(WebConstants.MINUTE_SECOND_DELIM) < 0 && value.indexOf(WebConstants.SECOND_MILISECOND_DELIM) < 0) {
			pattern = SEC_PATTERN;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			sdf.parse(value);
		} catch (ParseException e) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.invalidFormat"));
            return;
		}
        removeValidationStyle(ctx);
	}
}
