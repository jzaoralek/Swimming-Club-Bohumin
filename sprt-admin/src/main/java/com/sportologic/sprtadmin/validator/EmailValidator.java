package com.sportologic.sprtadmin.validator;

import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.util.resource.Labels;

import java.util.regex.Pattern;

public class EmailValidator extends SprtAbstractValidator {

    private static final String EMAIL_PATTERN = "[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public void validate(ValidationContext validationContext) {
        String value = (String) validationContext.getProperty().getValue();
        Boolean notNull = (Boolean)validationContext.getBindContext().getValidatorArg("required");

        // Not null
        if (notNull != null && notNull && !StringUtils.hasText(value)) {
            addRequiredValueMsg(validationContext);
            return;
        }
        // Format
        Pattern emailPattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
        if (StringUtils.hasText(value) && !emailPattern.matcher(value).matches()) {
            addInvalidMessage(validationContext, Labels.getLabel("sprt.web.common.msg.warn.invalidEmail"));
        }
    }
}
