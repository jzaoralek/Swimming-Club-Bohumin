package com.sportologic.sprtadmin.validator;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.repository.CustomerConfigRepository;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zk.ui.select.annotation.WireVariable;

/**
 * Validate if customer name already exists.
 */
public class UniqueCustomerValidator extends AbstractValidator {

    private CustomerConfigRepository customerConfigRepository;

    public UniqueCustomerValidator(CustomerConfigRepository customerConfigRepository) {
        this.customerConfigRepository = customerConfigRepository;
    }

    @Override
    public void validate(ValidationContext validationContext) {
        String custName = (String) validationContext.getProperty().getValue();
        CustomerConfig customerConfig = customerConfigRepository.findByCustName(custName);
        if (customerConfig != null) {
            addInvalidMessage(validationContext, "Instance pro klub " + custName + " již existuje.");
        }
    }
}
