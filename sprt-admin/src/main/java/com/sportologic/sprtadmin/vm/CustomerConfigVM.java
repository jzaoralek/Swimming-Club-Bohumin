package com.sportologic.sprtadmin.vm;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.repository.CustomerConfigRepository;
import com.sportologic.sprtadmin.utils.PasswordGenerator;
import com.sportologic.sprtadmin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

// TODO:
// - validace - unikatnost nazvu klubu
//            - delka nazvu
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CustomerConfigVM {

    // @Value("${database.base.url}")
    // private String dbBaseUrl;

    @WireVariable
    private CustomerConfigRepository customerConfigRepository;

    private List<CustomerConfig> customerConfigList;
    private String custName;

    @Init
    public void init() {
        customerConfigList = customerConfigRepository.findAllCustom();
    }

    @Command
    public void createCustConfigCmd() {

        // System.out.println("***** " + dbBaseUrl + " *****");

        CustomerConfig custConfig = new CustomerConfig();
        String customerId = StringUtils.buildCustId(custName);
        custConfig.setUuid(UUID.randomUUID());
        custConfig.setCustId(customerId);
        custConfig.setCustDefault(false);
        custConfig.setCustName(custName);
        custConfig.setDbUrl("jdbc:mysql://localhost:3306/"+customerId);
        custConfig.setDbUser(customerId);
        custConfig.setDbPassword(PasswordGenerator.generate());
        custConfig.setModifAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        custConfig.setModifBy("SYSTEM");

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
}
