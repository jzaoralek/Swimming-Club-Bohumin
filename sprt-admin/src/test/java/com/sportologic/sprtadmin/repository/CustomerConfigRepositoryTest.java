package com.sportologic.sprtadmin.repository;

import com.sportologic.common.model.domain.CustomerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class CustomerConfigRepositoryTest {

    private static String CUST_ID = "customerId";

    @Autowired
    private CustomerConfigRepository customerConfigRepository;

    @BeforeEach
    public void setUp() {
        CustomerConfig config = new CustomerConfig();
        config.setUuid(UUID.randomUUID());
        config.setCustDefault(false);
        config.setCustId(CUST_ID);
        config.setCustName("Customer name");
        config.setDbPassword("password");
        config.setDbUser("user");
        config.setDbUrl("dburl");
        config.setModifAt(new Date());
        config.setModifBy("modifBy");
        customerConfigRepository.save(config);
    }

    @Test
    void findAllTest() {
        List<CustomerConfig> customerConfigList = customerConfigRepository.findAll();
        Assert.notNull(customerConfigList,"CustomerConfigList is null.");
        Assert.isTrue(customerConfigList.size() == 1, "Incorrect count");
        Assert.isTrue(customerConfigList.get(0).getCustId().equals(CUST_ID), "CustId doesn't equals");
    }
}
