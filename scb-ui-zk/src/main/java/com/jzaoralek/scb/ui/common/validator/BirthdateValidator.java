package com.jzaoralek.scb.ui.common.validator;

import java.util.Date;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.util.resource.Labels;

import com.jzaoralek.scb.ui.common.utils.WebUtils;

public class BirthdateValidator extends ScbAbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		Date value = (Date) ctx.getProperty().getValue();
		Boolean notNull = (Boolean)ctx.getBindContext().getValidatorArg("notNull");
		
		if (notNull != null && notNull && value == null) {
			// NOT NULL
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return;
		}
		
		Boolean validateBirthNumber = (Boolean)ctx.getBindContext().getValidatorArg("validateBirthNumber");
		String birthNumber = (String)ctx.getBindContext().getValidatorArg("birthNumber");
		
		if (validateBirthNumber != null && validateBirthNumber && StringUtils.hasText(birthNumber)) {
			try {
				String birtNumberPreDelim = birthNumber.substring(0, birthNumber.indexOf("/"));
				Date birthdateFromBirtNumber = WebUtils.parseRcDatePart(birtNumberPreDelim).getValue1();
				if (birthdateFromBirtNumber != null &&  (value.compareTo(birthdateFromBirtNumber) != 0)) {
					super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.birthdateNotEqualsBirthNUmber"));
					return;
				}
			} catch (Exception e) {
				
			}
		}
	}

}
