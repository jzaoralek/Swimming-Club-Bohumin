package com.sportologic.sprtadmin.vm;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.repository.CustomerConfigRepository;
import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.utils.PasswordGenerator;
import com.sportologic.sprtadmin.utils.StringUtils;
import com.sportologic.sprtadmin.validator.UniqueCustomerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

// TODO:
// - validace - unikatnost nazvu klubu
//            - delka nazvu
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CustomerConfigVM {

    private static final Logger logger = LoggerFactory.getLogger(CustomerConfigVM.class);

    @WireVariable
    private CustomerConfigRepository customerConfigRepository;

    @WireVariable
    private ConfigService configService;

    private UniqueCustomerValidator uniqueCustomerValidator;

    private List<CustomerConfig> customerConfigList;
    private String custName;
    private String dbBaseUrl;

    @Init
    public void init() {
        customerConfigList = customerConfigRepository.findAllCustom();
        dbBaseUrl = configService.getDbBaseUrl();

        uniqueCustomerValidator = new UniqueCustomerValidator(customerConfigRepository);
    }

    @Command
    public void createCustConfigCmd() {
        CustomerConfig custConfig = new CustomerConfig();
        String customerId = StringUtils.buildCustId(custName);
        custConfig.setUuid(UUID.randomUUID());
        custConfig.setCustId(customerId);
        custConfig.setCustDefault(false);
        custConfig.setCustName(custName);
        custConfig.setDbUrl(dbBaseUrl + customerId);
        custConfig.setDbUser(customerId);
        custConfig.setDbPassword(PasswordGenerator.generate());
        custConfig.setModifAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        custConfig.setModifBy("SYSTEM");

        logger.info("Creating new customer: {}", custName);
        customerConfigRepository.save(custConfig);
        customerConfigRepository.create_db_user(custConfig.getCustId(),
                                                custConfig.getDbUser(),
                                                custConfig.getDbPassword());
    }

    public List<CustomerConfig> getCustomerConfigList() {
        return customerConfigList;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public UniqueCustomerValidator getUniqueCustomerValidator() {
        return uniqueCustomerValidator;
    }
}
