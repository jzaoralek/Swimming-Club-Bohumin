package com.sportologic.sprtadmin.validator;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.repository.CustomerConfigRepository;
import com.sportologic.sprtadmin.service.CustomerConfigService;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;

/**
 * Validate if customer name already exists.
 */
public class UniqueCustomerValidator extends AbstractValidator {

    private CustomerConfigService customerConfigRepository;

    public UniqueCustomerValidator(CustomerConfigService customerConfigRepository) {
        this.customerConfigRepository = customerConfigRepository;
    }

    @Override
    public void validate(ValidationContext validationContext) {
        String custName = (String) validationContext.getProperty().getValue();
        CustomerConfig customerConfig = customerConfigRepository.findCustConfigByName(custName);
        if (customerConfig != null) {
            addInvalidMessage(validationContext, Labels.getLabel("sprt.web.new-instance.msg.warn.instanceExists", new Object[]{custName}));
        }
    }
}
