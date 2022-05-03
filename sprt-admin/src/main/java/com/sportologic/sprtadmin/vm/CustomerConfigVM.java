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
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.impl.ValidationMessagesImpl;
import org.zkoss.util.resource.Labels;
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

    @WireVariable
    private ConfigService configService;

    @WireVariable
    private CustomerConfigService customerConfigService;

    // validators
    private CustomerNameValidator customerNameValidator;
    private NotEmptyStringValidator notEmptyStringValidator;
    private EmailValidator emailValidator;

    private String dbBaseUrl;
    private DBInitData custInitData;
    private boolean finishMode;
    private String confirmInstanceCreatedMsg;
    private String confirmEmailMsg;
    private String confirmNewInstanceUrl;

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

        // customerConfigService.createCustomerInstance(custConfig, custInitData);
        postInstanceCreated(custConfig);
    }

    private void postInstanceCreated(CustomerConfig custConfig) {
        confirmInstanceCreatedMsg = Labels.getLabel("sprt.web.new-instance.confirm.instanceCreated");
        confirmEmailMsg = Labels.getLabel("sprt.web.new-instance.confirm.email", new Object[] {custInitData.getConfigOrgEmail()});
        confirmNewInstanceUrl = configService.getSprtBaseUrl()+custConfig.getCustId();
        finishMode = true;
        BindUtils.postNotifyChange(null, null, this, "confirmInstanceCreatedMsg");
        BindUtils.postNotifyChange(null, null, this, "confirmEmailMsg");
        BindUtils.postNotifyChange(null, null, this, "confirmNewInstanceUrl");
        BindUtils.postNotifyChange(null, null, this, "custInitData");
        BindUtils.postNotifyChange(null, null, this, "finishMode");
    }

    public String getRecaptchaSiteKey() {
        return configService.getRecaptchaSiteKey();
    }

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

    public boolean isFinishMode() {
        return finishMode;
    }

    public String getConfirmInstanceCreatedMsg() {
        return confirmInstanceCreatedMsg;
    }

    public String getConfirmEmailMsg() {
        return confirmEmailMsg;
    }

    public String getConfirmNewInstanceUrl() {
        return confirmNewInstanceUrl;
    }
}