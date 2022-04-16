package com.sportologic.sprtadmin.vm;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.repository.CustomerConfigRepository;
import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.utils.shell.ShellScriptRunner;
import com.sportologic.sprtadmin.utils.shell.ShellScriptVO;
import com.sportologic.sprtadmin.utils.PasswordGenerator;
import com.sportologic.sprtadmin.utils.SprtAdminUtils;
import com.sportologic.sprtadmin.validator.UniqueCustomerValidator;
import com.sportologic.sprtadmin.vo.DBCredentials;
import com.sportologic.sprtadmin.vo.DBInitData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

// TODO:
// - validace - delka nazvu
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CustomerConfigVM {

    private static final Logger logger = LoggerFactory.getLogger(CustomerConfigVM.class);

    private static final String DB_SCRIPT_CREATE_OBJECTS = "002-init-cust-db-objects.sh";
    private static final String DB_SCRIPT_CREATE_DATA = "003-init-cust-db-data.sh";
    private static final String SPRT_CUST_DS_RELOAD_URI = "api/cust-ds-config-reload.zul";

    @WireVariable
    private CustomerConfigRepository customerConfigRepository;

    @WireVariable
    private ConfigService configService;

    @WireVariable
    private RestTemplate restTemplate;

    private UniqueCustomerValidator uniqueCustomerValidator;

    private List<CustomerConfig> customerConfigList;
    private String custName;
    private String dbBaseUrl;
    private String dbScriptSrcFolder;
    private String sprtBaseUrl;
    private String sprtBaseHttpUrl;

    @Init
    public void init() {
        customerConfigList = customerConfigRepository.findAllCustom();

        // config consts
        dbBaseUrl = configService.getDbBaseUrl();
        dbScriptSrcFolder = configService.getDbScriptSrcFolder();
        sprtBaseUrl = configService.getSprtBaseUrl();
        sprtBaseHttpUrl = configService.getSprtBaseHttpUrl();

        // validators
        uniqueCustomerValidator = new UniqueCustomerValidator(customerConfigRepository);
    }

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

        // Creating DB objects
        createDbObjects(dbCred);

        // Creating Init DB data
        // TODO: load real data
        DBInitData dbInitData = initDbInitFakeData();
        dbInitData.setConfigOrgName(custName);
        dbInitData.setConfigWelcomeInfo("Vítejte na stránkách klubu " + custName);
        dbInitData.setConfigBaseUrl(sprtBaseUrl + customerId);
        createDbData(dbCred, dbInitData);

        // Calling of sportologic app to reload customer DS config to add new instance.
        reloadCustDSConfig();
    }

    /**
     * REST call sportologic app to reload customer DS configuration.
     * Therefore is new instance added on the fly.
     */
    private void reloadCustDSConfig() {
        logger.info("Reloading customer DS config via url: {}", sprtBaseHttpUrl + SPRT_CUST_DS_RELOAD_URI);
        restTemplate.exchange(sprtBaseHttpUrl + SPRT_CUST_DS_RELOAD_URI, HttpMethod.GET, null, String.class);
    }

    /**
     * Create DB objects.
     * @param dbCred
     */
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

    /**
     * Create init db data
     * - admin USER, CONTACT
     * - CONFIGURATION
     * @param dbCred
     * @param dbInitData
     */
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

    private DBInitData initDbInitFakeData() {
        DBInitData dbInitData = new DBInitData();
        dbInitData.setAdmUsername("a.kuder");
        dbInitData.setAdmPassword("popov");
        dbInitData.setAdmContactFirstname("Adrian");
        dbInitData.setAdmContactSurname("Kuder");
        dbInitData.setAdmContactEmail("jakub.zaoralek@gmail.com");
        dbInitData.setAdmContactPhone("602001002");
        dbInitData.setConfigCaYear("2022/2023");
        dbInitData.setConfigOrgPhone("602001002");
        dbInitData.setConfigOrgEmail("jakub.zaoralek@gmail.com");
        dbInitData.setConfigContactPerson("Adrian");
        dbInitData.setConfigCourseApplicationTitle("Přihláška");
        dbInitData.setConfigSmtpUser("testuser002@sportologic.cz");
        dbInitData.setConfigSmptPwd("SprtTestUser001*");

        return dbInitData;
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

    public UniqueCustomerValidator getUniqueCustomerValidator() {
        return uniqueCustomerValidator;
    }
}