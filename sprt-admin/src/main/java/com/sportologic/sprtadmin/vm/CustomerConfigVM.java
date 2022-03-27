package com.sportologic.sprtadmin.vm;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.repository.CustomerConfigRepository;
import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.utils.StreamGobbler;
import com.sportologic.sprtadmin.utils.shell.PasswordGenerator;
import com.sportologic.sprtadmin.utils.StringUtils;
import com.sportologic.sprtadmin.validator.UniqueCustomerValidator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
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
// Zalozeni DB objektu
// - DB_SCRIPT_FOLDER z konfigurace
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CustomerConfigVM {

    private static final Logger logger = LoggerFactory.getLogger(CustomerConfigVM.class);

    private static final String DB_SCRIPT_SRC_FOLDER = "/Users/jakub.zaoralek/develLab/sources/Swimming-Club-Bohumin/sprt-admin/src/main/resources/db/migration/common/";
    private static final String DB_SCRIPT_CREATE_OBJECTS = "002-init-cust-db-objects.sh";
    private static final String DB_SCRIPT_CREATE_DATA = "003-init-cust-db-data.sh";

    @WireVariable
    private CustomerConfigRepository customerConfigRepository;

    @WireVariable
    private ConfigService configService;

    private UniqueCustomerValidator uniqueCustomerValidator;

    private List<CustomerConfig> customerConfigList;
    private String custName;
    private String dbBaseUrl;

    @Init
    public void init() {
        customerConfigList = customerConfigRepository.findAllCustom();
        dbBaseUrl = configService.getDbBaseUrl();
        uniqueCustomerValidator = new UniqueCustomerValidator(customerConfigRepository);
        System.out.println(getCustTablesInitSql("klub4"));
    }

    @Command
    public void createCustConfigCmd() {
        CustomerConfig custConfig = new CustomerConfig();
        String customerId = StringUtils.buildCustId(custName);
        custConfig.setUuid(UUID.randomUUID());
        custConfig.setCustId(customerId);
        custConfig.setCustDefault(false);
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

        createDbObjects(custConfig.getDbUser(), custConfig.getDbPassword(), custConfig.getCustId());
        createDbData(custConfig.getDbUser(), custConfig.getDbPassword(), custConfig.getCustId());
        // System.out.println(getCustTablesInitSql("klub4"));
    }

    public void createDbObjects(String user, String password, String schema) {
        try {
            StringBuilder cmdSb = new StringBuilder(DB_SCRIPT_SRC_FOLDER);
            cmdSb.append(DB_SCRIPT_CREATE_OBJECTS);
            cmdSb.append(" " + user + " " + password + " " + schema);
            cmdSb.append(" " + DB_SCRIPT_SRC_FOLDER);

            String cmd = cmdSb.toString();
            logger.info("Creating db objects for customer: {}, script: {}", custName, cmd);
            Process process = Runtime.getRuntime().exec(cmd);

            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = 0;
            exitCode = process.waitFor();
            assert exitCode == 0;
            logger.info("Creating db objects for customer: {}, exitCode: {}", custName, exitCode);
        } catch (IOException | InterruptedException e) {
            logger.error("Exception caught during creating db objects for customer: {}", custName, e);
            throw new RuntimeException(e);
        }
    }

    public void createDbData(String user, String password, String schema) {
        try {
            StringBuilder cmdSb = new StringBuilder(DB_SCRIPT_SRC_FOLDER);
            cmdSb.append(DB_SCRIPT_CREATE_DATA);
            cmdSb.append(" " + user + " " + password + " " + schema);
            cmdSb.append(" " + DB_SCRIPT_SRC_FOLDER);

            String cmd = cmdSb.toString();
            logger.info("Creating db data for customer: {}, script: {}", custName, cmd);
            Process process = Runtime.getRuntime().exec(cmd);

            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = 0;
            exitCode = process.waitFor();
            assert exitCode == 0;
            logger.info("Creating db data for customer: {}, exitCode: {}", custName, exitCode);
        } catch (IOException | InterruptedException e) {
            logger.error("Exception caught during creating db data for customer: {}", custName, e);
            throw new RuntimeException(e);
        }
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

    /**
     * Get init SQL customer tables.
     * @return
     */
    private String getCustTablesInitSql(String dbName) {
        String ret = null;
        try {
            File file = ResourceUtils.getFile("classpath:db/migration/common/V1.0.0__init-cust-db-objects.sql");
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8.toString());
            ret = content.replaceAll("##dbName##", dbName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
