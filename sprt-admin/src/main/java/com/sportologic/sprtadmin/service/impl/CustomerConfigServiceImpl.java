package com.sportologic.sprtadmin.service.impl;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.exception.SprtValidException;
import com.sportologic.sprtadmin.repository.CustomerConfigRepository;
import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.service.CustomerConfigService;
import com.sportologic.sprtadmin.service.VasServerRestApiClientService;
import com.sportologic.sprtadmin.utils.PasswordGenerator;
import com.sportologic.sprtadmin.utils.SprtAdminUtils;
import com.sportologic.sprtadmin.utils.shell.ShellScriptRunner;
import com.sportologic.sprtadmin.utils.shell.ShellScriptVO;
import com.sportologic.sprtadmin.vo.DBCredentials;
import com.sportologic.sprtadmin.vo.DBInitData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service("customerConfigService")
public class CustomerConfigServiceImpl implements CustomerConfigService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerConfigServiceImpl.class);

    private static final String DB_SCRIPT_CREATE_OBJECTS = "002-init-cust-db-objects.sh";
    private static final String DB_SCRIPT_CREATE_DATA = "003-init-cust-db-data.sh";
    private static final String MODIF_BY_SYSTEM = "SYSTEM";

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

    @Autowired
    private MessageSource messageSource;

    @Override
    public List<CustomerConfig> getCustConfigAll() {
        return customerConfigRepository.findAllCustom();
    }

    @Override
    public void validateUniqueCustName(String custName, Locale locale) throws SprtValidException {
        if (!StringUtils.hasText(custName)) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Checking unique customer for name: {}", custName);
        }

        CustomerConfig custConfig = customerConfigRepository.findByCustName(custName);
        SprtValidException notUniqueCustExc
                = new SprtValidException(messageSource.getMessage("sprt.adm.msg.warn.NotUniqueCustName", new Object[]{custName}, locale));
        if (custConfig != null) {
            // not unique customer name
            logger.warn("Not unique customer name: {}", custName);
            throw notUniqueCustExc;
        }

        String custId = SprtAdminUtils.normToLowerCaseWithoutCZChars(custName);
        custConfig = customerConfigRepository.findByCustId(custId);
        if (custConfig != null) {
            // not unique customer id
            logger.warn("Not unique customer id: {} for customer name: {}", custId, custName);
            throw notUniqueCustExc;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Customer for name: {} is unique.", custName);
        }
    }

    @Override
    public DBInitData createCustomerInstance(DBInitData dbInitData, Locale locale) throws SprtValidException {
        String custName = dbInitData.getConfigOrgName();

        // validation unique customer name, throws SprtValidationException
        validateUniqueCustName(custName, locale);

        try {
            logger.info("Creating new instance for customer: {}", dbInitData.getConfigOrgName());

            // build adminUsername
            String admUsernameFirstname = SprtAdminUtils.normToLowerCaseWithoutCZChars(dbInitData.getAdmContactFirstname());
            String admUsernameSurname = SprtAdminUtils.normToLowerCaseWithoutCZChars(dbInitData.getAdmContactSurname());
            dbInitData.setAdmUsername(admUsernameFirstname + "." + admUsernameSurname);

            // generate password of 6 letters, 2 lowercase, 2 upper case, 1 digit and 1 special char
            dbInitData.setAdmPassword(PasswordGenerator.generateSimple());

            // build course application year
            int yearCurr = LocalDate.now().getYear();
            int yearNext = yearCurr + 1;
            dbInitData.setConfigCaYear(yearCurr + "/" + yearNext);

            // build contact person
            dbInitData.setConfigContactPerson(dbInitData.getAdmContactFirstname() + " " + dbInitData.getAdmContactSurname());

            String customerId = SprtAdminUtils.normToLowerCaseWithoutCZChars(custName);
            CustomerConfig custConfig = new CustomerConfig();
            custConfig.setUuid(UUID.randomUUID());
            custConfig.setCustId(customerId);
            custConfig.setCustDefault(true);
            custConfig.setCustName(custName);
            custConfig.setDbUrl(configService.getDbBaseUrl() + customerId);
            custConfig.setDbUser(customerId);
            custConfig.setDbPassword(PasswordGenerator.generate());
            custConfig.setModifAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            custConfig.setModifBy(MODIF_BY_SYSTEM);

            // Run scripts
            String dbScriptSrcFolder = configService.getDbScriptSrcFolder();
            String sprtBaseUrl = configService.getSprtBaseUrl();

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

        return dbInitData;
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