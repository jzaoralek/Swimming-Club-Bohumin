package com.sportologic.sprtadmin.vm;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.repository.CustomerConfigRepository;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CustomerConfigVM {

    @WireVariable
    private CustomerConfigRepository customerConfigRepository;

    private List<CustomerConfig> customerConfigList;

    @Init
    public void init() {
        // customerConfigList = customerConfigRepository.findAll();
        customerConfigList = new ArrayList<>();
        long count = customerConfigRepository.count();
        // customerConfigList = customerConfigRepository.findAll();
        customerConfigList = customerConfigRepository.findAllCustom();
    }
    public List<CustomerConfig> getCustomerConfigList() {
        return customerConfigList;
    }
}
