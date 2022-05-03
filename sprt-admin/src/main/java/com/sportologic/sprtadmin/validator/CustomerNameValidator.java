package com.sportologic.sprtadmin.validator;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.repository.CustomerConfigRepository;
import com.sportologic.sprtadmin.service.CustomerConfigService;
import com.sportologic.sprtadmin.utils.SprtAdminUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;

/**
 * Validate customer name not null, if name or custId already exists.
 */
public class CustomerNameValidator extends SprtAbstractValidator {

    private CustomerConfigService customerConfigRepository;

    public CustomerNameValidator(CustomerConfigService customerConfigRepository) {
        this.customerConfigRepository = customerConfigRepository;
    }

    @Override
    public void validate(ValidationContext validationContext) {
        String custName = (String) validationContext.getProperty().getValue();

        // Not null
        if (!StringUtils.hasText(custName)) {
            addRequiredValueMsg(validationContext);
            return;
        }

        CustomerConfig customerConfig = customerConfigRepository.findCustConfigByName(custName);
        if (customerConfig != null) {
            addInvalidMessage(validationContext, Labels.getLabel("sprt.web.new-instance.msg.warn.instanceExists", new Object[]{custName}));
            return;
        }

        String custId = SprtAdminUtils.normToLowerCaseWithoutCZChars(custName);
        customerConfig = customerConfigRepository.findCustConfigByCustId(custId);
        if (customerConfig != null) {
            addInvalidMessage(validationContext, Labels.getLabel("sprt.web.new-instance.msg.warn.instanceExists", new Object[]{custName}));
        }
    }
}
