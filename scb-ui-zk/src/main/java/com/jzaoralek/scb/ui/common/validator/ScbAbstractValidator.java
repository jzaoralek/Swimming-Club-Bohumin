package com.jzaoralek.scb.ui.common.validator;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;

import com.jzaoralek.scb.ui.common.utils.ComponentUtils;

/**
 * Project: scb-ui-zk
 *
 * Created: 31. 10. 2018
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public abstract class ScbAbstractValidator extends AbstractValidator {

    private static final String VALIDATION_CLASS = "scb-input-invalid";
    private static final String INVALID_INPUT_SCLASS = "scb-z-validation-error-input";

    /**
     * Přidá na komponentu validační styl (css třídu)
     * 
     */
    @Override
    protected void addInvalidMessage(ValidationContext ctx, String message) {
        Component comp = ctx.getBindContext().getComponent();
        if (comp instanceof HtmlBasedComponent) {
            HtmlBasedComponent htmlBased = (HtmlBasedComponent) comp;
            ComponentUtils.addSclass(htmlBased, VALIDATION_CLASS);
            ComponentUtils.addSclass(htmlBased, INVALID_INPUT_SCLASS);
        }

        super.addInvalidMessage(ctx, message);
    }

    /**
     * Odstraní z komponenty validační styl
     * 
     * @param ctx
     */
    protected void removeValidationStyle(ValidationContext ctx) {
        Component comp = ctx.getBindContext().getComponent();
        if (comp instanceof HtmlBasedComponent) {
            HtmlBasedComponent htmlBased = (HtmlBasedComponent) comp;
            ComponentUtils.removeSclass(htmlBased, VALIDATION_CLASS);
            ComponentUtils.removeSclass(htmlBased, INVALID_INPUT_SCLASS);
        }
    }
}
