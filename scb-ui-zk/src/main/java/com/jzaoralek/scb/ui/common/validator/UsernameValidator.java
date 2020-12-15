package com.jzaoralek.scb.ui.common.validator;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;

import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

/**
 * Validation of username, must be not null,  in email format nad unique.
 * @author jakub.zaoralek
 *
 */
public class UsernameValidator extends ScbAbstractValidator {

private ScbUserService scbUserService;
	
	public UsernameValidator(ScbUserService scbUserService) {
		this.scbUserService = scbUserService;
	}
	
	@Override
	public void validate(ValidationContext ctx) {
		Object validate = ctx.getValidatorArg("validate");
		// dynamic validation
		if (validate != null && validate instanceof Boolean && !(Boolean)validate) {
			return;
		}
		
		// zobrazeni doplnujiciho warningu v messageBoxu, default true
		Boolean showMessageBoxWarning = Boolean.TRUE; 
		Object showMessageBoxWarningObj = ctx.getValidatorArg("showMessageBoxWarning");
		if (showMessageBoxWarningObj != null && showMessageBoxWarningObj instanceof Boolean) {
			showMessageBoxWarning = (Boolean)showMessageBoxWarningObj;
		}
		
		String value = (String) ctx.getProperty().getValue();
		if (value != null) {
			// remove whitespaces from input string
			value = value.trim();
		}

		if (StringUtils.isEmpty(value)) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.valueRequired"));
			return;
		}

		if (!WebUtils.emailValid(value)) {
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.invalidaEmail"));
            return;
		}
		
		final ScbUser scbUser = scbUserService.getByUsername(value);
		if (scbUser != null) {
			if (showMessageBoxWarning) {
				String question = Labels.getLabel("msg.ui.quest.participantRepresentativeExists", 
						new Object[] {value, scbUser.getContact().getCompleteName()});			
				Messagebox.show(question, Labels.getLabel("txt.ui.common.warning"), 
						Messagebox.OK, 
						Messagebox.EXCLAMATION);				
			}
			super.addInvalidMessage(ctx, Labels.getLabel("msg.ui.validation.err.notUniqueUsername"));
            return;
		}
		
        removeValidationStyle(ctx);
	}
}