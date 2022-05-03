package com.sportologic.sprtadmin.validator;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;

public abstract class SprtAbstractValidator extends AbstractValidator {

    /**
     * Add required value message to context.
     * @param ctx
     */
    protected void addRequiredValueMsg(ValidationContext ctx) {
        addInvalidMessage(ctx, Labels.getLabel("sprt.web.common.msg.warn.requiredValue"));
    }
}
