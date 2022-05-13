package com.sportologic.sprtadmin.controller;

import com.sportologic.sprtadmin.dto.CustConfigDto;
import com.sportologic.sprtadmin.dto.CustConfigResp;
import com.sportologic.sprtadmin.dto.RestResponse;
import com.sportologic.sprtadmin.exception.SprtValidException;
import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.service.CustomerConfigService;
import com.sportologic.sprtadmin.vo.DBInitData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

/**
 * REST API for create customer instance, list instances etc.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerConfigController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerConfigController.class);
    private Locale localeCZ = new Locale("cs","CZ");

    @Autowired
    private CustomerConfigService customerConfigService;

    @Autowired
    private ConfigService configService;

    /**
     * Create new customer instance.
     * @param custConfigDto
     */
    @PostMapping("/cust-config-create")
    public ResponseEntity<CustConfigResp> createCustConfig(@RequestBody CustConfigDto custConfigDto) throws SprtValidException {
        logger.info("Creating new customer instance, customer name: {}", custConfigDto.getCustName());

        DBInitData dbInitData = new DBInitData();
        dbInitData.setConfigOrgName(custConfigDto.getCustName());
        dbInitData.setConfigOrgEmail(custConfigDto.getCustEmail());
        dbInitData.setConfigOrgPhone("");
        dbInitData.setAdmContactFirstname(custConfigDto.getAdmFirstname());
        dbInitData.setAdmContactSurname(custConfigDto.getAdmSurname());
        dbInitData.setAdmContactEmail(custConfigDto.getAdmEmail());
        dbInitData.setAdmContactPhone("");

        dbInitData = customerConfigService.createCustomerInstance(dbInitData, localeCZ);

        logger.info("New customer instance successfully created, customer name: {}", custConfigDto.getCustName());

        // build response object
        CustConfigResp custConfResp = new CustConfigResp();
        custConfResp.setCustInstanceUrl(dbInitData.getConfigBaseUrl());
        custConfResp.setCustEmail(dbInitData.getConfigOrgEmail());
        custConfResp.setAdmUsername(dbInitData.getAdmUsername());
        custConfResp.setAdmPassword(dbInitData.getAdmPassword());

        return new ResponseEntity<CustConfigResp>(custConfResp, HttpStatus.OK);
    }

    /**
     * Validation of customer name.
     */
    @GetMapping("/cust-name-validate/{custName}")
    public ResponseEntity<String> validateCustName(@PathVariable("custName") String custName) throws SprtValidException {
        customerConfigService.validateUniqueCustName(custName, localeCZ);

        return new ResponseEntity<String>("Alles gute", HttpStatus.OK);
    }
}
