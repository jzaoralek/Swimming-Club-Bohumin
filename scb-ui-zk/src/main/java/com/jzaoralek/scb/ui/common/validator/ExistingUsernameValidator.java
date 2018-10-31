package com.jzaoralek.scb.ui.common.validator;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.util.resource.Labels;

import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.service.ScbUserService;

/**
 * Validate if username exists.
 *
 */
public class ExistingUsernameValidator extends ScbAbstractValidator {

	private ScbUserService scbUserService;
	
	public ExistingUsernameValidator(ScbUserService scbUserService) {
		this.scbUserService = scbUserService;
	}
	
	@Override
	public void validate(ValidationContext ctx) {
		String value = (String) ctx.getProperty().getValue();
		Boolean notNull = (Boolean)ctx.getBindContext().getValidatorArg("notNull");

		if (notNull != null && notNull && StringUtils.isEmpty(value)) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return;
		}
		
		final ScbUser scbUser = scbUserService.getByUsername(value);
		if (scbUser == null) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.usernameNotExists"));
            return;
		}
        removeValidationStyle(ctx);
	}
}
