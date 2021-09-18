package com.jzaoralek.scb.ui.common.validator;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.util.resource.Labels;

import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

public class SexMaleValidator extends ScbAbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		Boolean value = (Boolean) ctx.getProperty().getValue();
		
		Boolean validateBirthNumber = (Boolean)ctx.getBindContext().getValidatorArg("validateBirthNumber");
		String birthNumber = (String)ctx.getBindContext().getValidatorArg("birthNumber");
		
		if (validateBirthNumber != null && validateBirthNumber && StringUtils.hasText(birthNumber)) {
			if (!birthNumber.contains(WebConstants.BIRTH_NO_DELIM)) {
				super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.invalidaBirthNumber"));
				return;
			}
			try {
				String birtNumberPreDelim = birthNumber.substring(0, birthNumber.indexOf(WebConstants.BIRTH_NO_DELIM));				
				Boolean sexMaleFromBirtNumber = WebUtils.parseRcDatePart(birtNumberPreDelim).getValue0();
				if (sexMaleFromBirtNumber != null &&  !sexMaleFromBirtNumber.equals(value)) {
					super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.sexNotEqualsBirthNUmber"));
				}
			} catch (IllegalArgumentException e) {
				super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.sexNotEqualsBirthNUmber"));
			}
		}
	}
}