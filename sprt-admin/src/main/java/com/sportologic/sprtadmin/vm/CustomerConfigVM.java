package com.sportologic.sprtadmin.vm;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.repository.CustomerConfigRepository;
import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.utils.shell.ShellScriptRunner;
import com.sportologic.sprtadmin.utils.shell.ShellScriptVO;
import com.sportologic.sprtadmin.utils.shell.StreamGobbler;
import com.sportologic.sprtadmin.utils.PasswordGenerator;
import com.sportologic.sprtadmin.utils.StringUtils;
import com.sportologic.sprtadmin.validator.UniqueCustomerValidator;
import com.sportologic.sprtadmin.vo.DBCredentials;
import com.sportologic.sprtadmin.vo.DBInitData;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Executors;

// TODO:
// - validace - delka nazvu
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CustomerConfigVM {

    private static final Logger logger = LoggerFactory.getLogger(CustomerConfigVM.class);

    private static final String DB_SCRIPT_CREATE_OBJECTS = "002-init-cust-db-objects.sh";
    private static final String DB_SCRIPT_CREATE_DATA = "003-init-cust-db-data.sh";

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
    private DBInitData dbInitData;

    @Init
    public void init() {
        customerConfigList = customerConfigRepository.findAllCustom();
        dbBaseUrl = configService.getDbBaseUrl();
        dbScriptSrcFolder = configService.getDbScriptSrcFolder();
        uniqueCustomerValidator = new UniqueCustomerValidator(customerConfigRepository);
        initDbInitFakeData();
    }

    @Command
    public void createCustConfigCmd() {
        CustomerConfig custConfig = new CustomerConfig();
        String customerId = StringUtils.buildCustId(custName);
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
        customerConfigRepository.save(custConfig);
        customerConfigRepository.create_db_user(custConfig.getCustId(),
                                                custConfig.getDbUser(),
                                                custConfig.getDbPassword());

        DBCredentials dbCred = new DBCredentials(custConfig.getDbUser(),
                                                custConfig.getDbPassword(),
                                                custConfig.getCustId());

        createDbObjects(dbCred);

        dbInitData.setConfigOrgName(custName);
        dbInitData.setConfigWelcomeInfo("Vítejte na stránkách klubu " + custName);
        dbInitData.setConfigBaseUrl("https://localhost:8080/" + customerId);
        createDbData(dbCred, dbInitData);

        // call sportologic app to reload customer DS config to add new instance
        reloadCustDSConfig();
    }

    /**
     * REST call sportologic app to reload customer DS configuration.
     * Therefore is new instance added on the fly.
     */
    private void reloadCustDSConfig() {
        restTemplate.exchange("http://localhost:7002/api/cust-ds-config-reload.zul", HttpMethod.GET, null, String.class);
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

    private void initDbInitFakeData() {
        dbInitData = new DBInitData();
        dbInitData.setAdmUsername("a.kuder");
        dbInitData.setAdmPassword("popov");
        dbInitData.setAdmContactFirstname("Adrian");
        dbInitData.setAdmContactSurname("Kuder");
        dbInitData.setAdmContactEmail("a.kuder@seznam.cz");
        dbInitData.setAdmContactPhone("602001002");
        dbInitData.setConfigCaYear("2022/2023");
        dbInitData.setConfigOrgPhone("602001002");
        dbInitData.setConfigOrgEmail("klubmail@seznam.cz");
        dbInitData.setConfigContactPerson("Adrian");
        dbInitData.setConfigCourseApplicationTitle("Přihláška");
        dbInitData.setConfigSmtpUser("klub@sportologic.cz");
        dbInitData.setConfigSmptPwd("heslo");
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

    public DBInitData getDbInitData() {
        return dbInitData;
    }

    public void setDbInitData(DBInitData dbInitData) {
        this.dbInitData = dbInitData;
    }
}
