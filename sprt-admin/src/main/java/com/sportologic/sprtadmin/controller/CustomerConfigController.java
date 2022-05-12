package com.sportologic.sprtadmin.controller;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.dto.CustConfigDto;
import com.sportologic.sprtadmin.dto.CustConfigResp;
import com.sportologic.sprtadmin.dto.RestResponse;
import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.service.CustomerConfigService;
import com.sportologic.sprtadmin.service.impl.CustomerConfigServiceImpl;
import com.sportologic.sprtadmin.vo.DBInitData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CustomerConfigController.class);

    @Autowired
    private CustomerConfigService customerConfigService;

    @Autowired
    private ConfigService configService;

    /**
     * Create new customer instance.
     * @param custConfigDto
     */
    @PostMapping("/cust-config-create")
    public RestResponse<CustConfigResp> createCustConfig(@RequestBody CustConfigDto custConfigDto) {
        logger.info("Creating new customer instance, customer name: {}", custConfigDto.getCustName());
        RestResponse<CustConfigResp> ret = new RestResponse();
        try {
            DBInitData dbInitData = new DBInitData();
            dbInitData.setConfigOrgName(custConfigDto.getCustName());
            dbInitData.setConfigOrgEmail(custConfigDto.getCustEmail());
            dbInitData.setConfigOrgPhone("");
            dbInitData.setAdmContactFirstname(custConfigDto.getAdmFirstname());
            dbInitData.setAdmContactSurname(custConfigDto.getAdmSurname());
            dbInitData.setAdmContactEmail(custConfigDto.getAdmEmail());
            dbInitData.setAdmContactPhone("");

            // TODO: validate unique custName and custId, vratit ValidationException a WARN do RestResponse
            dbInitData = customerConfigService.createCustomerInstance(dbInitData);

            // build response object
            CustConfigResp custConfResp = new CustConfigResp();
            custConfResp.setCustInstanceUrl(dbInitData.getConfigBaseUrl());
            custConfResp.setCustEmail(dbInitData.getConfigOrgEmail());
            custConfResp.setAdmUsername(dbInitData.getAdmUsername());
            custConfResp.setAdmPassword(dbInitData.getAdmPassword());
            ret.setObject(custConfResp);
            ret.setState(RestResponse.RestResponseState.OK);

            logger.info("New customer instance successfully created, customer name: {}", custConfigDto.getCustName());
        } catch (Exception e) {
            logger.error("Unexpected exception during customer instance, customer name: {}", custConfigDto.getCustName(), e);
            ret.setState(RestResponse.RestResponseState.ERROR);
            ret.setMessage(e.getMessage());
        }

        return ret;
    }
}
