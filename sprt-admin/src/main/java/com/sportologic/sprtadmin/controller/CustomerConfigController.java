package com.sportologic.sprtadmin.controller;

import com.sportologic.sprtadmin.dto.CustConfigDto;
import com.sportologic.sprtadmin.dto.CustConfigResp;
import com.sportologic.sprtadmin.dto.RestResponse;
import com.sportologic.sprtadmin.exception.SprtValidException;
import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.service.CustomerConfigService;
import com.sportologic.sprtadmin.service.ReCaptchaService;
import com.sportologic.sprtadmin.utils.SprtAdminUtils;
import com.sportologic.sprtadmin.vo.DBInitData;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * REST API for create customer instance, list instances etc.
 */
@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "https://admin.sportologic.cz")
public class CustomerConfigController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerConfigController.class);
    private Locale localeCZ = new Locale("cs","CZ");

    @Autowired
    private CustomerConfigService customerConfigService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ReCaptchaService reCaptchaService;

    /**
     * Create new customer instance.
     * @param custConfigDto
     */
    @PostMapping("/cust-config-create")
    public ResponseEntity<CustConfigResp> createCustConfig(@RequestBody CustConfigDto custConfigDto,
                                                           @RequestParam(name="g-recaptcha-response") String recaptchaResponse) throws SprtValidException {
        logger.info("Creating new customer instance, customer name: {}", custConfigDto.getCustName());

        // reCaptcha validation
        LinkedHashMap result = reCaptchaService.verify(recaptchaResponse);
        if (!Boolean.parseBoolean(result.get("success").toString())){
            String errorCodes = result.get("error-codes").toString();
            logger.error("ReCaptcha validation failed error-codes: {}", errorCodes);
            throw new SprtValidException(String.format("ReCaptcha validation failed error-codes: {}", errorCodes));
        }

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
    @PostMapping("/cust-url")
    public ResponseEntity<CustConfigResp> getCustUrl(@RequestBody CustConfigDto custConfigDto) throws SprtValidException {
        customerConfigService.validateUniqueCustName(custConfigDto.getCustName(), localeCZ);

        String sprtBaseUrl = configService.getSprtBaseUrl();
        String custId = SprtAdminUtils.normToLowerCaseWithoutCZChars(custConfigDto.getCustName());

        // build response object
        CustConfigResp custConfResp = new CustConfigResp();
        custConfResp.setCustInstanceUrl(sprtBaseUrl + custId);

        return new ResponseEntity<CustConfigResp>(custConfResp, HttpStatus.OK);
    }
}
