package com.jzaoralek.scb.ui.common.validator;

import org.springframework.util.StringUtils;
import org.zkforge.bwcaptcha.Captcha;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;

public class CaptchaValidator extends AbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		String value = (String) ctx.getProperty().getValue();
		Captcha capt = (Captcha)ctx.getBindContext().getValidatorArg("catpt");
		Boolean validate = (Boolean)ctx.getBindContext().getValidatorArg("validate");

		if (!validate) {
			return;
		}

		if (StringUtils.isEmpty(value)) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return;
		}

		if(!capt.getValue().equals(value)) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.WrongValue"));
		}
	}
}
