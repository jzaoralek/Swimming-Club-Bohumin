package com.sportologic.sprtadmin.validator;

import com.sportologic.common.model.domain.CustomerConfig;
import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;

public class NotEmptyStringValidator extends SprtAbstractValidator {

    @Override
    public void validate(ValidationContext validationContext) {
        String value = (String) validationContext.getProperty().getValue();
        if (!StringUtils.hasText(value)) {
            addRequiredValueMsg(validationContext);
        }
    }
}
