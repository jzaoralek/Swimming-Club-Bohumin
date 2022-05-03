package com.sportologic.sprtadmin.vm;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.service.CustomerConfigService;
import com.sportologic.sprtadmin.utils.PasswordGenerator;
import com.sportologic.sprtadmin.utils.SprtAdminUtils;
import com.sportologic.sprtadmin.validator.EmailValidator;
import com.sportologic.sprtadmin.validator.CustomerNameValidator;
import com.sportologic.sprtadmin.validator.NotEmptyStringValidator;
import com.sportologic.sprtadmin.vo.DBInitData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.impl.ValidationMessagesImpl;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CustomerConfigVM {

    private static final String MODIF_BY_SYSTEM = "SYSTEM";
    private static final Logger logger = LoggerFactory.getLogger(CustomerConfigVM.class);

    /*
    private static final String DB_SCRIPT_CREATE_OBJECTS = "002-init-cust-db-objects.sh";
    private static final String DB_SCRIPT_CREATE_DATA = "003-init-cust-db-data.sh";
    private static final String SPRT_CUST_DS_RELOAD_URI = "api/cust-ds-config-reload.zul";
    private static final String SPRT_DOMAIN = "sportologic.cz";
    */

    @WireVariable
    private ConfigService configService;

    @WireVariable
    private CustomerConfigService customerConfigService;

    private CustomerNameValidator customerNameValidator;
    private NotEmptyStringValidator notEmptyStringValidator;
    private EmailValidator emailValidator;

    // private List<CustomerConfig> customerConfigList;
    private String dbBaseUrl;
    private DBInitData custInitData;
    /*
    private String dbScriptSrcFolder;
    private String sprtBaseUrl;
    private String sprtBaseHttpUrl;
    */

    @Init
    public void init() {
        // customerConfigList = customerConfigService.getCustConfigAll();
        custInitData = new DBInitData();
        // config consts
        dbBaseUrl = configService.getDbBaseUrl();
        // validators
        customerNameValidator = new CustomerNameValidator(customerConfigService);
        notEmptyStringValidator = new NotEmptyStringValidator();
        emailValidator = new EmailValidator();
        /*
        dbScriptSrcFolder = configService.getDbScriptSrcFolder();
        sprtBaseUrl = configService.getSprtBaseUrl();
        sprtBaseHttpUrl = configService.getSprtBaseHttpUrl();
        */
    }

    @Command
    public void createCustConfigCmd() {
        // build adminUsername
        String admUsernameFirstname = SprtAdminUtils.normToLowerCaseWithoutCZChars(custInitData.getAdmContactFirstname());
        String admUsernameSurname = SprtAdminUtils.normToLowerCaseWithoutCZChars(custInitData.getAdmContactSurname());
        custInitData.setAdmUsername(admUsernameFirstname + "." + admUsernameSurname);

        // generate password of 6 letters, 2 lowercase, 2 upper case, 1 digit and 1 special char
        custInitData.setAdmPassword(PasswordGenerator.generateSimple());

        // build course application year
        int yearCurr = LocalDate.now().getYear();
        int yearNext = yearCurr + 1;
        custInitData.setConfigCaYear(yearCurr + "/" + yearNext);

        // build contact person
        custInitData.setConfigContactPerson(custInitData.getAdmContactFirstname() + " " + custInitData.getAdmContactSurname());

        CustomerConfig custConfig = new CustomerConfig();
        String custName = custInitData.getConfigOrgName();
        String customerId = SprtAdminUtils.normToLowerCaseWithoutCZChars(custName);
        custConfig.setUuid(UUID.randomUUID());
        custConfig.setCustId(customerId);
        custConfig.setCustDefault(true);
        custConfig.setCustName(custName);
        custConfig.setDbUrl(dbBaseUrl + customerId);
        custConfig.setDbUser(customerId);
        custConfig.setDbPassword(PasswordGenerator.generate());
        custConfig.setModifAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        custConfig.setModifBy(MODIF_BY_SYSTEM);

        System.out.println(custInitData);
        // customerConfigService.createCustomerInstance(custConfig, custInitData);
    }

    public String getRecaptchaSiteKey() {
        return configService.getRecaptchaSiteKey();
    }

    /*
    @Command
    public void createCustConfigCmd() {
        CustomerConfig custConfig = new CustomerConfig();
        String customerId = SprtAdminUtils.buildCustId(custName);
        custConfig.setUuid(UUID.randomUUID());
        custConfig.setCustId(customerId);
        custConfig.setCustDefault(true);
        custConfig.setCustName(custName);
        custConfig.setDbUrl(dbBaseUrl + customerId);
        custConfig.setDbUser(customerId);
        custConfig.setDbPassword(PasswordGenerator.generate());
        custConfig.setModifAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        custConfig.setModifBy("SYSTEM");

        logger.info("Creating new customer: {}", custName);

        // Creating new customer configuration
        customerConfigRepository.save(custConfig);

        DBCredentials dbCred = new DBCredentials(custConfig.getDbUser(),
                                                custConfig.getDbPassword(),
                                                custConfig.getCustId());

        // Creating new DB schema and user
        customerConfigRepository.create_db_user(dbCred.getSchema(),
                                                dbCred.getUsername(),
                                                dbCred.getPassword());

        // TODO: load real data
        DBInitData dbInitData = initDbInitFakeData();

        // Creating customer email account
        // String smtpPwd = "Sportologic1234*"; // Couldn't connect to host, port: pio12.vas-server.cz, 465; timeout -1
        String smtpPwd =  PasswordGenerator.generate();
        createEmail(customerId, smtpPwd);
        String smtpUser = customerId + "@" + SPRT_DOMAIN;
        dbInitData.setConfigSmtpUser(smtpUser);
        dbInitData.setConfigSmptPwd(smtpPwd);

        // Creating DB objects
        createDbObjects(dbCred);

        // Creating Init DB data
        dbInitData.setConfigOrgName(custName);
        dbInitData.setConfigWelcomeInfo("Vítejte na stránkách klubu " + custName);
        dbInitData.setConfigBaseUrl(sprtBaseUrl + customerId);
        createDbData(dbCred, dbInitData);

        // Calling sportologic app to reload customer DS config to add new instance.
        reloadCustDSConfig();
    }

    private void createEmail(String username, String password) {
        try {
            logger.info("Creating new email: {}", username + "@" + SPRT_DOMAIN);
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl("no-cache");
            headers.set("x-vpsc-apikey", configService.getVpscApiKey());
            headers.set("x-vpsc-admin", configService.getVpscAdmin());

            RestEmailAdd emailAdd = new RestEmailAdd("email-add", SPRT_DOMAIN, username, password, username, "");

            HttpEntity<RestEmailAdd> entity = new HttpEntity<RestEmailAdd>(emailAdd, headers);

            ResponseEntity responseEntity = restTemplate.exchange("https://pio12.vas-server.cz/admin/api/v1/api.php?email-add", HttpMethod.POST, entity, String.class);

            logger.info("Creating new email response status code: {}", responseEntity.getStatusCode());
            logger.info("New email {} successfully created.", responseEntity.getBody());
        } catch (Exception e) {
            logger.error("Exception caught during creating new email: {}", username + "@" + SPRT_DOMAIN, e);
            throw new RuntimeException(e);
        }
    }

    private void reloadCustDSConfig() {
        logger.info("Reloading customer DS config via url: {}", sprtBaseHttpUrl + SPRT_CUST_DS_RELOAD_URI);
        restTemplate.exchange(sprtBaseHttpUrl + SPRT_CUST_DS_RELOAD_URI, HttpMethod.GET, null, String.class);
    }

    private void createDbObjects(DBCredentials dbCred) {
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

    private void createDbData(DBCredentials dbCred, DBInitData dbInitData) {
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
            shScript.addArg(dbInitData.getConfigCourseApplicationTitle());
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
    */

    /*
    public List<CustomerConfig> getCustomerConfigList() {
        return customerConfigList;
    }
    */

    public CustomerNameValidator getCustomerNameValidator() {
        return customerNameValidator;
    }

    public NotEmptyStringValidator getNotEmptyStringValidator() {
        return notEmptyStringValidator;
    }

    public EmailValidator getEmailValidator() {
        return emailValidator;
    }

    public DBInitData getCustInitData() {
        return custInitData;
    }

    public void setCustInitData(DBInitData custInitData) {
        this.custInitData = custInitData;
    }
}