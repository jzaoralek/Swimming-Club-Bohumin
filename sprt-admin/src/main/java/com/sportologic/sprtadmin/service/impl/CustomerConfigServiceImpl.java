package com.sportologic.sprtadmin.service.impl;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.repository.CustomerConfigRepository;
import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.service.CustomerConfigService;
import com.sportologic.sprtadmin.service.VasServerRestApiClientService;
import com.sportologic.sprtadmin.utils.PasswordGenerator;
import com.sportologic.sprtadmin.utils.shell.ShellScriptRunner;
import com.sportologic.sprtadmin.utils.shell.ShellScriptVO;
import com.sportologic.sprtadmin.vo.DBCredentials;
import com.sportologic.sprtadmin.vo.DBInitData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service("customerConfigService")
public class CustomerConfigServiceImpl implements CustomerConfigService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerConfigServiceImpl.class);

    private static final String DB_SCRIPT_CREATE_OBJECTS = "002-init-cust-db-objects.sh";
    private static final String DB_SCRIPT_CREATE_DATA = "003-init-cust-db-data.sh";

    @Autowired
    private CustomerConfigRepository customerConfigRepository;

    @Autowired
    private ConfigService configService;

    @Autowired
    private VasServerRestApiClientService vasServerRestApiClientService;

    @Autowired
    private SprtRestApiClientServiceImpl sprtRestApiClientService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<CustomerConfig> getCustConfigAll() {
        return customerConfigRepository.findAllCustom();
    }

    @Override
    public CustomerConfig findCustConfigByName(String name) {
        return customerConfigRepository.findByCustName(name);
    }

    @Override
    public CustomerConfig findCustConfigByCustId(String custId) {
        return customerConfigRepository.findByCustId(custId);
    }

    @Override
    public void createCustomerInstance(CustomerConfig custConfig, DBInitData dbInitData) {
        String custName = custConfig.getCustName();
        try {
            logger.info("Creating new instance for customer: {}", custName);
            String dbScriptSrcFolder = configService.getDbScriptSrcFolder();
            String sprtBaseUrl = configService.getSprtBaseUrl();
            String customerId = custConfig.getCustId();

            // Creating new customer configuration
            customerConfigRepository.save(custConfig);

            DBCredentials dbCred = new DBCredentials(custConfig.getDbUser(),
                    custConfig.getDbPassword(),
                    custConfig.getCustId());

            // Creating new DB schema and user
            customerConfigRepository.create_db_user(dbCred.getSchema(),
                    dbCred.getUsername(),
                    dbCred.getPassword());

            // Creating customer email account
            String smtpPwd =  PasswordGenerator.generate();
            String smtpUser = vasServerRestApiClientService.createEmail(customerId, smtpPwd);
            dbInitData.setConfigSmtpUser(smtpUser);
            dbInitData.setConfigSmptPwd(smtpPwd);

            // Creating DB objects
            createDbObjects(dbCred, custName, dbScriptSrcFolder);

            // Creating Init DB data
            dbInitData.setConfigOrgName(custName);
            dbInitData.setConfigWelcomeInfo("Vítejte na stránkách klubu " + custName);
            dbInitData.setConfigBaseUrl(sprtBaseUrl + customerId);
            createDbData(dbCred, dbInitData, dbScriptSrcFolder);

            logger.info("New instance for customer: {} successfully created.", custName);
        } catch (Exception e) {
            logger.error("Unexpected exception caught during creating new instance for customer: {}", custName, e);
            // TODO: revert all actions
            throw new RuntimeException(e);
        }

        try {
            // Calling sportologic app to reload customer DS config to add new instance.
            sprtRestApiClientService.reloadCustDbConfig();
        } catch (Exception e) {
            logger.error("Unexpected exception caught reloading customer datasource config on Sportologic for customer: {}", custName, e);
            // probably sportologic.cz is down, changes will be applied after startup, no action is neeced
        }
    }

    @Override
    public void deleteCustomerInstance() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void activateCustomer(boolean activate) {
        throw new RuntimeException("Not implemented yet");
    }

    /**
     * Create DB objects.
     * @param dbCred
     */
    private void createDbObjects(DBCredentials dbCred, String custName, String dbScriptSrcFolder) {
        try {
            logger.info("Creating db objects for customer: {}", custName);
            ShellScriptVO shScript = new ShellScriptVO(dbScriptSrcFolder, DB_SCRIPT_CREATE_OBJECTS);
            shScript.addArg(dbCred.getUsername());
            shScript.addArg(dbCred.getPassword());
            shScript.addArg(dbCred.getSchema());
            shScript.addArg(dbScriptSrcFolder);

            ShellScriptRunner shRunner = new ShellScriptRunner(shScript);
            int exitCode = shRunner.run();

            logger.info("Creating db objects for customer: {}, exitCode: {}", custName, exitCode);
        } catch (IOException | InterruptedException e) {
            logger.error("Exception caught during creating db objects for customer: {}", custName, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Create init db data
     * - admin USER, CONTACT
     * - CONFIGURATION
     * @param dbCred
     * @param dbInitData
     */
    private void createDbData(DBCredentials dbCred, DBInitData dbInitData, String dbScriptSrcFolder) {
        String custName = dbInitData.getConfigOrgName();
        try {
            logger.info("Creating db data for customer: {}, script: {}", custName);

            ShellScriptVO shScript = new ShellScriptVO(dbScriptSrcFolder, DB_SCRIPT_CREATE_DATA);
            shScript.addArg(dbCred.getUsername());
            shScript.addArg(dbCred.getPassword());
            shScript.addArg(dbCred.getSchema());
            shScript.addArg(dbInitData.getAdmUsername());
            shScript.addArg(dbInitData.getAdmPassword());
            shScript.addArg(dbInitData.getAdmContactFirstname());
            shScript.addArg(dbInitData.getAdmContactSurname());
            shScript.addArg(dbInitData.getAdmContactEmail());
            shScript.addArg(dbInitData.getAdmContactPhone());
            shScript.addArg(dbInitData.getConfigCaYear());
            shScript.addArg(dbInitData.getConfigOrgName());
            shScript.addArg(dbInitData.getConfigOrgPhone());
            shScript.addArg(dbInitData.getConfigOrgEmail());
            shScript.addArg(dbInitData.getConfigWelcomeInfo());
            shScript.addArg(dbInitData.getConfigBaseUrl());
            shScript.addArg(dbInitData.getConfigContactPerson());
            shScript.addArg(dbInitData.getConfigSmtpUser());
            shScript.addArg(dbInitData.getConfigSmptPwd());

            ShellScriptRunner shRunner = new ShellScriptRunner(shScript);
            int exitCode = shRunner.run();

            logger.info("Creating db data for customer: {}, exitCode: {}", custName, exitCode);
        } catch (IOException | InterruptedException e) {
            logger.error("Exception caught during creating db data for customer: {}", custName, e);
            throw new RuntimeException(e);
        }
    }
}