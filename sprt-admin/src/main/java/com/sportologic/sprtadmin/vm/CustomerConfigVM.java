package com.sportologic.sprtadmin.vm;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.repository.CustomerConfigRepository;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

// TODO:
// - custId - odebrat diakritiku, mezery a velka pismena
// - validace - unikatnost nazvu klubu
//            - delka nazvu
// - dbPassword - prověrit pravidla pro heslo
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CustomerConfigVM {

    @WireVariable
    private CustomerConfigRepository customerConfigRepository;

    private List<CustomerConfig> customerConfigList;
    private String custName;

    @Init
    public void init() {
        // customerConfigList = customerConfigRepository.findAll();
        customerConfigList = new ArrayList<>();
        // customerConfigList = customerConfigRepository.findAll();
        customerConfigList = customerConfigRepository.findAllCustom();
    }

    @Command
    public void createCustConfigCmd() {
        CustomerConfig custConfig = new CustomerConfig();
        String custNameLowerCase = custName.toLowerCase(Locale.ROOT);
        custConfig.setUuid(UUID.randomUUID());
        custConfig.setCustId(custNameLowerCase);
        custConfig.setCustDefault(false);
        custConfig.setCustName(custName);
        custConfig.setDbUrl("jdbc:mysql://localhost:3306/"+custNameLowerCase);
        custConfig.setDbUser(custNameLowerCase);
        custConfig.setDbPassword(generatingRandomAlphanumStr());
        custConfig.setModifAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        custConfig.setModifBy("SYSTEM");

        customerConfigRepository.save(custConfig);

        customerConfigRepository.create_db(custNameLowerCase);
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

    public String generatingRandomAlphanumStr() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}
