package com.sportologic.sprtadmin.controller;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.dto.CustConfigDto;
import com.sportologic.sprtadmin.service.CustomerConfigService;
import com.sportologic.sprtadmin.vo.DBInitData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for create customer instance, list instances etc.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerConfigController {

    @Autowired
    private CustomerConfigService customerConfigService;

    /**
     * Create new customer instance.
     * @param custConfigDto
     */
    @PostMapping("/cust-config-create")
    void createCustConfig(@RequestBody CustConfigDto custConfigDto) {
        CustomerConfig custConfig = null;
        DBInitData dbInitData = null;
        customerConfigService.createCustomerInstance(custConfig, dbInitData);
    }
}
